import SwiftUI
import Observation

/// Encapsulates canvas pan and zoom math. Views should feed gestures into this
/// object rather than reimplementing coordinate transforms locally.
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
    public func handleDragTranslation(_ translation: CGSize) {
        offset = CGSize(
            width: lastOffset.width + translation.width,
            height: lastOffset.height + translation.height
        )
    }

    /// Updates the current offset based on the translation of an active drag gesture.
    public func handleDragChanged(_ value: DragGesture.Value) {
        handleDragTranslation(value.translation)
    }
    
    /// Commits the current offset as the starting point for the next drag.
    public func handleDragEnded() {
        lastOffset = offset
    }
    
    /// Updates scale and offset around the user's pinch anchor. The offset is
    /// recalculated so the canvas point under the fingers stays visually pinned
    /// while zoom changes.
    public func handleMagnificationChanged(_ magnification: CGFloat, at location: CGPoint, in viewSize: CGSize) {
        let newScale = min(max(lastScale * magnification, minScale), maxScale)
        
        // Calculate the center of the view (our coordinate origin)
        let centerX = viewSize.width / 2
        let centerY = viewSize.height / 2
        
        // 1. Find where the pinch location is in 'unscaled' canvas coordinates (relative to origin)
        // (Location - Center - LastOffset) / LastScale
        let pointX = (location.x - centerX - lastOffset.width) / lastScale
        let pointY = (location.y - centerY - lastOffset.height) / lastScale
        
        // 2. Update the scale
        self.scale = newScale
        
        // 3. Calculate the new offset so that the same point remains under the fingers
        // NewOffset = Location - Center - (Point * NewScale)
        self.offset = CGSize(
            width: location.x - centerX - (pointX * newScale),
            height: location.y - centerY - (pointY * newScale)
        )
    }
    
    /// Commits the current scale and offset as the starting point for the next zoom.
    public func handleMagnificationEnded() {
        lastScale = scale
        lastOffset = offset
    }

    /// Computes and applies the offset/scale needed to center a canvas-space node
    /// position in the visible container. Caller should wrap in withAnimation.
    public func flyTo(nodePosition: CGPoint, containerSize: CGSize, targetScale: CGFloat = 1.0) {
        let clampedScale = min(max(targetScale, minScale), maxScale)
        
        // Target offset = -nodePosition * scale
        // This centers the node at the origin (center of the screen)
        let newOffset = CGSize(
            width: -nodePosition.x * clampedScale,
            height: -nodePosition.y * clampedScale
        )
        
        self.scale = clampedScale
        self.lastScale = clampedScale
        self.offset = newOffset
        self.lastOffset = newOffset
    }
}
