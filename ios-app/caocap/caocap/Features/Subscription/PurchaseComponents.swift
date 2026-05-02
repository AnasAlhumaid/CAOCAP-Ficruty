import SwiftUI

// MARK: - Components

struct FeatureRow: View {
    let feature: FeatureItem
    
    var body: some View {
        HStack(spacing: 20) {
            ZStack {
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .fill(feature.color.opacity(0.15))
                    .frame(width: 48, height: 48)
                
                Image(systemName: feature.icon)
                    .font(.system(size: 22, weight: .semibold))
                    .foregroundStyle(feature.color)
                    .shadow(color: feature.color.opacity(0.3), radius: 5)
            }
            
            VStack(alignment: .leading, spacing: 4) {
                Text(LocalizedStringKey(feature.title))
                    .font(.system(size: 18, weight: .bold))
                    .foregroundStyle(.primary)
                Text(LocalizedStringKey(feature.subtitle))
                    .font(.system(size: 15))
                    .foregroundStyle(.secondary)
                    .fixedSize(horizontal: false, vertical: true)
            }
        }
    }
}

struct PlanCard: View {
    let id: String
    let title: LocalizedStringKey
    let price: String
    let subtitle: LocalizedStringKey
    var trialPeriod: LocalizedStringKey? = nil
    var isSelected: Bool
    var isBestValue: Bool = false
    var isLoading: Bool = false
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        Text(title)
                            .font(.system(size: 20, weight: .bold))
                            .foregroundStyle(.primary)
                        
                        if isBestValue {
                            Text(LocalizedStringKey("subscription.saveBadge"))
                                .font(.system(size: 10, weight: .black))
                                .padding(.horizontal, 8)
                                .padding(.vertical, 4)
                                .background(
                                    LinearGradient(colors: [Color(hex: "7C3AED"), Color(hex: "3B82F6")], startPoint: .leading, endPoint: .trailing)
                                )
                                .clipShape(Capsule())
                                .foregroundStyle(.white)
                        }
                    }
                    
                    Text(subtitle)
                        .font(.system(size: 14))
                        .foregroundStyle(.secondary)
                    
                    if let trialPeriod {
                        Text(trialPeriod)
                            .font(.system(size: 12, weight: .bold))
                            .foregroundStyle(Color(hex: "10B981"))
                            .padding(.top, 2)
                    }
                }
                
                Spacer()
                
                if isLoading {
                    ProgressView()
                        .tint(.white.opacity(0.5))
                        .scaleEffect(0.8)
                } else {
                    Text(price)
                        .font(.system(size: 22, weight: .bold, design: .rounded))
                        .foregroundStyle(.primary)
                }
                
                ZStack {
                    Circle()
                        .strokeBorder(isSelected ? Color(hex: "7C3AED") : Color.primary.opacity(0.1), lineWidth: 2)
                        .frame(width: 28, height: 28)
                    
                    if isSelected {
                        Circle()
                            .fill(LinearGradient(colors: [Color(hex: "7C3AED"), Color(hex: "3B82F6")], startPoint: .top, endPoint: .bottom))
                            .frame(width: 18, height: 18)
                            .transition(.scale.combined(with: .opacity))
                    }
                }
            }
            .padding(20)
            .background(
                ZStack {
                    RoundedRectangle(cornerRadius: 24, style: .continuous)
                        .fill(Color.primary.opacity(isSelected ? 0.08 : 0.03))
                    
                    if isSelected {
                        RoundedRectangle(cornerRadius: 24, style: .continuous)
                            .stroke(
                                LinearGradient(colors: [Color(hex: "7C3AED").opacity(0.6), Color(hex: "3B82F6").opacity(0.6)], startPoint: .topLeading, endPoint: .bottomTrailing),
                                lineWidth: 2
                            )
                    }
                }
            )
            .scaleEffect(isSelected ? 1.02 : 1.0)
        }
        .buttonStyle(.plain)
        .disabled(isLoading)
    }
}

struct MeshBackgroundView: View {
    @State private var animate = false
    
    var body: some View {
        ZStack {
            // Blob 1
            Circle()
                .fill(Color(hex: "7C3AED").opacity(0.3))
                .frame(width: 400, height: 400)
                .blur(radius: 80)
                .offset(x: animate ? 100 : -100, y: animate ? -200 : -100)
            
            // Blob 2
            Circle()
                .fill(Color(hex: "3B82F6").opacity(0.2))
                .frame(width: 500, height: 500)
                .blur(radius: 100)
                .offset(x: animate ? -150 : 150, y: animate ? 150 : -50)
            
            // Blob 3
            Circle()
                .fill(Color(hex: "EC4899").opacity(0.15))
                .frame(width: 300, height: 300)
                .blur(radius: 70)
                .offset(x: animate ? 50 : -50, y: animate ? 300 : 200)
        }
        .onAppear {
            withAnimation(.easeInOut(duration: 10).repeatForever(autoreverses: true)) {
                animate = true
            }
        }
    }
}

struct FeatureItem: Identifiable {
    let id = UUID()
    let icon: String
    let title: String
    let subtitle: String
    let color: Color
}
