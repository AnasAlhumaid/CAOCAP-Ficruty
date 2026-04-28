import Foundation

@MainActor
public protocol CoCaptainLLMClient: AnyObject {
    func resetChat()
    func streamResponse(
        for userMessage: String,
        context: String?,
        expectsStructuredResponse: Bool,
        availableActions: [AppActionDefinition]
    ) -> AsyncThrowingStream<String, Error>
}

extension LLMService: CoCaptainLLMClient {}

public struct CoCaptainAgentRunResult: Hashable {
    public let visibleText: String
    public let executionSummary: ExecutionStatusItem?
    public let reviewBundle: ReviewBundleItem?
}

@MainActor
public final class CoCaptainAgentCoordinator {
    private let llmClient: any CoCaptainLLMClient
    private let contextBuilder: ProjectContextBuilder
    private let patchEngine: NodePatchEngine
    private let parser: CoCaptainAgentParser

    public init(
        llmClient: (any CoCaptainLLMClient)? = nil,
        contextBuilder: ProjectContextBuilder = ProjectContextBuilder(),
        patchEngine: NodePatchEngine = NodePatchEngine(),
        parser: CoCaptainAgentParser = CoCaptainAgentParser()
    ) {
        self.llmClient = llmClient ?? LLMService.shared
        self.contextBuilder = contextBuilder
        self.patchEngine = patchEngine
        self.parser = parser
    }

    public func resetChat() {
        llmClient.resetChat()
    }

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
        let stream = llmClient.streamResponse(
            for: userMessage,
            context: context,
            expectsStructuredResponse: expectsStructuredResponse,
            availableActions: dispatcher?.availableActions ?? []
        )

        for try await chunk in stream {
            responseText += chunk
            onVisibleText(parser.visibleText(from: responseText))
        }

        let parsed = parser.parse(responseText)
        let payload = expectsStructuredResponse ? parsed.payload : nil

        if expectsStructuredResponse,
           payload == nil,
           allowAgenticRetry,
           shouldRequireAgenticWork(for: userMessage) {
            return try await runOnce(
                userMessage: agenticRetryMessage(for: userMessage),
                context: context,
                expectsStructuredResponse: true,
                store: store,
                dispatcher: dispatcher,
                onVisibleText: onVisibleText,
                allowAgenticRetry: false
            )
        }

        let executionSummary = executeSafeActions(payload?.safeActions ?? [], dispatcher: dispatcher)
        let reviewBundle = buildReviewBundle(
            pendingActions: payload?.pendingActions ?? [],
            nodeEdits: payload?.nodeEdits ?? [],
            store: store,
            dispatcher: dispatcher
        )

        return CoCaptainAgentRunResult(
            visibleText: parsed.visibleText,
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
            "game"
        ]

        return triggers.contains { lowercased.contains($0) }
    }

    private func agenticRetryMessage(for userMessage: String) -> String {
        """
        The previous response did not include executable Co-Captain work.

        Retry this user request and include a `cocaptain-actions` fenced block with concrete nodeEdits. For full builds, games, or demos, use `replace_all` operations for the html, css, and javascript nodes.

        Original user request:
        \(userMessage)
        """
    }

    private func executeSafeActions(
        _ actions: [CoCaptainAgentAction],
        dispatcher: (any AppActionPerforming)?
    ) -> ExecutionStatusItem? {
        guard let dispatcher, !actions.isEmpty else { return nil }

        let executedSummaries = actions.compactMap { action -> String? in
            guard let id = AppActionID(rawValue: action.actionID) else { return nil }
            let result = dispatcher.perform(id, source: .agentAutomatic)
            return result.executed ? result.title : nil
        }

        guard !executedSummaries.isEmpty else { return nil }
        return ExecutionStatusItem(
            summary: LocalizationManager.shared.localizedString(
                "Executed: %@",
                arguments: [executedSummaries.joined(separator: ", ")]
            )
        )
    }

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
                    preview: definition.localizedTitle,
                    source: .appAction(id)
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
