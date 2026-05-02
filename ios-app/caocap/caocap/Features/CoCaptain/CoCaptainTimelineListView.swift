import SwiftUI

struct CoCaptainTimelineListView: View {
    let viewModel: CoCaptainViewModel
    @Binding var lastScrollPosition: UUID?
    let isFocused: Bool

    var body: some View {
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
            .scrollPosition(id: $lastScrollPosition)
            .scrollDismissesKeyboard(.interactively)
            .onChange(of: viewModel.items) {
                scrollToBottom(proxy: proxy)
            }
            .onChange(of: viewModel.isThinking) {
                scrollToBottom(proxy: proxy)
            }
            .onChange(of: isFocused) { _, newValue in
                if newValue {
                    Task { @MainActor in
                        try? await Task.sleep(for: .milliseconds(100))
                        scrollToBottom(proxy: proxy)
                    }
                }
            }
            .onAppear {
                restoreScrollPosition(proxy: proxy)
            }
        }
    }

    private func restoreScrollPosition(proxy: ScrollViewProxy) {
        if let lastScrollPosition {
            Task { @MainActor in
                try? await Task.sleep(for: .milliseconds(50))
                withAnimation {
                    proxy.scrollTo(lastScrollPosition, anchor: .top)
                }
            }
        } else {
            Task { @MainActor in
                try? await Task.sleep(for: .milliseconds(200))
                scrollToBottom(proxy: proxy)
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
}
