import SwiftUI

struct InfiniteCanvasView: View {
    @Environment(\.colorScheme) var colorScheme
    @State private var offset: CGSize = .zero
    @State private var lastOffset: CGSize = .zero
    @State private var scale: CGFloat = 1.0
    @State private var lastScale: CGFloat = 1.0
    
    // Scale Limits to prevent crashes and performance issues
    let minScale: CGFloat = 0.5
    let maxScale: CGFloat = 3.0
    @State private var nodes: [SpatialNode] = [
        SpatialNode(
            position: .zero,
            title: "Hello, world!",
            subtitle: "You've entered a spatial IDE where code lives in 2D space.",
            icon: "hand.wave.fill",
            color: "purple"
        ),
        SpatialNode(
            position: CGPoint(x: 450, y: 150),
            title: "The Spatial Medium",
            subtitle: "Pan and zoom to explore the architecture of your software.",
            icon: "map.fill",
            color: "blue"
        ),
        SpatialNode(
            position: CGPoint(x: -400, y: 350),
            title: "Agentic Flow",
            subtitle: "AI agents don't just write code; they inhabit this workspace with you.",
            icon: "sparkles",
            color: "pink"
        ),
        SpatialNode(
            position: CGPoint(x: 100, y: -450),
            title: "Direct Manipulation",
            subtitle: "Grab an idea, move it, scale it, and see how it connects.",
            icon: "hand.point.up.left.fill",
            color: "orange"
        ),
        SpatialNode(
            position: CGPoint(x: -600, y: -100),
            title: "Start Building",
            subtitle: "Press Cmd+K to summon the Omnibox and create your first node.",
            icon: "command",
            color: "green"
        )
    ]
    
    var body: some View {
        GeometryReader { geometry in
            let center = CGPoint(x: geometry.size.width / 2, y: geometry.size.height / 2)
            
            ZStack {
                // The Dotted Background (Renders dots relative to global state)
                DottedBackground(offset: offset, scale: scale)
                
                // The Content Layer (Everything here is "pinned" to the canvas)
                ZStack {
                    ForEach(nodes) { node in
                        NodeView(node: node)
                            .position(
                                x: center.x + node.position.x,
                                y: center.y + node.position.y
                            )
                    }
                }
                .scaleEffect(scale) // 1. Scale the coordinate system
                .offset(offset)     // 2. Pan the scaled system
            }
            .contentShape(Rectangle()) // Make the whole area interactive
            .simultaneousGesture(
                DragGesture()
                    .onChanged { value in
                        offset = CGSize(
                            width: lastOffset.width + value.translation.width,
                            height: lastOffset.height + value.translation.height
                        )
                    }
                    .onEnded { _ in
                        lastOffset = offset
                    }
            )
            .simultaneousGesture(
                MagnificationGesture()
                    .onChanged { value in
                        // Clamp the scale to prevent 0 or extreme density
                        let newScale = lastScale * value
                        scale = min(max(newScale, minScale), maxScale)
                    }
                    .onEnded { _ in
                        lastScale = scale
                    }
            )
        }
        .background(backgroundColor) // Dark professional background
        .edgesIgnoringSafeArea(.all)
    }
    
    private var backgroundColor: Color {
        colorScheme == .dark ? Color(white: 0.05) : Color(white: 0.95)
    }
}

struct DottedBackground: View {
    @Environment(\.colorScheme) var colorScheme
    let offset: CGSize
    let scale: CGFloat
    
    let dotSpacing: CGFloat = 30
    let dotSize: CGFloat = 2
    
    var body: some View {
        Canvas { context, size in
            let scaledSpacing = dotSpacing * scale
            let dotColor = colorScheme == .dark ? Color.white.opacity(0.5) : Color.black.opacity(0.5)
            
            // Calculate start points relative to the CENTER of the screen
            // This ensures the grid and the content expand from the same anchor point
            let centerX = size.width / 2
            let centerY = size.height / 2
            
            let startX = ((offset.width + centerX).truncatingRemainder(dividingBy: scaledSpacing)) - scaledSpacing
            let startY = ((offset.height + centerY).truncatingRemainder(dividingBy: scaledSpacing)) - scaledSpacing
            
            for x in stride(from: startX, through: size.width + scaledSpacing, by: scaledSpacing) {
                for y in stride(from: startY, through: size.height + scaledSpacing, by: scaledSpacing) {
                    let rect = CGRect(x: x, y: y, width: dotSize, height: dotSize)
                    context.fill(Path(ellipseIn: rect), with: .color(dotColor))
                }
            }
        }
    }
}

#Preview {
    InfiniteCanvasView()
}
