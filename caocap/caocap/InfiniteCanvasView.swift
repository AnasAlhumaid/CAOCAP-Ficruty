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
    
    var body: some View {
        GeometryReader { geometry in
            ZStack {
                // The Dotted Background (Renders dots relative to global state)
                DottedBackground(offset: offset, scale: scale)
                
                // The Content Layer (Everything here is "pinned" to the canvas)
                ZStack {
                    Text("CAOCAP Spatial Workspace")
                        .font(.system(.caption, design: .monospaced))
                        .foregroundColor(.secondary.opacity(0.5))
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
