import Foundation

@MainActor
public protocol CoCaptainLLMClient: AnyObject {
    func resetChat()
    func streamAgentEvents(
        for userMessage: String,
        context: String?,
        expectsStructuredResponse: Bool,
        availableActions: [AppActionDefinition]
    ) -> AsyncThrowingStream<CoCaptainLLMStreamEvent, Error>
}

extension LLMService: CoCaptainLLMClient {}

public struct CoCaptainAgentRunResult: Hashable {
    public let preamble: String
    public let payloadMessage: String?
    public let executionSummary: ExecutionStatusItem?
    public let reviewBundle: ReviewBundleItem?

    public var visibleText: String {
        if preamble.isEmpty { return payloadMessage ?? "" }
        return preamble
    }
}

/// Bridges model output to app behavior while keeping mutating code edits in
/// an explicit review flow.
@MainActor
public final class CoCaptainAgentCoordinator {
    private let llmClient: any CoCaptainLLMClient
    private let contextBuilder: ProjectContextBuilder
    private let patchEngine: NodePatchEngine
    private let outputAdapter: any CoCaptainAgentOutputAdapting
    private let validator: CoCaptainAgentValidator

    public init(
        llmClient: (any CoCaptainLLMClient)? = nil,
        contextBuilder: ProjectContextBuilder = ProjectContextBuilder(),
        patchEngine: NodePatchEngine = NodePatchEngine(),
        parser: CoCaptainAgentParser = CoCaptainAgentParser(),
        outputAdapter: (any CoCaptainAgentOutputAdapting)? = nil,
        validator: CoCaptainAgentValidator = CoCaptainAgentValidator()
    ) {
        self.llmClient = llmClient ?? LLMService.shared
        self.contextBuilder = contextBuilder
        self.patchEngine = patchEngine
        self.outputAdapter = outputAdapter ?? CoCaptainCompositeAgentAdapter(
            xmlAdapter: CoCaptainXMLAgentAdapter(parser: parser)
        )
        self.validator = validator
    }

    public func resetChat() {
        llmClient.resetChat()
    }

    /// Runs one assistant turn against the active project context. Structured
    /// responses are preferred so the UI can separate visible chat text from
    /// executable actions and reviewable node edits.
    public func run(
        userMessage: String,
        store: ProjectStore?,
        dispatcher: (any AppActionPerforming)?,
        onVisibleText: @escaping (String) -> Void
    ) async throws -> CoCaptainAgentRunResult {
        let context = store.map { contextBuilder.buildPromptContext(from: $0) }
        do {
            return try await runOnce(
                userMessage: userMessage,
                context: context,
                expectsStructuredResponse: true,
                store: store,
                dispatcher: dispatcher,
                onVisibleText: onVisibleText,
                allowAgenticRetry: true
            )
        } catch {
            // Fallback: if the structured+context prompt fails (often with opaque
            // `GenerateContentError error 0`), retry with a minimal prompt so chat stays usable.
            return try await runOnce(
                userMessage: userMessage,
                context: nil,
                expectsStructuredResponse: false,
                store: store,
                dispatcher: dispatcher,
                onVisibleText: onVisibleText,
                allowAgenticRetry: false
            )
        }
    }

    private func runOnce(
        userMessage: String,
        context: String?,
        expectsStructuredResponse: Bool,
        store: ProjectStore?,
        dispatcher: (any AppActionPerforming)?,
        onVisibleText: @escaping (String) -> Void,
        allowAgenticRetry: Bool
    ) async throws -> CoCaptainAgentRunResult {
        var responseText = ""
        var functionCalls: [CoCaptainAgentFunctionCall] = []
        var seenFunctionCallIDs = Set<String>()

        let stream = llmClient.streamAgentEvents(
            for: userMessage,
            context: context,
            expectsStructuredResponse: expectsStructuredResponse,
            availableActions: dispatcher?.availableActions ?? []
        )

        for try await event in stream {
            switch event {
            case .text(let chunk):
                responseText += chunk
                onVisibleText(outputAdapter.visibleText(from: responseText))
            case .functionCalls(let calls):
                for call in calls where shouldAppend(functionCall: call, seenIDs: &seenFunctionCallIDs) {
                    functionCalls.append(call)
                }
            }
        }

        // The visible chat can stream before the structured block is complete;
        // only parse actions after the model has finished the turn.
        let directive = outputAdapter.directive(from: responseText, functionCalls: functionCalls)
        let payload = expectsStructuredResponse ? directive.payload : nil

        let requiresAgenticWork = shouldRequireAgenticWork(for: userMessage)

        if expectsStructuredResponse {
            if !directive.diagnostics.isEmpty {
                if allowAgenticRetry {
                    return try await runOnce(
                        userMessage: agenticRetryMessage(
                            for: userMessage,
                            validationIssues: directive.diagnostics
                        ),
                        context: context,
                        expectsStructuredResponse: true,
                        store: store,
                        dispatcher: dispatcher,
                        onVisibleText: onVisibleText,
                        allowAgenticRetry: false
                    )
                }

                return CoCaptainAgentRunResult(
                    preamble: directive.preamble,
                    payloadMessage: nil,
                    executionSummary: nil,
                    reviewBundle: validationReviewBundle(issues: directive.diagnostics)
                )
            }

            // Build/edit requests should produce executable work. If the model only
            // chatted back, retry once with a stronger contract before falling back.
            if payload == nil, allowAgenticRetry, requiresAgenticWork {
                return try await runOnce(
                    userMessage: agenticRetryMessage(
                        for: userMessage,
                        validationIssues: directive.diagnostics.isEmpty
                            ? ["Missing machine-readable CoCaptain action directive."]
                            : directive.diagnostics
                    ),
                    context: context,
                    expectsStructuredResponse: true,
                    store: store,
                    dispatcher: dispatcher,
                    onVisibleText: onVisibleText,
                    allowAgenticRetry: false
                )
            }

            if let payload {
                let validation = validator.validate(
                    payload: payload,
                    dispatcher: dispatcher,
                    requiresAgenticWork: requiresAgenticWork
                )

                if !validation.isValid {
                    if allowAgenticRetry {
                        return try await runOnce(
                            userMessage: agenticRetryMessage(
                                for: userMessage,
                                validationIssues: validation.issues
                            ),
                            context: context,
                            expectsStructuredResponse: true,
                            store: store,
                            dispatcher: dispatcher,
                            onVisibleText: onVisibleText,
                            allowAgenticRetry: false
                        )
                    }

                    return CoCaptainAgentRunResult(
                        preamble: directive.preamble,
                        payloadMessage: payload.assistantMessage,
                        executionSummary: nil,
                        reviewBundle: validationReviewBundle(issues: validation.issues)
                    )
                }
            }
        }

        let executionSummary = executeSafeActions(payload?.safeActions ?? [], dispatcher: dispatcher, store: store)
        let reviewBundle = buildReviewBundle(
            pendingActions: payload?.pendingActions ?? [],
            nodeEdits: payload?.nodeEdits ?? [],
            store: store,
            dispatcher: dispatcher
        )

        return CoCaptainAgentRunResult(
            preamble: directive.preamble,
            payloadMessage: payload?.assistantMessage,
            executionSummary: executionSummary,
            reviewBundle: reviewBundle
        )
    }

    private func shouldRequireAgenticWork(for userMessage: String) -> Bool {
        let lowercased = userMessage.lowercased()
        let triggers = [
            "build",
            "make",
            "create",
            "add",
            "change",
            "update",
            "fix",
            "remove",
            "style",
            "implement",
            "improve",
            "game",
            "open",
            "go",
            "show",
            "navigate",
            "settings",
            "home"
        ]

        return triggers.contains { lowercased.contains($0) }
    }

    private func agenticRetryMessage(for userMessage: String, validationIssues: [String]) -> String {
        let issueList = validationIssues.map { "- \($0)" }.joined(separator: "\n")

        return """
        The previous response did not satisfy the machine-readable CoCaptain action contract.

        Validation issues:
        \(issueList)
        
        CRITICAL: 
        1. Do NOT just provide code in markdown chat. 
        2. You MUST include a `cocaptain_actions` XML block.
        3. For app navigation/tool actions, call `request_app_action`.
        4. Put code/content implementation in `nodeEdits`.
        5. Put mutating or non-autonomous app actions in `pendingActions` or call `request_app_action` with `executionMode=pending`.
        6. Use `safeActions` or `executionMode=safe` only for available, non-mutating, autonomous app actions.
        7. For full builds or games, use `replace_all` for html, css, and javascript nodes.
        
        Original user request:
        \(userMessage)
        """
    }

    private func shouldAppend(
        functionCall: CoCaptainAgentFunctionCall,
        seenIDs: inout Set<String>
    ) -> Bool {
        guard let id = functionCall.id else { return true }
        return seenIDs.insert(id).inserted
    }

    private func validationReviewBundle(issues: [String]) -> ReviewBundleItem {
        ReviewBundleItem(
            title: LocalizationManager.shared.localizedString("CoCaptain action needs revision"),
            items: [
                PendingReviewItem(
                    targetLabel: LocalizationManager.shared.localizedString("CoCaptain action contract"),
                    summary: LocalizationManager.shared.localizedString("The assistant response could not be executed safely."),
                    preview: issues.joined(separator: "\n"),
                    status: .conflicted,
                    source: .nodeEdit(role: .srs, operations: [], baseText: "")
                )
            ]
        )
    }

    private func executeSafeActions(
        _ actions: [CoCaptainAgentAction],
        dispatcher: (any AppActionPerforming)?,
        store: ProjectStore?
    ) -> ExecutionStatusItem? {
        guard let dispatcher, !actions.isEmpty else { return nil }

        // Create a checkpoint before executing multiple safe actions to allow revert
        store?.createAutoCheckpoint(label: "Before AI Actions")

        let executedSummaries = actions.compactMap { action -> String? in
            guard let id = AppActionID(rawValue: action.actionID) else { return nil }
            let result = dispatcher.perform(id, source: .agentAutomatic, arguments: action.args)
            return result.executed ? result.title : nil
        }

        guard !executedSummaries.isEmpty else { return nil }
        return ExecutionStatusItem(
            summary: LocalizationManager.shared.localizedString(
                "agent.executedSummary",
                arguments: [executedSummaries.joined(separator: ", ")]
            )
        )
    }

    /// Converts pending actions and node edits into review items. Node edit
    /// previews capture the current text as `baseText` so apply can detect
    /// whether the user changed the node after the model response.
    private func buildReviewBundle(
        pendingActions: [CoCaptainAgentAction],
        nodeEdits: [CoCaptainNodeEditProposal],
        store: ProjectStore?,
        dispatcher: (any AppActionPerforming)?
    ) -> ReviewBundleItem? {
        var items: [PendingReviewItem] = []

        for action in pendingActions {
            guard let id = AppActionID(rawValue: action.actionID),
                  let definition = dispatcher?.definition(for: id) else {
                continue
            }

            items.append(
                PendingReviewItem(
                    targetLabel: definition.localizedTitle,
                    summary: LocalizationManager.shared.localizedString(
                        "Awaiting approval to run %@.",
                        arguments: [definition.localizedTitle]
                    ),
                    preview: action.args?.description ?? definition.localizedTitle,
                    source: .appAction(id, action.args)
                )
            )
        }

        if let store {
            for edit in nodeEdits {
                do {
                    let preview = try patchEngine.preview(role: edit.role, operations: edit.operations, in: store)
                    items.append(
                        PendingReviewItem(
                            targetLabel: edit.role.localizedDisplayName,
                            summary: edit.summary,
                            preview: previewSnippet(for: preview.resultText),
                            source: .nodeEdit(role: edit.role, operations: edit.operations, baseText: preview.originalText)
                        )
                    )
                } catch {
                    items.append(
                        PendingReviewItem(
                            targetLabel: edit.role.localizedDisplayName,
                            summary: edit.summary,
                            preview: error.localizedDescription,
                            status: .conflicted,
                            source: .nodeEdit(role: edit.role, operations: edit.operations, baseText: "")
                        )
                    )
                }
            }
        } else {
            for edit in nodeEdits {
                items.append(
                    PendingReviewItem(
                        targetLabel: edit.role.localizedDisplayName,
                        summary: edit.summary,
                        preview: LocalizationManager.shared.localizedString("No active project context is available for this edit."),
                        status: .conflicted,
                        source: .nodeEdit(role: edit.role, operations: edit.operations, baseText: "")
                    )
                )
            }
        }

        return items.isEmpty ? nil : ReviewBundleItem(items: items)
    }

    private func previewSnippet(for text: String) -> String {
        let trimmed = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard trimmed.count > 280 else { return trimmed }
        return String(trimmed.prefix(280)) + "\n[TRUNCATED]"
    }
}
