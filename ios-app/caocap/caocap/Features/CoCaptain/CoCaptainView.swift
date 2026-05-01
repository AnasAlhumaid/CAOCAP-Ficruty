import SwiftUI
import UIKit

struct CoCaptainView: View {
    var viewModel: CoCaptainViewModel
    @State private var text: String = ""
    @FocusState private var isFocused: Bool

    var body: some View {
        @Bindable var viewModel = viewModel
        NavigationStack {
            VStack(spacing: 0) {
                ScrollViewReader { proxy in
                    ScrollView {
                        VStack(alignment: .leading, spacing: 16) {
                            ForEach(viewModel.items) { item in
                                if !item.isEmptyAssistantMessage {
                                    TimelineItemView(item: item, viewModel: viewModel)
                                        .id(item.id)
                                }
                            }

                            if viewModel.isAwaitingFirstResponse {
                                HStack(alignment: .bottom, spacing: 8) {
                                    Image("cocaptain")
                                        .resizable()
                                        .scaledToFill()
                                        .frame(width: 28, height: 28)
                                        .clipShape(Circle())
                                        .shadow(color: .blue.opacity(0.5), radius: 4, x: 0, y: 0)

                                    ThinkingIndicator()
                                        .transition(.opacity.combined(with: .move(edge: .bottom)))

                                    Spacer()
                                }
                                .id("thinking_indicator")
                            }
                        }
                        .padding()
                        .scrollTargetLayout()
                    }
                    .scrollPosition(id: $viewModel.lastScrollPosition)
                    .scrollDismissesKeyboard(.interactively)
                    .onChange(of: viewModel.items) {
                        scrollToBottom(proxy: proxy)
                    }
                    .onChange(of: viewModel.isThinking) {
                        scrollToBottom(proxy: proxy)
                    }
                    .onChange(of: isFocused) { _, newValue in
                        if newValue {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                                scrollToBottom(proxy: proxy)
                            }
                        }
                    }
                    .onAppear {
                        if let lastPos = viewModel.lastScrollPosition {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
                                withAnimation {
                                    proxy.scrollTo(lastPos, anchor: .top)
                                }
                            }
                        } else {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                                scrollToBottom(proxy: proxy)
                            }
                        }
                    }
                }

                VStack(spacing: 10) {
                    Divider().opacity(0.5)

                    if let store = viewModel.store {
                        ContextPill(projectName: store.projectName, fileName: store.fileName, nodeCount: store.nodes.count)
                    }

                    HStack(alignment: .bottom, spacing: 8) {
                        Menu {
                            Button {
                                sendQuickPrompt("Summarize the current canvas and point out the most important next step.")
                            } label: {
                                Label("Summarize Canvas", systemImage: "doc.text.magnifyingglass")
                            }
                            .disabled(viewModel.isThinking)

                            Button {
                                sendQuickPrompt("Review the current canvas for obvious issues, missing pieces, or polish opportunities.")
                            } label: {
                                Label("Review Canvas", systemImage: "checklist")
                            }
                            .disabled(viewModel.isThinking)

                            Button {
                                sendQuickPrompt("Suggest three useful next improvements for this project.")
                            } label: {
                                Label("Suggest Next Steps", systemImage: "sparkles")
                            }
                            .disabled(viewModel.isThinking)
                        } label: {
                            Image(systemName: "plus.circle.fill")
                                .font(.system(size: 26))
                                .foregroundColor(.blue)
                                .shadow(color: .blue.opacity(0.2), radius: 4)
                        }
                        .padding(.bottom, 6)

                        HStack(spacing: 0) {
                            TextField("Ask Co-Captain...", text: $text, axis: .vertical)
                                .lineLimit(1...5)
                                .focused($isFocused)
                                .submitLabel(.send)
                                .onSubmit {
                                    sendCurrentMessage()
                                }
                                .padding(.horizontal, 16)
                                .padding(.vertical, 12)
                        }
                        .background(Color.primary.opacity(0.06))
                        .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
                        .overlay(
                            RoundedRectangle(cornerRadius: 18, style: .continuous)
                                .stroke(isFocused ? Color.blue.opacity(0.3) : Color.clear, lineWidth: 1.5)
                        )
                        .animation(.easeInOut(duration: 0.2), value: isFocused)

                        let isInputValid = !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
                        let canSend = isInputValid && !viewModel.isThinking

                        Button(action: {
                            if viewModel.isThinking {
                                viewModel.stopStreaming()
                            } else {
                                sendCurrentMessage()
                            }
                        }) {
                            ZStack {
                                if viewModel.isThinking {
                                    Image(systemName: "stop.circle.fill")
                                        .font(.system(size: 38))
                                        .frame(width: 38, height: 38)
                                        .transition(.scale.combined(with: .opacity))
                                } else if isInputValid {
                                    Image(systemName: "arrow.up.circle.fill")
                                        .font(.system(size: 38))
                                        .transition(.scale.combined(with: .opacity))
                                } else {
                                    Image(systemName: "mic.fill")
                                        .font(.system(size: 22))
                                        .transition(.scale.combined(with: .opacity))
                                        .frame(width: 38, height: 38)
                                        .background(Color.blue.opacity(0.1))
                                        .clipShape(Circle())
                                }
                            }
                            .foregroundColor(.blue)
                            .shadow(color: .blue.opacity(0.3), radius: 6, y: 3)
                        }
                        .disabled(!viewModel.isThinking && !canSend)
                        .animation(.spring(response: 0.3, dampingFraction: 0.7), value: isInputValid)
                        .animation(.spring(response: 0.3, dampingFraction: 0.7), value: viewModel.isThinking)
                        .padding(.bottom, 5)
                    }
                    .padding(.horizontal, 12)
                    .padding(.bottom, 10)
                }
                .background(Color.primary.opacity(0.02))
            }
            .navigationTitle("Co-Captain")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        viewModel.setPresented(false)
                    }
                }

                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: {
                        viewModel.clearHistory()
                    }) {
                        Text("Clear")
                            .foregroundColor(.red)
                    }
                }
            }
        }
    }

    private func scrollToBottom(proxy: ScrollViewProxy) {
        if viewModel.isAwaitingFirstResponse {
            withAnimation {
                proxy.scrollTo("thinking_indicator", anchor: .bottom)
            }
        } else if let lastItem = viewModel.items.last {
            withAnimation {
                proxy.scrollTo(lastItem.id, anchor: .bottom)
            }
        }
    }

    private func sendCurrentMessage() {
        let prompt = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !prompt.isEmpty, !viewModel.isThinking else { return }

        viewModel.sendMessage(prompt)
        text = ""
        isFocused = false
    }

    private func sendQuickPrompt(_ prompt: String) {
        guard !viewModel.isThinking else { return }

        text = ""
        isFocused = false
        viewModel.sendMessage(prompt)
    }
}

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

private extension CoCaptainTimelineItem {
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

struct ReviewBundleView: View {
    let bundle: ReviewBundleItem
    let viewModel: CoCaptainViewModel
    let bundleID: UUID

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text(bundle.title)
                    .font(.system(size: 16, weight: .bold))
                Spacer()
            }

            ForEach(bundle.items) { item in
                ReviewCardView(item: item) {
                    viewModel.applyReviewItem(bundleID: bundleID, itemID: item.id)
                } onReject: {
                    viewModel.rejectReviewItem(bundleID: bundleID, itemID: item.id)
                }
            }

            HStack(spacing: 16) {
                Spacer()
                Button("Apply All") {
                    viewModel.applyAll(in: bundleID)
                }
                .font(.system(size: 12, weight: .semibold))
                .disabled(!hasPendingItems)

                Button("Reject All") {
                    viewModel.rejectAll(in: bundleID)
                }
                .font(.system(size: 12, weight: .semibold))
                .foregroundColor(.red)
                .disabled(!hasPendingItems)
            }
            .padding(.top, 2)
        }
        .padding(14)
        .background(Color.primary.opacity(0.05))
        .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
    }

    private var hasPendingItems: Bool {
        bundle.items.contains { $0.status == .pending }
    }
}

struct ReviewCardView: View {
    let item: PendingReviewItem
    let onApply: () -> Void
    let onReject: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                Text(item.targetLabel)
                    .font(.system(size: 14, weight: .bold))
                Spacer()
                Text(item.status.localizedTitle)
                    .font(.system(size: 11, weight: .bold))
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(statusColor.opacity(0.14))
                    .foregroundColor(statusColor)
                    .clipShape(Capsule())
            }

            Text(item.summary)
                .font(.system(size: 13, weight: .medium))
                .foregroundColor(.secondary)

            // Show the reason and guidance when this item conflicted.
            if item.status == .conflicted, let reason = item.conflictDescription {
                HStack(alignment: .top, spacing: 6) {
                    Image(systemName: "exclamationmark.triangle.fill")
                        .font(.system(size: 11))
                        .foregroundColor(.orange)
                    Text(reason)
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.orange)
                        .fixedSize(horizontal: false, vertical: true)
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 8)
                .background(Color.orange.opacity(0.08))
                .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
            }

            Text(item.preview.isEmpty ? LocalizationManager.shared.localizedString("No preview available.") : item.preview)
                .font(.system(size: 12, weight: .regular, design: .monospaced))
                .padding(10)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color.black.opacity(0.06))
                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))

            HStack {
                Button("Apply") {
                    onApply()
                }
                .buttonStyle(.borderedProminent)
                .disabled(item.status != .pending)

                Button("Reject") {
                    onReject()
                }
                .buttonStyle(.bordered)
                .disabled(item.status != .pending)
            }
        }
        .padding(12)
        .background(Color.white.opacity(0.04))
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
    }

    private var statusColor: Color {
        switch item.status {
        case .pending: return .orange
        case .applied: return .green
        case .conflicted: return .red
        case .rejected: return .secondary
        }
    }
}

struct ThinkingIndicator: View {
    @State private var dotScale: CGFloat = 0.5

    var body: some View {
        HStack(spacing: 4) {
            ForEach(0..<3) { index in
                Circle()
                    .fill(Color.blue.opacity(0.5))
                    .frame(width: 6, height: 6)
                    .scaleEffect(dotScale)
                    .animation(
                        .easeInOut(duration: 0.6)
                            .repeatForever()
                            .delay(Double(index) * 0.2),
                        value: dotScale
                    )
            }
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(Color.primary.opacity(0.05))
        .clipShape(Capsule())
        .onAppear {
            dotScale = 1.0
        }
    }
}

struct ChatBubbleView: View {
    let message: ChatBubbleItem
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        HStack(alignment: .bottom, spacing: 10) {
            if message.isUser {
                Spacer()
            } else {
                Image("cocaptain")
                    .resizable()
                    .scaledToFill()
                    .frame(width: 32, height: 32)
                    .clipShape(Circle())
                    .overlay(Circle().stroke(Color.blue.opacity(0.3), lineWidth: 1))
                    .shadow(color: .blue.opacity(0.4), radius: 6)
            }

            VStack(alignment: message.isUser ? .trailing : .leading, spacing: 4) {
                ChatBubbleText(message: message)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)
                    .background(
                        Group {
                            if message.isUser {
                                MessageBubbleShape(isUser: true)
                                    .fill(
                                        LinearGradient(
                                            colors: [Color(hex: "0066FF"), Color(hex: "00CCFF")],
                                            startPoint: .topLeading,
                                            endPoint: .bottomTrailing
                                        )
                                    )
                                    .shadow(color: .blue.opacity(0.2), radius: 4, y: 2)
                            } else {
                                MessageBubbleShape(isUser: false)
                                    .fill(.ultraThinMaterial)
                                    .overlay(
                                        MessageBubbleShape(isUser: false)
                                            .stroke(
                                                LinearGradient(
                                                    colors: [
                                                        Color.blue.opacity(0.4),
                                                        Color.cyan.opacity(0.2)
                                                    ],
                                                    startPoint: .topLeading,
                                                    endPoint: .bottomTrailing
                                                ),
                                                lineWidth: 1
                                            )
                                    )
                            }
                        }
                    )
                    .foregroundColor(message.isUser ? .white : .primary)
            }

            if message.isUser {
                ZStack {
                    Circle()
                        .fill(.ultraThinMaterial)
                        .frame(width: 34, height: 34)
                        .overlay(Circle().stroke(Color.primary.opacity(0.1), lineWidth: 1))

                    Image(systemName: "person.fill")
                        .font(.system(size: 14))
                        .foregroundColor(.primary.opacity(0.7))
                }
            } else {
                Spacer()
            }
        }
        .transition(.asymmetric(insertion: .push(from: .bottom).combined(with: .opacity), removal: .opacity))
    }
}

struct ChatBubbleText: View {
    let message: ChatBubbleItem

    var body: some View {
        Text(message.isUser ? AttributedString(message.text) : message.markdownText)
            .font(.system(size: 15, weight: .medium))
            .textSelection(.enabled)
            .fixedSize(horizontal: false, vertical: true)
    }
}

struct MessageBubbleShape: Shape {
    var isUser: Bool
    var radius: CGFloat = 18

    func path(in rect: CGRect) -> Path {
        var path = Path()

        let tl = radius
        let tr = radius
        let bl = isUser ? radius : 4
        let br = isUser ? 4 : radius

        path.move(to: CGPoint(x: rect.minX + tl, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX - tr, y: rect.minY))
        path.addArc(center: CGPoint(x: rect.maxX - tr, y: rect.minY + tr), radius: tr, startAngle: Angle(degrees: -90), endAngle: Angle(degrees: 0), clockwise: false)

        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY - br))
        path.addArc(center: CGPoint(x: rect.maxX - br, y: rect.maxY - br), radius: br, startAngle: Angle(degrees: 0), endAngle: Angle(degrees: 90), clockwise: false)

        path.addLine(to: CGPoint(x: rect.minX + bl, y: rect.maxY))
        path.addArc(center: CGPoint(x: rect.minX + bl, y: rect.maxY - bl), radius: bl, startAngle: Angle(degrees: 90), endAngle: Angle(degrees: 180), clockwise: false)

        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY + tl))
        path.addArc(center: CGPoint(x: rect.minX + tl, y: rect.minY + tl), radius: tl, startAngle: Angle(degrees: 180), endAngle: Angle(degrees: 270), clockwise: false)

        return path
    }
}

#Preview {
    CoCaptainView(viewModel: CoCaptainViewModel())
}
