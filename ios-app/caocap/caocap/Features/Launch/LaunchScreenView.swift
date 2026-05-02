import SwiftUI

struct ShimmerEffect: ViewModifier {
    @State private var isInitialState = true

    func body(content: Content) -> some View {
        content
            .mask(
                LinearGradient(
                    gradient: Gradient(colors: [.black.opacity(0.4), .black, .black.opacity(0.4)]),
                    startPoint: (isInitialState ? .init(x: -0.3, y: -0.3) : .init(x: 1, y: 1)),
                    endPoint: (isInitialState ? .init(x: 0, y: 0) : .init(x: 1.3, y: 1.3))
                )
            )
            .onAppear {
                withAnimation(
                    .linear(duration: 1.5)
                    .delay(0.2)
                    .repeatForever(autoreverses: false)
                ) {
                    isInitialState = false
                }
            }
    }
}

extension View {
    func shimmer() -> some View {
        modifier(ShimmerEffect())
    }
}

public struct LaunchScreenView: View {
    @Environment(\.colorScheme) var colorScheme
    @State private var opacity: Double = 0.0
    @State private var scale: CGFloat = 0.85
    @State private var glowOpacity: Double = 0.0
    @State private var continuousScale: CGFloat = 1.0
    
    public init() {}
    
    public var body: some View {
        ZStack {
            // Background adapts to color scheme
            (colorScheme == .dark ? Color(hex: "050505") : Color(UIColor.systemBackground))
                .ignoresSafeArea()
            
            // Subtle spatial sketch background
            Image("SpaceSketchBG")
                .resizable()
                .scaledToFill()
                .opacity(colorScheme == .dark ? 0.3 : 0.15)
                .blendMode(colorScheme == .dark ? .screen : .multiply)
                .scaleEffect(continuousScale * 1.1) // Moves slightly faster than the text for parallax
                .ignoresSafeArea()
            
            // Subtle center glow
            RadialGradient(
                gradient: Gradient(colors: [Color(hex: "3B82F6").opacity(colorScheme == .dark ? 0.15 : 0.05), Color.clear]),
                center: .center,
                startRadius: 0,
                endRadius: 300
            )
            .opacity(glowOpacity)
            
            VStack(spacing: 12) {
                Text("CAOCAP")
                    .font(.system(size: 42, weight: .semibold, design: .rounded))
                    .tracking(2)
                    .foregroundStyle(
                        LinearGradient(
                            colors: colorScheme == .dark ? [.white, Color(hex: "E2E8F0")] : [.black, Color(hex: "334155")],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    )
                    .shimmer()
                
                Text("THE FUTURE OF PROGRAMMING")
                    .font(.system(size: 11, weight: .bold, design: .default))
                    .tracking(8)
                    .foregroundStyle(Color.secondary)
                    .opacity(0.8)
            }
            .scaleEffect(scale * continuousScale)
            .opacity(opacity)
        }
        .onAppear {
            // Animate in
            withAnimation(.easeOut(duration: 1.2)) {
                opacity = 1.0
                scale = 1.0
            }
            
            // Continuous slow spatial zoom
            withAnimation(.linear(duration: 4.0).delay(0.5)) {
                continuousScale = 1.05
            }
            
            // Haptic bump when it lands
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.2) {
                let generator = UIImpactFeedbackGenerator(style: .soft)
                generator.impactOccurred()
            }
            
            // Bring in the glow slightly later
            withAnimation(.easeIn(duration: 1.5).delay(0.5)) {
                glowOpacity = 1.0
            }
        }
    }
}

#Preview {
    LaunchScreenView()
}
