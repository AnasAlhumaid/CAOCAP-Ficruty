import SwiftUI

struct NodeView: View {
    let node: SpatialNode
    var isDragging: Bool = false
    @State private var isHovering = false
    @AppStorage(LocalizationManager.languageStorageKey) private var selectedLanguage = "English"
    
    var body: some View {
        VStack(spacing: 0) {
            HStack(alignment: .top, spacing: 16) {
                // Icon / Symbol
                if let icon = node.icon {
                    ZStack {
                        Circle()
                            .fill(themeColor.opacity(0.15))
                            .frame(width: 40, height: 40)
                        
                        Image(systemName: icon)
                            .font(.system(size: 20, weight: .semibold))
                            .foregroundColor(themeColor)
                    }
                }
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(node.displayTitle)
                        .font(.system(size: 18, weight: .bold, design: .rounded))
                        .foregroundColor(.primary)
                    
                    if let subtitle = node.displaySubtitle {
                        Text(subtitle)
                            .font(.system(size: 14, weight: .medium, design: .default))
                            .foregroundColor(.secondary)
                            .fixedSize(horizontal: false, vertical: true)
                            .lineLimit(3)
                    }

                    // Show SRS readiness badge for SRS nodes.
                    if node.type == .srs {
                        let state = node.srsReadinessState ?? .empty
                        HStack(spacing: 5) {
                            Image(systemName: state.icon)
                                .font(.system(size: 10, weight: .semibold))
                            Text(state.displayTitle)
                                .font(.system(size: 11, weight: .semibold, design: .rounded))
                        }
                        .foregroundColor(state == .stale ? .orange : themeColor)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background((state == .stale ? Color.orange : themeColor).opacity(0.12))
                        .clipShape(Capsule())
                        .padding(.top, 4)
                    }
                }
                .frame(maxWidth: 240, alignment: .leading)
            }
            .environment(\.layoutDirection, LocalizationManager.shared.layoutDirection(for: selectedLanguage))
            .padding(.bottom, node.type == .webView ? 16 : 0)
            
            if node.type == .webView, let html = node.htmlContent {
                HTMLWebView(htmlContent: html)
                    .frame(width: 360, height: 640)
                    .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
            }

        }
        .padding(.horizontal, 20)
        .padding(.vertical, 20)
        .background(
            ZStack {
                RoundedRectangle(cornerRadius: 24, style: .continuous)
                    .fill(.ultraThinMaterial)
                
                RoundedRectangle(cornerRadius: 24, style: .continuous)
                    .fill(isDragging ? themeColor.opacity(0.08) : themeColor.opacity(0.03))
            }
            .shadow(
                color: Color.black.opacity(isDragging ? 0.25 : 0.15),
                radius: isDragging ? 30 : 20,
                x: 0,
                y: isDragging ? 20 : 10
            )
        )
        .overlay(
            RoundedRectangle(cornerRadius: 24, style: .continuous)
                .stroke(
                    LinearGradient(
                        colors: [
                            .white.opacity(isDragging ? 0.6 : 0.3),
                            .white.opacity(0.05),
                            themeColor.opacity(isDragging ? 0.6 : 0.3)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    ),
                    lineWidth: isDragging ? 2 : 1
                )
        )
        .scaleEffect(isDragging ? 1.05 : (isHovering ? 1.02 : 1.0))
        .animation(.spring(response: 0.3, dampingFraction: 0.7), value: isDragging)
        .animation(.spring(response: 0.3, dampingFraction: 0.7), value: isHovering)
    }
    
    private var themeColor: Color {
        node.theme.color
    }
}

#Preview {
    ZStack {
        Color(white: 0.05).ignoresSafeArea()
        NodeView(node: SpatialNode(
            position: .zero,
            title: "Hello, world!",
            subtitle: "Welcome to the future of agentic programming.",
            icon: "sparkles",
            theme: .purple
        ))
    }
}
