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
    
    var body: some View {
        Canvas { context, size in
            let nodeDict = Dictionary(uniqueKeysWithValues: nodes.map { ($0.id, $0) })
            
            for node in nodes {
                var targets: [UUID] = []
                if let next = node.nextNodeId { targets.append(next) }
                if let connected = node.connectedNodeIds { targets.append(contentsOf: connected) }
                
                for targetId in targets {
                    if let nextNode = nodeDict[targetId] {
                        
                        let nodeOffset = dragOffsets[node.id] ?? .zero
                        let nextNodeOffset = dragOffsets[nextNode.id] ?? .zero
                        
                        // Convert back to screen space to ensure perfect alignment
                        let start = CGPoint(
                            x: center.x + (node.position.x + nodeOffset.width) * viewport.scale + viewport.offset.width,
                            y: center.y + (node.position.y + nodeOffset.height) * viewport.scale + viewport.offset.height
                        )
                        
                        let end = CGPoint(
                            x: center.x + (nextNode.position.x + nextNodeOffset.width) * viewport.scale + viewport.offset.width,
                            y: center.y + (nextNode.position.y + nextNodeOffset.height) * viewport.scale + viewport.offset.height
                        )
                        
                        drawArrow(context: context, from: start, to: end, themeColor: node.theme.color, scale: viewport.scale)
                    }
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .allowsHitTesting(false)
    }
    
    private func drawArrow(context: GraphicsContext, from: CGPoint, to: CGPoint, themeColor: Color, scale: CGFloat) {
        var path = Path()
        path.move(to: from)
        
        // Calculate control points for a smooth curve
        let midX = (from.x + to.x) / 2
        let cp1 = CGPoint(x: midX, y: from.y)
        let cp2 = CGPoint(x: midX, y: to.y)
        
        path.addCurve(to: to, control1: cp1, control2: cp2)
        
        
        let stroke: StrokeStyle
        let color: Color
        
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
        
        var arrowContext = context
        if connectionStyle == "Neon" {
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
