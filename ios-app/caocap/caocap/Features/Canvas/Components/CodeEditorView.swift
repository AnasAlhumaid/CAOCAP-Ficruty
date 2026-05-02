import SwiftUI

struct CodeEditorView: View {
    let node: SpatialNode
    let store: ProjectStore
    @Environment(\.dismiss) var dismiss
    @State private var text: String
    
    init(node: SpatialNode, store: ProjectStore) {
        self.node = node
        self.store = store
        self._text = State(initialValue: node.textContent ?? "")
    }
    
    var body: some View {
        VStack(spacing: 0) {
            // Custom Top Bar (VS Code Tab Style)
            HStack {
                HStack(spacing: 6) {
                    Image(systemName: "curlybraces")
                        .foregroundColor(.blue)
                        .font(.system(size: 14, weight: .semibold))
                    Text("\(node.title.lowercased()).\(fileExtension(for: node.title))")
                        .font(.system(size: 13, weight: .medium, design: .monospaced))
                        .foregroundColor(.white)
                        .contextMenu {
                            Section("Aesthetics") {
                                ForEach(NodeTheme.allCases, id: \.self) { theme in
                                    Button {
                                        store.updateNodeTheme(id: node.id, theme: theme)
                                    } label: {
                                        Label(theme.rawValue.capitalized, systemImage: "circle.fill")
                                            .foregroundColor(theme.color)
                                    }
                                }
                            }
                            
                            Section("Transform") {
                                ForEach(NodeType.allCases, id: \.self) { type in
                                    if type != node.type {
                                        Button {
                                            store.updateNodeType(id: node.id, type: type)
                                        } label: {
                                            Label(type.displayName, systemImage: "arrow.triangle.2.circlepath")
                                        }
                                    }
                                }
                            }
                        }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(Color(red: 0.12, green: 0.12, blue: 0.12)) // Dark active tab color
                
                Spacer()
                
                Button(action: {
                    store.updateNodeTextContent(id: node.id, text: text, persist: true)
                    dismiss()
                }) {
                    Image(systemName: "xmark")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(.gray)
                        .padding(8)
                        .background(Circle().fill(Color.white.opacity(0.1)))
                }
                .padding(.trailing, 16)
            }
            .frame(height: 48)
            .background(Color(red: 0.15, green: 0.15, blue: 0.15)) // Header background
            
            // The Main Editor
            LineNumberedTextView(text: $text)
                .edgesIgnoringSafeArea(.bottom)
        }
        .background(Color(red: 0.12, green: 0.12, blue: 0.12).ignoresSafeArea())
        .environment(\.layoutDirection, .leftToRight)
    }
    
    private func fileExtension(for title: String) -> String {
        switch title.lowercased() {
        case "html": return "html"
        case "css": return "css"
        case "javascript": return "js"
        default: return "txt"
        }
    }
}
