import SwiftUI
import Observation

@Observable
public class ViewportState {
    /// The current visual offset of the canvas.
    public var offset: CGSize = .zero
    
    /// The offset at the end of the previous drag gesture.
    public var lastOffset: CGSize = .zero
    
    /// The current zoom level of the canvas.
    public var scale: CGFloat = 1.0
    
    /// The zoom level at the end of the previous magnification gesture.
    public var lastScale: CGFloat = 1.0
    
    public let minScale: CGFloat = 0.1
    public let maxScale: CGFloat = 2.0
    
    public init(offset: CGSize = .zero, scale: CGFloat = 1.0) {
        self.offset = offset
        self.lastOffset = offset
        self.scale = scale
        self.lastScale = scale
    }
    
    /// Updates the current offset based on the translation of an active drag gesture.
    public func handleDragChanged(_ value: DragGesture.Value) {
        offset = CGSize(
            width: lastOffset.width + value.translation.width,
            height: lastOffset.height + value.translation.height
        )
    }
    
    /// Commits the current offset as the starting point for the next drag.
    public func handleDragEnded() {
        lastOffset = offset
    }
    
    /// Updates the current scale based on a magnification multiplier.
    public func handleMagnificationChanged(_ value: CGFloat) {
        let newScale = lastScale * value
        scale = min(max(newScale, minScale), maxScale)
    }
    
    /// Commits the current scale as the starting point for the next zoom.
    public func handleMagnificationEnded() {
        lastScale = scale
    }
}
