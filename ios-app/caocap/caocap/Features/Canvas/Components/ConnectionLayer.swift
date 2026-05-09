import SwiftUI

/// Renders smooth, curved arrows between connected nodes in screen space. Links
/// are not placed inside the scaled node layer because large curves can clip
/// when their endpoints sit far apart on the infinite canvas.
struct ConnectionLayer: View {
    @AppStorage("connection_style") private var connectionStyle = "Dashed"
    let nodes: [SpatialNode]
    let dragOffsets: [UUID: CGSize]
    let viewport: ViewportState
    let center: CGPoint
    let nodeFrames: [UUID: NodeFrameData]
    
    var body: some View {
        Canvas { context, size in
            let nodeDict = Dictionary(uniqueKeysWithValues: nodes.map { ($0.id, $0) })
            
            for node in nodes {
                // 1. Structural Links (Next/Connected)
                var structuralTargets: [UUID] = []
                if let next = node.nextNodeId { structuralTargets.append(next) }
                if let connected = node.connectedNodeIds { structuralTargets.append(contentsOf: connected) }
                
                for targetId in structuralTargets {
                    if let targetNode = nodeDict[targetId] {
                        let start = screenPoint(for: node)
                        let end = screenPoint(for: targetNode)
                        drawArrow(context: context, from: start, to: end, themeColor: node.theme.color, scale: viewport.scale, isLogic: false)
                    }
                }
                
                // 2. Logic Links (Inputs -> Current Node)
                if let inputIds = node.inputNodeIds {
                    for sourceId in inputIds {
                        if let sourceNode = nodeDict[sourceId] {
                            let start = screenPoint(for: sourceNode)
                            let end = screenPoint(for: node)
                            // Logic links now use the source node's theme color to match the premium design
                            drawArrow(context: context, from: start, to: end, themeColor: sourceNode.theme.color, scale: viewport.scale, isLogic: false)
                        }
                    }
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .allowsHitTesting(false)
    }

    private func screenPoint(for node: SpatialNode) -> CGPoint {
        // Preference 1: Use real measured frame if available
        if let frameData = nodeFrames[node.id] {
            return frameData.center
        }
        
        // Fallback: Use manual calculation (only during initial render before frames are measured)
        let nodeOffset = dragOffsets[node.id] ?? .zero
        return CGPoint(
            x: center.x + (node.position.x + nodeOffset.width) * viewport.scale + viewport.offset.width,
            y: center.y + (node.position.y + nodeOffset.height) * viewport.scale + viewport.offset.height
        )
    }
    
    private func drawArrow(context: GraphicsContext, from: CGPoint, to: CGPoint, themeColor: Color, scale: CGFloat, isLogic: Bool) {
        var path = Path()
        path.move(to: from)
        
        // Calculate control points for a smooth curve
        let midX = (from.x + to.x) / 2
        let cp1 = CGPoint(x: midX, y: from.y)
        let cp2 = CGPoint(x: midX, y: to.y)
        
        path.addCurve(to: to, control1: cp1, control2: cp2)
        
        let stroke: StrokeStyle
        let color: Color
        
        if isLogic {
            // Logic links use a tighter dash and distinct orange color
            stroke = StrokeStyle(lineWidth: 2 * scale, lineCap: .round, lineJoin: .round, dash: [5 * scale, 5 * scale])
            color = .orange.opacity(0.8)
        } else {
            switch connectionStyle {
            case "Solid":
                stroke = StrokeStyle(lineWidth: 2 * scale, lineCap: .round, lineJoin: .round)
                color = themeColor.opacity(0.6)
            case "Neon":
                stroke = StrokeStyle(lineWidth: 3 * scale, lineCap: .round, lineJoin: .round)
                color = themeColor
            default: // Dashed
                stroke = StrokeStyle(lineWidth: 3 * scale, lineCap: .round, lineJoin: .round, dash: [10 * scale, 10 * scale])
                color = themeColor.opacity(0.4)
            }
        }
        
        var arrowContext = context
        if !isLogic && connectionStyle == "Neon" {
            arrowContext.addFilter(.shadow(color: themeColor, radius: 4 * scale))
        }
        
        arrowContext.stroke(path, with: .color(color), style: stroke)
        
        // Draw an arrowhead at the end
        drawArrowhead(context: arrowContext, at: to, direction: calculateDirection(from: cp2, to: to), color: color, scale: scale)
    }
    
    private func drawArrowhead(context: GraphicsContext, at point: CGPoint, direction: CGFloat, color: Color, scale: CGFloat) {
        let size: CGFloat = 12 * scale
        var path = Path()
        path.move(to: CGPoint(x: -size, y: -size/1.5))
        path.addLine(to: .zero)
        path.addLine(to: CGPoint(x: -size, y: size/1.5))
        
        var arrowContext = context
        arrowContext.translateBy(x: point.x, y: point.y)
        arrowContext.rotate(by: Angle(radians: Double(direction)))
        arrowContext.fill(path, with: .color(color))
    }
    
    private func calculateDirection(from: CGPoint, to: CGPoint) -> CGFloat {
        atan2(to.y - from.y, to.x - from.x)
    }
}
