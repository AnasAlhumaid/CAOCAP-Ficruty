import SwiftUI

struct InfiniteCanvasView: View {
    @Environment(\.colorScheme) var colorScheme
    
    /// Tracks the current panning and zooming state of the canvas.
    @State private var viewport: ViewportState
    
    /// The central store managing node data and persistence.
    var store: ProjectStore
    
    init(store: ProjectStore) {
        self.store = store
        // Initialize viewport from the store's persisted state.
        self._viewport = State(initialValue: ViewportState(
            offset: store.viewportOffset,
            scale: store.viewportScale
        ))
    }
    
    // Selection and Dragging State
    @State private var selectedNode: SpatialNode?
    @State private var nodeDragOffsets: [UUID: CGSize] = [:]
    @State private var isDraggingNode = false
    
    var body: some View {
        GeometryReader { geometry in
            // Calculate the screen center to serve as the canvas origin.
            let center = CGPoint(x: geometry.size.width / 2, y: geometry.size.height / 2)
            
            ZStack {
                // Layer 1: The Infinite Dotted Grid
                DottedBackground(offset: viewport.offset, scale: viewport.scale)
                
                // Layer 2: The Spatial Nodes
                ZStack {
                    ForEach(store.nodes) { node in
                        let currentOffset = nodeDragOffsets[node.id] ?? .zero
                        let isDraggingThisNode = nodeDragOffsets[node.id] != nil
                        
                        NodeView(node: node, isDragging: isDraggingThisNode)
                            .position(
                                x: center.x + node.position.x + currentOffset.width,
                                y: center.y + node.position.y + currentOffset.height
                            )
                            .onTapGesture {
                                selectedNode = node
                            }
                            .highPriorityGesture(
                                DragGesture(minimumDistance: 5)
                                    .onChanged { value in
                                        // Block canvas panning while a node is being moved.
                                        isDraggingNode = true
                                        nodeDragOffsets[node.id] = value.translation
                                    }
                                    .onEnded { value in
                                        // Finalize the node position with a smooth spring animation.
                                        withAnimation(.spring(response: 0.4, dampingFraction: 0.75)) {
                                            let finalX = node.position.x + value.translation.width
                                            let finalY = node.position.y + value.translation.height
                                            store.updateNodePosition(id: node.id, position: CGPoint(x: finalX, y: finalY))
                                            
                                            nodeDragOffsets[node.id] = nil
                                            isDraggingNode = false
                                        }
                                    }
                            )
                    }
                }
                .scaleEffect(viewport.scale)
                .offset(viewport.offset)
            }
            .contentShape(Rectangle()) // Ensure the entire area is gesture-sensitive.
            .simultaneousGesture(
                DragGesture()
                    .onChanged { value in
                        // Only pan the background if no node is currently being dragged.
                        if !isDraggingNode {
                            viewport.handleDragChanged(value)
                        }
                    }
                    .onEnded { _ in 
                        if !isDraggingNode {
                            viewport.handleDragEnded()
                            // Persist the new canvas offset.
                            store.updateViewport(offset: viewport.offset, scale: viewport.scale)
                        }
                    }
            )
            .simultaneousGesture(
                MagnificationGesture()
                    .onChanged { 
                        viewport.handleMagnificationChanged($0)
                    }
                    .onEnded { _ in 
                        viewport.handleMagnificationEnded()
                        // Persist the new zoom level.
                        store.updateViewport(offset: viewport.offset, scale: viewport.scale)
                    }
            )
        }
        .background(backgroundColor)
        .edgesIgnoringSafeArea(.all)
        .sheet(item: $selectedNode) { node in
            NodeDetailView(node: node)
        }
    }
    
    private var backgroundColor: Color {
        colorScheme == .dark ? Color(white: 0.05) : Color(white: 0.95)
    }
}

/// A highly optimized canvas view that renders a procedural dotted grid.
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
            
            let centerX = size.width / 2
            let centerY = size.height / 2
            
            // Calculate the starting position for the dot grid to ensure it loops infinitely.
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
    InfiniteCanvasView(store: ProjectStore())
}
