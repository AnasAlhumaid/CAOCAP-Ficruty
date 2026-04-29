import SwiftUI

/// A highly optimized canvas view that renders a procedural nested dotted grid with LOD.
struct DottedBackground: View {
    @AppStorage("grid_opacity") private var gridOpacity: Double = 0.1
    @Environment(\.colorScheme) var colorScheme
    let offset: CGSize
    let scale: CGFloat
    
    let dotSpacing: CGFloat = 30
    let dotSize: CGFloat = 2
    
    var body: some View {
        Canvas { context, size in
            let centerX = size.width / 2
            let centerY = size.height / 2
            let baseColor = colorScheme == .dark ? Color.white : Color.black
            let maxAlpha = gridOpacity * 5
            
            // Define our three levels of grid detail
            // Level 1: 30px (Dense)
            // Level 2: 60px (Medium)
            // Level 3: 120px (Sparse)
            
            // Calculate opacities for each level based on scale
            // Level 3 is always visible above 0.05
            let l3Alpha = max(0, min(1, (scale - 0.05) / 0.1)) * maxAlpha
            
            // Level 2 fades in between 0.15 and 0.4
            let l2Alpha = max(0, min(1, (scale - 0.15) / 0.25)) * maxAlpha
            
            // Level 1 fades in between 0.4 and 0.8
            let l1Alpha = max(0, min(1, (scale - 0.4) / 0.4)) * maxAlpha
            
            func drawLevel(spacing: CGFloat, alpha: CGFloat) {
                if alpha <= 0 { return }
                let scaledSpacing = spacing * scale
                let color = baseColor.opacity(alpha)
                
                let startX = ((offset.width + centerX).truncatingRemainder(dividingBy: scaledSpacing)) - scaledSpacing
                let startY = ((offset.height + centerY).truncatingRemainder(dividingBy: scaledSpacing)) - scaledSpacing
                
                for x in stride(from: startX, through: size.width + scaledSpacing, by: scaledSpacing) {
                    for y in stride(from: startY, through: size.height + scaledSpacing, by: scaledSpacing) {
                        // To avoid over-drawing dots that exist in sparser levels, 
                        // we could add logic here, but for simple dots, overlapping is fine
                        // and actually slightly sharpens the primary nodes.
                        let rect = CGRect(x: x, y: y, width: dotSize, height: dotSize)
                        context.fill(Path(ellipseIn: rect), with: .color(color))
                    }
                }
            }
            
            // Draw levels from sparsest to densest
            // Note: We draw them separately so we can stride differently for performance
            drawLevel(spacing: dotSpacing * 4, alpha: l3Alpha)
            drawLevel(spacing: dotSpacing * 2, alpha: l2Alpha)
            drawLevel(spacing: dotSpacing, alpha: l1Alpha)
        }
    }
}
