import SwiftUI

/// Routes a selected node to the correct full-screen inspector/editor. Adding a
/// node type should usually update this router and the matching store/context
/// behavior together.
struct NodeDetailView: View {
    let node: SpatialNode
    let store: ProjectStore
    @Environment(\.dismiss) var dismiss
    
    var body: some View {
        if node.type == .webView {
            NavigationView {
                ZStack {
                    Color(uiColor: .systemBackground).ignoresSafeArea()
                    
                    if let html = node.htmlContent {
                        HTMLWebView(htmlContent: html)
                            .ignoresSafeArea()
                    } else {
                        Text("No content to display.")
                            .foregroundColor(.gray)
                    }
                }
                .navigationTitle(node.displayTitle)
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button("Done") {
                            dismiss()
                        }
                        .fontWeight(.semibold)
                    }
                }
            }
        } else if node.type == .code {
            CodeEditorView(node: node, store: store)
        } else if node.type == .srs {
            SRSEditorView(node: node, store: store)
        } else if node.type == .art {
            ArtEditorView(node: node, store: store)
        } else {
            NavigationView {
                ZStack {
                    // Background
                    themeColor.opacity(0.05).ignoresSafeArea()
                    
                    ScrollView {
                        VStack(alignment: .leading, spacing: 24) {
                            // Header Section
                            Section(header: Text("Aesthetics & Role").font(.caption).foregroundStyle(.secondary)) {
                                HStack(spacing: 12) {
                                    Picker("Theme", selection: Binding(
                                        get: { node.theme },
                                        set: { store.updateNodeTheme(id: node.id, theme: $0) }
                                    )) {
                                        ForEach(NodeTheme.allCases, id: \.self) { theme in
                                            Circle()
                                                .fill(theme.color)
                                                .frame(width: 20, height: 20)
                                                .tag(theme)
                                        }
                                    }
                                    .pickerStyle(.menu)
                                    .buttonStyle(.bordered)
                                    
                                    if !node.isProtected {
                                        Picker("Type", selection: Binding(
                                            get: { node.type },
                                            set: { store.updateNodeType(id: node.id, type: $0) }
                                        )) {
                                            ForEach(NodeType.allCases, id: \.self) { type in
                                                Text(type.displayName).tag(type)
                                            }
                                        }
                                        .pickerStyle(.menu)
                                        .buttonStyle(.bordered)
                                    }
                                }
                            }
                            HStack(spacing: 20) {
                                if let icon = node.icon {
                                    ZStack {
                                        Circle()
                                            .fill(themeColor.opacity(0.15))
                                            .frame(width: 80, height: 80)
                                        
                                        Image(systemName: icon)
                                            .font(.system(size: 32, weight: .bold))
                                            .foregroundColor(themeColor)
                                    }
                                }
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(node.displayTitle)
                                        .font(.system(size: 28, weight: .bold, design: .rounded))
                                    
                                    if let subtitle = node.displaySubtitle {
                                        Text(subtitle)
                                            .font(.system(size: 16, weight: .medium))
                                            .foregroundColor(.secondary)
                                    }
                                }
                            }
                            .padding(.top, 20)
                            
                            Divider()
                            
                            // Content Section (Placeholder for node-specific data)
                            VStack(alignment: .leading, spacing: 16) {
                                Label("Node Details", systemImage: "info.circle")
                                    .font(.headline)
                                    .foregroundColor(themeColor)
                                
                                Text("This node represents a spatial element in your project. You can drag it around the canvas to organize your thoughts and code.")
                                    .font(.body)
                                    .foregroundColor(.primary.opacity(0.8))
                                    .lineSpacing(4)
                                
                                HStack {
                                    DetailTag(label: "nodeDetail.typeLabel", value: "Spatial Node")
                                    DetailTag(label: "Theme", value: node.theme.localizedDisplayName)
                                }
                            }
                            .padding(.vertical)
                            
                            if !node.isProtected {
                                Divider()
                                
                                Button(role: .destructive) {
                                    HapticsManager.shared.notification(.warning)
                                    store.deleteNode(id: node.id)
                                    dismiss()
                                } label: {
                                    Label("Delete Node", systemImage: "trash")
                                        .frame(maxWidth: .infinity)
                                        .padding()
                                        .background(Color.red.opacity(0.1))
                                        .cornerRadius(12)
                                }
                                .padding(.vertical)
                            }
                            
                            Spacer()
                        }
                        .padding(24)
                    }
                }
                .navigationTitle("Node Inspector")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button("Done") {
                            dismiss()
                        }
                        .fontWeight(.semibold)
                    }
                }
            }
        }
    }
    
    private var themeColor: Color {
        node.theme.color
    }
}

struct DetailTag: View {
    let label: String
    let value: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(LocalizationManager.shared.localizedString(label).uppercased())
                .font(.system(size: 10, weight: .bold))
                .foregroundColor(.secondary)
            
            Text(value)
                .font(.system(size: 14, weight: .semibold))
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(Color.secondary.opacity(0.1))
                .cornerRadius(8)
        }
    }
}

#Preview {
    NodeDetailView(node: SpatialNode(
        position: .zero,
        title: "Intent Node",
        subtitle: "Define the core purpose of your app",
        icon: "lightbulb.fill",
        theme: .purple
    ), store: ProjectStore(fileName: "preview.json", projectName: "Preview"))
}
