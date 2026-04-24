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
                onVisibleText: onVisibleText
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
                onVisibleText: onVisibleText
            )
        }
    }

    private func runOnce(
        userMessage: String,
        context: String?,
        expectsStructuredResponse: Bool,
        store: ProjectStore?,
        dispatcher: (any AppActionPerforming)?,
        onVisibleText: @escaping (String) -> Void
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
        return ExecutionStatusItem(summary: "Executed: " + executedSummaries.joined(separator: ", "))
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
                    targetLabel: definition.title,
                    summary: "Awaiting approval to run \(definition.title.lowercased()).",
                    preview: definition.title,
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
                            targetLabel: edit.role.displayName,
                            summary: edit.summary,
                            preview: previewSnippet(for: preview.resultText),
                            source: .nodeEdit(role: edit.role, operations: edit.operations, baseText: preview.originalText)
                        )
                    )
                } catch {
                    items.append(
                        PendingReviewItem(
                            targetLabel: edit.role.displayName,
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
                        targetLabel: edit.role.displayName,
                        summary: edit.summary,
                        preview: "No active project context is available for this edit.",
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
