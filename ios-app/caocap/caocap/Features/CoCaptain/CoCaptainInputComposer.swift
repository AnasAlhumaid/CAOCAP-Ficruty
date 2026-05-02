import SwiftUI

struct CoCaptainInputComposer: View {
    @Binding var text: String
    @FocusState.Binding var isFocused: Bool
    let store: ProjectStore?
    let isThinking: Bool
    let onSend: () -> Void
    let onStop: () -> Void
    let onQuickPrompt: (String) -> Void

    private var isInputValid: Bool {
        !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    private var canSend: Bool {
        isInputValid && !isThinking
    }

    var body: some View {
        VStack(spacing: 10) {
            Divider().opacity(0.5)

            if let store {
                ContextPill(
                    projectName: store.projectName,
                    fileName: store.fileName,
                    nodeCount: store.nodes.count
                )
            }

            HStack(alignment: .bottom, spacing: 8) {
                quickPromptMenu
                promptField
                sendButton
            }
            .padding(.horizontal, 12)
            .padding(.bottom, 10)
        }
        .background(Color.primary.opacity(0.02))
    }

    private var quickPromptMenu: some View {
        Menu {
            Button {
                onQuickPrompt("Summarize the current canvas and point out the most important next step.")
            } label: {
                Label("Summarize Canvas", systemImage: "doc.text.magnifyingglass")
            }
            .disabled(isThinking)

            Button {
                onQuickPrompt("Review the current canvas for obvious issues, missing pieces, or polish opportunities.")
            } label: {
                Label("Review Canvas", systemImage: "checklist")
            }
            .disabled(isThinking)

            Button {
                onQuickPrompt("Suggest three useful next improvements for this project.")
            } label: {
                Label("Suggest Next Steps", systemImage: "sparkles")
            }
            .disabled(isThinking)
        } label: {
            Image(systemName: "plus.circle.fill")
                .font(.system(size: 26))
                .foregroundColor(.blue)
                .shadow(color: .blue.opacity(0.2), radius: 4)
        }
        .padding(.bottom, 6)
    }

    private var promptField: some View {
        HStack(spacing: 0) {
            TextField("Ask Co-Captain...", text: $text, axis: .vertical)
                .lineLimit(1...5)
                .focused($isFocused)
                .submitLabel(.send)
                .onSubmit {
                    onSend()
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
    }

    private var sendButton: some View {
        Button(action: {
            if isThinking {
                onStop()
            } else {
                onSend()
            }
        }) {
            ZStack {
                if isThinking {
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
        .disabled(!isThinking && !canSend)
        .animation(.spring(response: 0.3, dampingFraction: 0.7), value: isInputValid)
        .animation(.spring(response: 0.3, dampingFraction: 0.7), value: isThinking)
        .padding(.bottom, 5)
    }
}
