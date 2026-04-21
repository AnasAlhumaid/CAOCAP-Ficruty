import SwiftUI

struct NodeView: View {
    let node: SpatialNode
    @State private var isHovering = false
    
    var body: some View {
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
                Text(node.title)
                    .font(.system(size: 18, weight: .bold, design: .rounded))
                    .foregroundColor(.primary)
                
                if let subtitle = node.subtitle {
                    Text(subtitle)
                        .font(.system(size: 14, weight: .medium, design: .default))
                        .foregroundColor(.secondary)
                        .fixedSize(horizontal: false, vertical: true)
                        .lineLimit(3)
                }
            }
            .frame(maxWidth: 240, alignment: .leading)
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 20)
        .background(
            ZStack {
                RoundedRectangle(cornerRadius: 24, style: .continuous)
                    .fill(.ultraThinMaterial)
                
                RoundedRectangle(cornerRadius: 24, style: .continuous)
                    .fill(themeColor.opacity(0.03))
            }
        )
        .overlay(
            RoundedRectangle(cornerRadius: 24, style: .continuous)
                .stroke(
                    LinearGradient(
                        colors: [
                            .white.opacity(0.3),
                            .white.opacity(0.05),
                            themeColor.opacity(0.3)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    ),
                    lineWidth: 1
                )
        )
        .shadow(color: Color.black.opacity(0.15), radius: 20, x: 0, y: 10)
        .scaleEffect(isHovering ? 1.02 : 1.0)
        .animation(.spring(response: 0.3, dampingFraction: 0.7), value: isHovering)
    }
    
    private var themeColor: Color {
        if let colorName = node.color {
            switch colorName.lowercased() {
            case "blue": return .blue
            case "purple": return .purple
            case "orange": return .orange
            case "green": return .green
            case "pink": return .pink
            default: return .blue
            }
        }
        return .blue
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
            color: "purple"
        ))
    }
}
