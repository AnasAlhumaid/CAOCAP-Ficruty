import Observation
import SwiftUI

@MainActor
@Observable
public final class CoCaptainViewModel {
    public var isPresented: Bool = false
    public var items: [CoCaptainTimelineItem]
    public var store: ProjectStore? {
        didSet {
            handleStoreChange()
        }
    }
    public var analysisItems: [ProjectSuggestion] = []
    
    @ObservationIgnored
    private let analyzer = ProjectAnalyzer()
    @ObservationIgnored
    public var actionDispatcher: (any AppActionPerforming)?

    /// Tracks the ID of the message that was last visible to the user.
    public var lastScrollPosition: UUID?

    @ObservationIgnored
    private let agentCoordinator = CoCaptainAgentCoordinator()
    @ObservationIgnored
    private let commandIntentResolver = CommandIntentResolver()
    @ObservationIgnored
    private let patchEngine = NodePatchEngine()
    @ObservationIgnored
    private var lastStoreFileName: String?
    @ObservationIgnored
    private var streamingTask: Task<Void, Never>?

    public var isThinking: Bool = false
    public var isAwaitingFirstResponse: Bool {
        guard isThinking,
              let lastMessage,
              !lastMessage.isUser else {
            return false
        }
        return lastMessage.text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    public init() {
        self.items = [CoCaptainViewModel.greetingItem()]
    }

    public func clearHistory() {
        items = [CoCaptainViewModel.greetingItem()]
        agentCoordinator.resetChat()
        lastScrollPosition = nil
    }

    public func setPresented(_ presented: Bool) {
        if !presented {
            streamingTask?.cancel()
            streamingTask = nil
            isThinking = false
        }

        withAnimation(.spring(response: 0.3, dampingFraction: 0.8)) {
            isPresented = presented
        }

        if presented {
            runAnalysis()
        }
    }

    public func runAnalysis() {
        guard let nodes = store?.nodes else { return }
        let newSuggestions = analyzer.analyze(nodes: nodes)
        
        // Only update if suggestions have changed to avoid UI flickering
        if newSuggestions != analysisItems {
            withAnimation(.spring()) {
                analysisItems = newSuggestions
            }
        }
    }

    public func dismissSuggestion(_ suggestion: ProjectSuggestion) {
        withAnimation(.spring()) {
            analysisItems.removeAll(where: { $0.id == suggestion.id })
        }
    }

    public func applySuggestion(_ suggestion: ProjectSuggestion) {
        dismissSuggestion(suggestion)
        sendMessage(suggestion.suggestedPrompt)
    }

    public func sendMessage(_ text: String) {
        guard !isThinking else { return }

        let userItem = ChatBubbleItem(text: text, isUser: true)
        items.append(CoCaptainTimelineItem(content: .message(userItem)))

        if handleDirectCommand(text) {
            return
        }

        isThinking = true
        let aiMessageID = UUID()
        items.append(
            CoCaptainTimelineItem(
                id: aiMessageID,
                content: .message(ChatBubbleItem(id: aiMessageID, text: "", isUser: false))
            )
        )

        streamingTask = Task { @MainActor in
            do {
                let result = try await agentCoordinator.run(
                    userMessage: text,
                    store: store,
                    dispatcher: actionDispatcher
                ) { _ in
                    // Stop streaming characters to the UI for a cleaner 'split message' feel.
                }

                // Remove the empty thinking placeholder.
                removeEmptyMessage(id: aiMessageID)

                // 1. Add Preamble bubble (the conversational part).
                if !result.preamble.isEmpty {
                    items.append(CoCaptainTimelineItem(content: .message(ChatBubbleItem(text: result.preamble, isUser: false))))
                }

                // 2. Add Payload Message bubble (the intent summary).
                if let payloadMsg = result.payloadMessage, !payloadMsg.isEmpty, payloadMsg != result.preamble {
                    items.append(CoCaptainTimelineItem(content: .message(ChatBubbleItem(text: payloadMsg, isUser: false))))
                }

                if let executionSummary = result.executionSummary {
                    items.append(CoCaptainTimelineItem(content: .execution(executionSummary)))
                }

                if let reviewBundle = result.reviewBundle {
                    items.append(CoCaptainTimelineItem(content: .reviewBundle(reviewBundle)))
                }
            } catch {
                if error is CancellationError || Task.isCancelled {
                    removeEmptyMessage(id: aiMessageID)
                    return
                }

                let details = String(reflecting: error)
                updateMessage(
                    id: aiMessageID,
                    text: LocalizationManager.shared.localizedString(
                        "Sorry, I hit an error while contacting the model.\n\n%@",
                        arguments: [details]
                    )
                )
            }

            isThinking = false
        }
    }

    public func stopStreaming() {
        streamingTask?.cancel()
        streamingTask = nil
        isThinking = false

        if let lastMessage, !lastMessage.isUser {
            removeEmptyMessage(id: lastMessage.id)
        }
    }

    /// Handles simple app commands locally so navigation does not need a model
    /// round trip. Mutating commands still become review items.
    private func handleDirectCommand(_ text: String) -> Bool {
        guard let actionDispatcher,
              let actionID = commandIntentResolver.resolve(text, availableActions: actionDispatcher.availableActions),
              let definition = actionDispatcher.definition(for: actionID) else {
            return false
        }

        if definition.isMutating || !definition.allowsAutonomousExecution {
            items.append(
                CoCaptainTimelineItem(
                    content: .message(
                        ChatBubbleItem(
                            text: LocalizationManager.shared.localizedString(
                                "I can do that. Review the action below, then tap Apply."
                            ),
                            isUser: false
                        )
                    )
                )
            )
            items.append(
                CoCaptainTimelineItem(
                    content: .reviewBundle(
                        ReviewBundleItem(
                            items: [
                                PendingReviewItem(
                                    targetLabel: definition.localizedTitle,
                                    summary: LocalizationManager.shared.localizedString(
                                        "Awaiting approval to run %@.",
                                        arguments: [definition.localizedTitle]
                                    ),
                                    preview: definition.localizedTitle,
                                    source: .appAction(actionID, nil) // args will be handled in handleDirectCommand if needed
                                )
                            ]
                        )
                    )
                )
            )
            return true
        }

        let result = actionDispatcher.perform(actionID, source: .agentAutomatic, arguments: nil)
        items.append(
            CoCaptainTimelineItem(
                content: .execution(ExecutionStatusItem(summary: result.message))
            )
        )
        return true
    }

    /// Applies one user-approved review item. Node edits are revalidated against
    /// their captured base text so stale AI suggestions cannot overwrite newer
    /// user edits.
    public func applyReviewItem(bundleID: UUID, itemID: UUID) {
        guard let bundleIndex = items.firstIndex(where: { $0.id == bundleID }),
              case .reviewBundle(var bundle) = items[bundleIndex].content,
              let itemIndex = bundle.items.firstIndex(where: { $0.id == itemID }) else {
            return
        }

        var item = bundle.items[itemIndex]
        
        // Create checkpoint before applying a single item
        store?.createCheckpoint(label: "Apply Suggestion: \(item.targetLabel)")

        switch item.source {
        case .appAction(let actionID, let arguments):
            let result = actionDispatcher?.perform(actionID, source: .agentApproved, arguments: arguments)
            item.status = result?.executed == true ? .applied : .conflicted
            if let result, result.executed {
                items.append(
                    CoCaptainTimelineItem(
                        content: .execution(ExecutionStatusItem(summary: result.message))
                    )
                )
            }
        case .nodeEdit(let role, let operations, let baseText):
            guard let store,
                  let node = patchEngine.resolveNode(for: role, in: store) else {
                item.status = .conflicted
                item.conflictDescription = LocalizationManager.shared.localizedString("The node could not be found in the current project.")
                break
            }

            guard (node.textContent ?? "") == baseText else {
                item.status = .conflicted
                item.conflictDescription = LocalizationManager.shared.localizedString("This node was edited after the suggestion was generated. Ask Co-Captain to revise.")
                break
            }

            do {
                let preview = try patchEngine.preview(role: role, operations: operations, in: store)
                store.updateNodeTextContent(id: node.id, text: preview.resultText, persist: true)
                item.status = .applied
                items.append(
                    CoCaptainTimelineItem(
                        content: .execution(
                            ExecutionStatusItem(
                                summary: LocalizationManager.shared.localizedString(
                                    "Applied updates to %@.",
                                    arguments: [role.localizedDisplayName]
                                )
                            )
                        )
                    )
                )
            } catch {
                item.status = .conflicted
                item.conflictDescription = error.localizedDescription
            }
        }

        bundle.items[itemIndex] = item
        items[bundleIndex].content = .reviewBundle(bundle)
    }

    public func rejectReviewItem(bundleID: UUID, itemID: UUID) {
        updateReviewItem(bundleID: bundleID, itemID: itemID, status: .rejected)
    }

    public func applyAll(in bundleID: UUID) {
        guard let bundle = reviewBundle(for: bundleID) else { return }
        
        // Create one checkpoint for the whole bundle
        store?.createCheckpoint(label: "Apply All Changes")
        
        for itemID in bundle.items.filter({ $0.status == .pending }).map(\.id) {
            applyReviewItem(bundleID: bundleID, itemID: itemID)
        }
    }

    public func rejectAll(in bundleID: UUID) {
        guard let bundle = reviewBundle(for: bundleID) else { return }
        for itemID in bundle.items.filter({ $0.status == .pending }).map(\.id) {
            rejectReviewItem(bundleID: bundleID, itemID: itemID)
        }
    }

    /// Resets chat state when the active project changes so streamed responses
    /// and review bundles cannot leak across project contexts.
    private func handleStoreChange() {
        let currentFileName = store?.fileName
        guard currentFileName != lastStoreFileName else { return }
        defer { lastStoreFileName = currentFileName }

        if lastStoreFileName != nil {
            streamingTask?.cancel()
            streamingTask = nil
            isThinking = false
            clearHistory()
        }
        
        runAnalysis()
    }

    private func reviewBundle(for bundleID: UUID) -> ReviewBundleItem? {
        guard let bundleIndex = items.firstIndex(where: { $0.id == bundleID }),
              case .reviewBundle(let bundle) = items[bundleIndex].content else {
            return nil
        }
        return bundle
    }

    private func updateReviewItem(bundleID: UUID, itemID: UUID, status: ReviewItemStatus) {
        guard let bundleIndex = items.firstIndex(where: { $0.id == bundleID }),
              case .reviewBundle(var bundle) = items[bundleIndex].content,
              let itemIndex = bundle.items.firstIndex(where: { $0.id == itemID }) else {
            return
        }

        bundle.items[itemIndex].status = status
        items[bundleIndex].content = .reviewBundle(bundle)
    }

    private func updateMessage(id: UUID, text: String) {
        guard let index = items.firstIndex(where: { $0.id == id }) else { return }
        if case .message(var bubble) = items[index].content {
            bubble.text = text
            items[index].content = .message(bubble)
        }
    }

    private func removeEmptyMessage(id: UUID) {
        guard let index = items.firstIndex(where: { $0.id == id }),
              case .message(let bubble) = items[index].content,
              !bubble.isUser,
              bubble.text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            return
        }

        items.remove(at: index)
    }

    private var lastMessage: ChatBubbleItem? {
        guard case .message(let bubble) = items.last?.content else { return nil }
        return bubble
    }

    private static func greetingItem() -> CoCaptainTimelineItem {
        CoCaptainTimelineItem(
            content: .message(
                ChatBubbleItem(
                    text: LocalizationManager.shared.localizedString("Hello! I'm your Co-Captain. How can I help you build today?"),
                    isUser: false
                )
            )
        )
    }
}
