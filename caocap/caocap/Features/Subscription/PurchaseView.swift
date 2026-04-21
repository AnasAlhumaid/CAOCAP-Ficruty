import SwiftUI
import StoreKit

struct PurchaseView: View {
    @Environment(\.dismiss) var dismiss
    @StateObject private var manager = SubscriptionManager.shared
    @State private var selectedProductID: String = "com.caocap.pro.yearly"
    @State private var isPurchasing = false
    @State private var appearAnimation = false
    
    // Mock features based on CAOCAP Pro
    let features = [
        FeatureItem(icon: "sparkles", title: "AI Co-Captain", subtitle: "Unlimited intelligent design suggestions", color: Color(hex: "A855F7")),
        FeatureItem(icon: "cloud.fill", title: "Cloud Sync", subtitle: "Access your projects from any device", color: Color(hex: "10B981")),
        FeatureItem(icon: "paintpalette.fill", title: "Custom Themes", subtitle: "Exclusive premium UI themes and colors", color: Color(hex: "F59E0B")),
    ]
    
    var body: some View {
        ZStack {
            // MARK: - Background
            Color(hex: "050505").ignoresSafeArea()
            
            // Animated Mesh Background
            MeshBackgroundView()
                .opacity(0.6)
            
            ScrollView(showsIndicators: false) {
                VStack(spacing: 48) {
                    // MARK: - Header
                    VStack(spacing: 20) {
                        ZStack {
                            // Pulsing Glow
                            Circle()
                                .fill(LinearGradient(colors: [Color(hex: "7C3AED"), Color(hex: "3B82F6")], startPoint: .topLeading, endPoint: .bottomTrailing))
                                .frame(width: 100, height: 100)
                                .blur(radius: appearAnimation ? 30 : 10)
                                .scaleEffect(appearAnimation ? 1.2 : 0.8)
                                .opacity(0.4)
                                .animation(.easeInOut(duration: 3).repeatForever(autoreverses: true), value: appearAnimation)
                            
                            Image(systemName: "crown.fill")
                                .font(.system(size: 48, weight: .black))
                                .foregroundStyle(
                                    LinearGradient(
                                        colors: [.white, .white.opacity(0.7)],
                                        startPoint: .top,
                                        endPoint: .bottom
                                    )
                                )
                                .shadow(color: .black.opacity(0.2), radius: 5, x: 0, y: 5)
                                .scaleEffect(appearAnimation ? 1.0 : 0.5)
                                .rotationEffect(.degrees(appearAnimation ? 0 : -20))
                        }
                        
                        VStack(spacing: 8) {
                            Text("CAOCAP PRO")
                                .font(.system(size: 14, weight: .black))
                                .kerning(4)
                                .foregroundStyle(
                                    LinearGradient(
                                        colors: [Color(hex: "A855F7"), Color(hex: "3B82F6")],
                                        startPoint: .leading,
                                        endPoint: .trailing
                                    )
                                )
                            
                            Text("Unlimited Creativity")
                                .font(.system(size: 34, weight: .bold, design: .rounded))
                                .foregroundStyle(.white)
                            
                            Text("The ultimate toolkit for spatial designers and vibecoders.")
                                .font(.system(size: 17))
                                .foregroundStyle(.secondary)
                                .multilineTextAlignment(.center)
                                .padding(.horizontal, 40)
                        }
                        .opacity(appearAnimation ? 1 : 0)
                        .offset(y: appearAnimation ? 0 : 20)
                    }
                    .padding(.top, 60)
                    
                    // MARK: - Features
                    VStack(alignment: .leading, spacing: 24) {
                        ForEach(Array(features.enumerated()), id: \.offset) { index, feature in
                            FeatureRow(feature: feature)
                                .opacity(appearAnimation ? 1 : 0)
                                .offset(x: appearAnimation ? 0 : -20)
                                .animation(.spring(response: 0.6, dampingFraction: 0.8).delay(Double(index) * 0.1), value: appearAnimation)
                        }
                    }
                    
                    // MARK: - Plans
                    VStack(spacing: 16) {
                        PlanCard(
                            id: "com.caocap.pro.monthly",
                            title: "Monthly",
                            price: "$9.99",
                            subtitle: "Billed monthly",
                            isSelected: selectedProductID == "com.caocap.pro.monthly",
                            action: { withAnimation(.spring()) { selectedProductID = "com.caocap.pro.monthly" } }
                        )
                        
                        PlanCard(
                            id: "com.caocap.pro.yearly",
                            title: "Yearly",
                            price: "$79.99",
                            subtitle: "Billed annually • Save 33%",
                            isSelected: selectedProductID == "com.caocap.pro.yearly",
                            isBestValue: true,
                            action: { withAnimation(.spring()) { selectedProductID = "com.caocap.pro.yearly" } }
                        )
                    }
                    .padding(.horizontal, 50)
                    .opacity(appearAnimation ? 1 : 0)
                    .offset(y: appearAnimation ? 0 : 30)
                    .animation(.spring().delay(0.5), value: appearAnimation)
                    
                    // MARK: - Action
                    VStack(spacing: 20) {
                        Button(action: purchaseAction) {
                            ZStack {
                                RoundedRectangle(cornerRadius: 20, style: .continuous)
                                    .fill(
                                        LinearGradient(
                                            colors: [Color(hex: "7C3AED"), Color(hex: "3B82F6")],
                                            startPoint: .leading,
                                            endPoint: .trailing
                                        )
                                    )
                                    .frame(height: 64)
                                    .shadow(color: Color(hex: "7C3AED").opacity(0.4), radius: 20, x: 0, y: 10)
                                
                                if isPurchasing {
                                    ProgressView()
                                        .tint(.white)
                                } else {
                                    HStack {
                                        Text("Unlock Everything")
                                            .font(.system(size: 20, weight: .bold))
                                        Image(systemName: "arrow.right")
                                            .font(.system(size: 18, weight: .bold))
                                    }
                                    .foregroundStyle(.white)
                                }
                            }
                        }
                        .padding(.horizontal, 50)
                        .scaleEffect(isPurchasing ? 0.95 : 1.0)
                        .animation(.spring(), value: isPurchasing)
                        
                        // Footer Links
                        HStack(spacing: 20) {
                            Button("Restore Purchases") {
                                Task { try? await manager.restorePurchases() }
                            }
                            Circle().frame(width: 3, height: 3)
                            Link("Terms", destination: URL(string: "https://caocap.app/terms")!)
                            Circle().frame(width: 3, height: 3)
                            Link("Privacy", destination: URL(string: "https://caocap.app/privacy")!)
                        }
                        .font(.system(size: 13, weight: .medium))
                        .foregroundStyle(.secondary)
                    }
                    .padding(.bottom, 60)
                    .opacity(appearAnimation ? 1 : 0)
                    .animation(.easeIn.delay(0.7), value: appearAnimation)
                }
            }
            .padding(.horizontal, 20)
            
            // Close Button
            VStack {
                HStack {
                    Spacer()
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "xmark")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundStyle(.white.opacity(0.6))
                            .padding(12)
                            .background(.white.opacity(0.1))
                            .clipShape(Circle())
                            .overlay(Circle().stroke(.white.opacity(0.1), lineWidth: 1))
                    }
                    .padding()
                }
                Spacer()
            }
        }
        .onAppear {
            withAnimation(.spring(response: 0.8, dampingFraction: 0.7)) {
                appearAnimation = true
            }
        }
        .task {
            await manager.fetchProducts()
        }
        .preferredColorScheme(.dark)
    }
    
    private func purchaseAction() {
        // UI Feedback
        let generator = UIImpactFeedbackGenerator(style: .medium)
        generator.impactOccurred()
        
        guard let product = manager.products.first(where: { $0.id == selectedProductID }) else {
            isPurchasing = true
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                isPurchasing = false
                dismiss()
            }
            return
        }
        
        isPurchasing = true
        Task {
            do {
                _ = try await manager.purchase(product)
                isPurchasing = false
                dismiss()
            } catch {
                print("Purchase failed: \(error)")
                isPurchasing = false
            }
        }
    }
}

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
                Text(feature.title)
                    .font(.system(size: 18, weight: .bold))
                    .foregroundStyle(.white)
                Text(feature.subtitle)
                    .font(.system(size: 15))
                    .foregroundStyle(.secondary)
                    .fixedSize(horizontal: false, vertical: true)
            }
        }
    }
}

struct PlanCard: View {
    let id: String
    let title: String
    let price: String
    let subtitle: String
    var isSelected: Bool
    var isBestValue: Bool = false
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        Text(title)
                            .font(.system(size: 20, weight: .bold))
                            .foregroundStyle(.white)
                        
                        if isBestValue {
                            Text("SAVE 33%")
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
                }
                
                Spacer()
                
                Text(price)
                    .font(.system(size: 22, weight: .bold, design: .rounded))
                    .foregroundStyle(.white)
                
                ZStack {
                    Circle()
                        .strokeBorder(isSelected ? Color(hex: "7C3AED") : Color.white.opacity(0.2), lineWidth: 2)
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
                        .fill(.white.opacity(isSelected ? 0.08 : 0.03))
                    
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

#Preview {
    PurchaseView()
        .preferredColorScheme(.dark)
}
