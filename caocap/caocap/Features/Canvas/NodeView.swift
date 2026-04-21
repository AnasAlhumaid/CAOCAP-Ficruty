import SwiftUI

struct NodeView: View {
    let node: SpatialNode
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(node.content)
                .font(.system(size: 16, weight: .medium, design: .monospaced))
                .foregroundColor(.primary)
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 14)
        .background(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .fill(.ultraThinMaterial)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .stroke(Color.white.opacity(0.15), lineWidth: 0.5)
        )
        .shadow(color: Color.black.opacity(0.1), radius: 10, x: 0, y: 5)
    }
}

#Preview {
    ZStack {
        Color.black.ignoresSafeArea()
        NodeView(node: SpatialNode(position: .zero, content: "Hello, world!"))
    }
}
