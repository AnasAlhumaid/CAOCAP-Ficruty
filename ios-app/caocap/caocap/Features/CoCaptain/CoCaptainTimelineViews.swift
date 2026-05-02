import SwiftUI

struct TimelineItemView: View {
    let item: CoCaptainTimelineItem
    let viewModel: CoCaptainViewModel

    var body: some View {
        switch item.content {
        case .message(let bubble):
            ChatBubbleView(message: bubble)
        case .execution(let status):
            ExecutionSummaryView(status: status)
        case .reviewBundle(let bundle):
            ReviewBundleView(bundle: bundle, viewModel: viewModel, bundleID: item.id)
        }
    }
}

extension CoCaptainTimelineItem {
    var isEmptyAssistantMessage: Bool {
        guard case .message(let bubble) = content,
              !bubble.isUser else {
            return false
        }

        return bubble.text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }
}

struct ContextPill: View {
    let projectName: String
    let fileName: String
    let nodeCount: Int

    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: "scope")
            Text("Using current canvas")
            Text(verbatim: "·")
            Text(LocalizationManager.shared.localizedProjectName(projectName, fileName: fileName))
            Text(verbatim: "·")
            Text(
                LocalizationManager.shared.localizedString(
                    "context.nodeCount",
                    arguments: [Int64(nodeCount)]
                )
            )
        }
        .font(.system(size: 12, weight: .medium))
        .foregroundColor(.secondary)
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(Color.primary.opacity(0.04))
        .clipShape(Capsule())
    }
}

struct ExecutionSummaryView: View {
    let status: ExecutionStatusItem

    var body: some View {
        HStack {
            Image(systemName: "checkmark.circle.fill")
                .foregroundColor(.green)
            Text(status.summary)
                .font(.system(size: 13, weight: .medium))
            Spacer()
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 10)
        .background(Color.green.opacity(0.08))
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
    }
}
