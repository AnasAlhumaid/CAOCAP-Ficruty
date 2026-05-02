import SwiftUI

struct CoCaptainView: View {
    var viewModel: CoCaptainViewModel
    @State private var text: String = ""
    @FocusState private var isFocused: Bool

    var body: some View {
        @Bindable var viewModel = viewModel
        NavigationStack {
            VStack(spacing: 0) {
                CoCaptainTimelineListView(
                    viewModel: viewModel,
                    lastScrollPosition: $viewModel.lastScrollPosition,
                    isFocused: isFocused
                )

                CoCaptainInputComposer(
                    text: $text,
                    isFocused: $isFocused,
                    store: viewModel.store,
                    isThinking: viewModel.isThinking,
                    onSend: sendCurrentMessage,
                    onStop: viewModel.stopStreaming,
                    onQuickPrompt: sendQuickPrompt
                )
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

#Preview {
    CoCaptainView(viewModel: CoCaptainViewModel())
}
