import SwiftUI

struct FloatingCommandButton: View {
    @State private var position: CGPoint = .zero
    @State private var startPosition: CGPoint = .zero // Track where the drag started
    @State private var isDragging: Bool = false
    @Environment(\.colorScheme) var colorScheme
    
    var onTap: () -> Void // Callback for command palette
    
    // Padding from screen edges
    private let padding: CGFloat = 35
    private let buttonSize: CGFloat = 64
    
    var body: some View {
        GeometryReader { geometry in
            let size = geometry.size
            
            ZStack {
                // The Button (Custom implementation to avoid gesture conflicts)
                ZStack {
                    Circle()
                        .fill(.ultraThinMaterial)
                        .overlay(
                            Circle()
                                .stroke(Color.white.opacity(0.2), lineWidth: 0.5)
                        )
                        .shadow(color: Color.black.opacity(isDragging ? 0.3 : 0.2), radius: isDragging ? 15 : 10, x: 0, y: isDragging ? 8 : 5)
                    
                    Image(systemName: "sparkles")
                        .font(.system(size: 24, weight: .semibold))
                }
                .frame(width: buttonSize, height: buttonSize)
                .scaleEffect(isDragging ? 1.15 : 1.0)
                .position(position == .zero ? initialPosition(in: size) : position)
                .onTapGesture {
                    triggerHapticFeedback(.medium)
                    onTap()
                }
                .highPriorityGesture(
                    DragGesture(coordinateSpace: .named("floatingLayer"))
                        .onChanged { value in
                            if !isDragging {
                                startPosition = position == .zero ? initialPosition(in: size) : position
                                withAnimation(.interactiveSpring()) {
                                    isDragging = true
                                }
                                triggerHapticFeedback(.light)
                            }
                            
                            position = CGPoint(
                                x: startPosition.x + value.translation.width,
                                y: startPosition.y + value.translation.height
                            )
                        }
                        .onEnded { _ in
                            withAnimation(.spring(response: 0.4, dampingFraction: 0.7)) {
                                isDragging = false
                                snapToNearestPoint(in: size)
                            }
                        }
                )
            }
            .coordinateSpace(name: "floatingLayer")
            .onAppear {
                if position == .zero {
                    position = initialPosition(in: size)
                }
            }
            .onChange(of: geometry.size) { oldSize, newSize in
                withAnimation(.spring()) {
                    snapToNearestPoint(in: newSize)
                }
            }
        }
        .ignoresSafeArea() // Take control of the entire physical screen
    }
    
    private func initialPosition(in size: CGSize) -> CGPoint {
        CGPoint(
            x: size.width - padding - buttonSize/2,
            y: size.height - padding - buttonSize/2
        )
    }
    
    private func snapToNearestPoint(in size: CGSize) {
        let minX = padding + buttonSize/2
        let maxX = size.width - padding - buttonSize/2
        let minY = 60 + buttonSize/2 // Pushed down specifically for the notch
        let maxY = size.height - padding - buttonSize/2
        
        let centerX = size.width / 2
        let centerY = size.height / 2
        
        // 8 Snap Points (Corners and Edge-Centers) - Top points are pushed down
        let points: [CGPoint] = [
            CGPoint(x: minX, y: minY),
            CGPoint(x: centerX, y: minY),
            CGPoint(x: maxX, y: minY),
            
            CGPoint(x: minX, y: centerY),
            CGPoint(x: maxX, y: centerY),
            
            CGPoint(x: minX, y: maxY),
            CGPoint(x: centerX, y: maxY),
            CGPoint(x: maxX, y: maxY)
        ]
        
        let nearest = points.min(by: { 
            distance(from: $0, to: position) < distance(from: $1, to: position) 
        }) ?? points[7]
        
        position = nearest
        triggerHapticFeedback(.rigid)
    }
    
    private func distance(from: CGPoint, to: CGPoint) -> CGFloat {
        sqrt(pow(from.x - to.x, 2) + pow(from.y - to.y, 2))
    }
    
    private func triggerHapticFeedback(_ style: UIImpactFeedbackGenerator.FeedbackStyle) {
        let generator = UIImpactFeedbackGenerator(style: style)
        generator.prepare()
        generator.impactOccurred()
    }
}

#Preview {
    ZStack {
        Color.black.ignoresSafeArea()
        FloatingCommandButton(onTap: {})
    }
}
