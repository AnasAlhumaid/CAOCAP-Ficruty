import SwiftUI

struct NodeDetailView: View {
    let node: SpatialNode
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
                .navigationTitle(node.title)
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
        } else {
            NavigationView {
                ZStack {
                    // Background
                    themeColor.opacity(0.05).ignoresSafeArea()
                    
                    ScrollView {
                        VStack(alignment: .leading, spacing: 24) {
                            // Header Section
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
                                    Text(node.title)
                                        .font(.system(size: 28, weight: .bold, design: .rounded))
                                    
                                    if let subtitle = node.subtitle {
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
                                    DetailTag(label: "Type", value: "Spatial Node")
                                    DetailTag(label: "Theme", value: node.theme.rawValue.capitalized)
                                }
                            }
                            .padding(.vertical)
                            
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
            Text(label.uppercased())
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
    ))
}
