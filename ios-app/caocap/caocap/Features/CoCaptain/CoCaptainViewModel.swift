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
    @ObservationIgnored
    public var actionDispatcher: (any AppActionPerforming)?

    /// Tracks the ID of the message that was last visible to the user.
    public var lastScrollPosition: UUID?

    @ObservationIgnored
    private let agentCoordinator = CoCaptainAgentCoordinator()
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
    }

    public func sendMessage(_ text: String) {
        guard !isThinking else { return }

        let userItem = ChatBubbleItem(text: text, isUser: true)
        items.append(CoCaptainTimelineItem(content: .message(userItem)))

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
                ) { [weak self] partialText in
                    self?.updateMessage(id: aiMessageID, text: partialText)
                }

                updateMessage(id: aiMessageID, text: result.visibleText)

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

    public func applyReviewItem(bundleID: UUID, itemID: UUID) {
        guard let bundleIndex = items.firstIndex(where: { $0.id == bundleID }),
              case .reviewBundle(var bundle) = items[bundleIndex].content,
              let itemIndex = bundle.items.firstIndex(where: { $0.id == itemID }) else {
            return
        }

        var item = bundle.items[itemIndex]

        switch item.source {
        case .appAction(let actionID):
            let result = actionDispatcher?.perform(actionID, source: .agentApproved)
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
                break
            }

            guard (node.textContent ?? "") == baseText else {
                item.status = .conflicted
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

    private func handleStoreChange() {
        let currentFileName = store?.fileName
        guard currentFileName != lastStoreFileName else { return }
        defer { lastStoreFileName = currentFileName }

        guard lastStoreFileName != nil else { return }

        streamingTask?.cancel()
        streamingTask = nil
        isThinking = false
        clearHistory()
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
