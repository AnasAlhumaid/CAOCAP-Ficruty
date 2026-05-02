import SwiftUI

/// A pulsing ring and instruction label that highlights a specific area of the canvas.
struct FocusRingOverlay: View {
    let step: OnboardingStep
    let screenPosition: CGPoint
    
    @State private var pulse = false
    
    var body: some View {
        ZStack {
            // The pulsing ring
            Circle()
                .stroke(Color.blue, lineWidth: 3)
                .frame(width: step.spotlightRadius * 2, height: step.spotlightRadius * 2)
                .scaleEffect(pulse ? 1.1 : 1.0)
                .opacity(pulse ? 0.6 : 1.0)
                .shadow(color: .blue.opacity(0.5), radius: pulse ? 10 : 5)
            
            // The instruction label
            VStack {
                Spacer()
                    .frame(height: step.spotlightRadius * 2 + 40)
                
                Text(step.label)
                    .font(.system(size: 16, weight: .semibold, design: .rounded))
                    .foregroundColor(.white)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                    .background {
                        Capsule()
                            .fill(.ultraThinMaterial)
                            .shadow(color: .black.opacity(0.3), radius: 10)
                    }
                    .overlay {
                        Capsule()
                            .stroke(.white.opacity(0.2), lineWidth: 0.5)
                    }
                    .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .position(screenPosition)
        .onAppear {
            withAnimation(.easeInOut(duration: 1.2).repeatForever(autoreverses: true)) {
                pulse = true
            }
        }
    }
}
