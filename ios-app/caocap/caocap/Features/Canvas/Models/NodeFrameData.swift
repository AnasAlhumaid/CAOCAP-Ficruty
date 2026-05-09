import SwiftUI

/// Data structure to track the global frame of a node.
struct NodeFrameData: Equatable {
    let nodeId: UUID
    let frame: CGRect
    
    var center: CGPoint {
        CGPoint(x: frame.midX, y: frame.midY)
    }
}

/// PreferenceKey to bubble up node frames to the InfiniteCanvasView.
struct NodeFramePreferenceKey: PreferenceKey {
    static var defaultValue: [UUID: NodeFrameData] = [:]
    
    static func reduce(value: inout [UUID: NodeFrameData], nextValue: () -> [UUID: NodeFrameData]) {
        value.merge(nextValue(), uniquingKeysWith: { $1 })
    }
}
