import SwiftUI
import StoreKit
import OSLog

/// StoreKit paywall for CAOCAP Pro. The view owns presentation state while
/// `SubscriptionManager` owns products, entitlements, and transactions.
struct PurchaseView: View {
    @Environment(\.dismiss) var dismiss
    private let logger = Logger(subsystem: "Ficruty", category: "PurchaseView")
    @State private var manager = SubscriptionManager.shared
    @State private var selectedProductID: String = "CAOCAP_Pro_Yearly"
    @State private var isPurchasing = false
    @State private var appearAnimation = false
    @State private var purchaseError: String?
    @State private var showSuccess = false
    
    // Mock features based on CAOCAP Pro
    let features = [
        FeatureItem(icon: "sparkles", title: "AI Co-Captain", subtitle: "Unlimited intelligent design suggestions", color: Color(hex: "A855F7")),
        FeatureItem(icon: "cloud.fill", title: "Cloud Sync", subtitle: "Access your projects from any device", color: Color(hex: "10B981")),
        FeatureItem(icon: "paintpalette.fill", title: "Custom Themes", subtitle: "Exclusive premium UI themes and colors", color: Color(hex: "F59E0B")),
    ]
    
    var body: some View {
        ZStack {
            // MARK: - Background
            Color(uiColor: .systemBackground).ignoresSafeArea()
            
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
                            Text(LocalizedStringKey("subscription.paywallBadge"))
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
                                .foregroundStyle(.primary)
                            
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
                        if manager.isLoading && manager.products.isEmpty {
                            ProgressView()
                                .tint(.white)
                                .padding()
                        } else {
                            PlanCard(
                                id: "CAOCAP_Pro_Monthly",
                                title: "CAOCAP Pro Monthly",
                                price: productPrice(for: "CAOCAP_Pro_Monthly"),
                                subtitle: "Billed monthly",
                                trialPeriod: "7 DAYS FREE",
                                isSelected: selectedProductID == "CAOCAP_Pro_Monthly",
                                isLoading: manager.isLoading,
                                action: { withAnimation(.spring()) { selectedProductID = "CAOCAP_Pro_Monthly" } }
                            )
                            
                            PlanCard(
                                id: "CAOCAP_Pro_Yearly",
                                title: "CAOCAP Pro Yearly",
                                price: productPrice(for: "CAOCAP_Pro_Yearly"),
                                subtitle: "Billed annually",
                                trialPeriod: "14 DAYS FREE",
                                isSelected: selectedProductID == "CAOCAP_Pro_Yearly",
                                isBestValue: true,
                                isLoading: manager.isLoading,
                                action: { withAnimation(.spring()) { selectedProductID = "CAOCAP_Pro_Yearly" } }
                            )
                        }
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
                                        Text(actionButtonTitle)
                                            .font(.system(size: 18, weight: .bold))
                                        Image(systemName: manager.isSubscribed ? "gearshape.fill" : "sparkles")
                                            .font(.system(size: 18, weight: .bold))
                                    }
                                    .foregroundStyle(.white)
                                }
                            }
                        }
                        .padding(.horizontal, 50)
                        .scaleEffect(isPurchasing ? 0.95 : 1.0)
                        .disabled(isPurchasing || (manager.isLoading && !manager.isSubscribed))
                        .animation(.spring(), value: isPurchasing)
                        
                        // Footer Links
                        HStack(spacing: 20) {
                            Button("Restore Purchases") {
                                Task { try? await manager.restorePurchases() }
                            }
                            Circle().frame(width: 3, height: 3)
                            Link("Terms of Use (EULA)", destination: URL(string: "https://www.azzam.ai/caocap/terms")!)
                            Circle().frame(width: 3, height: 3)
                            Link("Privacy Policy", destination: URL(string: "https://www.azzam.ai/caocap/privacy")!)
                        }
                        .font(.system(size: 13, weight: .medium))
                        .foregroundStyle(.secondary)
                        
                        // Mandatory Disclosure
                        VStack(spacing: 8) {
                            Text("Subscription automatically renews unless auto-renew is turned off at least 24-hours before the end of the current period. Payment will be charged to your iTunes Account at confirmation of purchase. Account will be charged for renewal within 24-hours prior to the end of the current period. Subscriptions may be managed and auto-renewal may be turned off by going to your Account Settings after purchase.")
                                .multilineTextAlignment(.center)
                                .font(.system(size: 11))
                                .foregroundStyle(.secondary)
                                .padding(16)
                                .background(
                                    RoundedRectangle(cornerRadius: 16)
                                        .fill(Color.primary.opacity(0.03))
                                )
                        }
                        .padding(.horizontal, 20)
                        .padding(.top, 10)
                    }
                    .padding(.bottom, 60)
                    .opacity(appearAnimation ? 1 : 0)
                    .animation(.easeIn.delay(0.7), value: appearAnimation)
                }
            }
            .padding(.horizontal, 20)
            
            // Success Overlay
            if showSuccess {
                Color.black.opacity(0.8).ignoresSafeArea()
                VStack(spacing: 20) {
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 80))
                        .foregroundStyle(.green)
                    Text("Welcome to Pro!")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundStyle(.primary)
                }
                .transition(.scale.combined(with: .opacity))
            }
            
            // Close Button
            VStack {
                HStack {
                    Spacer()
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "xmark")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundStyle(.primary.opacity(0.6))
                            .padding(12)
                            .background(.primary.opacity(0.1))
                            .clipShape(Circle())
                            .overlay(Circle().stroke(.primary.opacity(0.1), lineWidth: 1))
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
        .alert("Purchase Failed", isPresented: Binding(get: { purchaseError != nil }, set: { if !$0 { purchaseError = nil } })) {
            Button("OK", role: .cancel) { }
        } message: {
            if let error = purchaseError {
                Text(error)
            }
        }
    }
    
    /// Uses StoreKit's localized price when available, with launch-copy
    /// fallbacks so the paywall remains readable while products load.
    private func productPrice(for id: String) -> String {
        manager.products.first(where: { $0.id == id })?.displayPrice ?? (id.localizedCaseInsensitiveContains("monthly") ? "$9.99" : "$79.99")
    }

    private var actionButtonTitle: String {
        if manager.isSubscribed {
            return LocalizationManager.shared.localizedString("Manage Subscription")
        }

        let key = selectedProductID == "CAOCAP_Pro_Yearly"
            ? "Start 14-Day Free Trial"
            : "Start 7-Day Free Trial"
        return LocalizationManager.shared.localizedString(key)
    }
    
    /// Routes subscribed users to Apple's subscription management page and
    /// starts a StoreKit purchase for non-subscribed users.
    private func purchaseAction() {
        if manager.isSubscribed {
            // Redirect to App Store Manage Subscriptions
            if let url = URL(string: "https://apps.apple.com/account/subscriptions") {
                UIApplication.shared.open(url)
            }
            return
        }
        
        let generator = UIImpactFeedbackGenerator(style: .medium)
        generator.impactOccurred()
        
        guard let product = manager.products.first(where: { $0.id == selectedProductID }) else {
            purchaseError = LocalizationManager.shared.localizedString("Product not found. Please try again later.")
            return
        }
        
        isPurchasing = true
        Task {
            do {
                let transaction = try await manager.purchase(product)
                isPurchasing = false
                
                if transaction != nil {
                    // A verified transaction is the only successful purchase path.
                    UINotificationFeedbackGenerator().notificationOccurred(.success)
                    withAnimation { showSuccess = true }
                    try? await Task.sleep(nanoseconds: 1_500_000_000)
                    dismiss()
                } else {
                    // StoreKit reports cancellation and pending approval as nil;
                    // neither should be presented as a purchase failure.
                }
            } catch {
                isPurchasing = false
                
                // Ignore cancellation errors from throwing
                let errorString = error.localizedDescription.lowercased()
                if errorString.contains("cancel") || errorString.contains("usercancelled") {
                    logger.info("Purchase cancelled by user.")
                    return
                }
                
                logger.error("Purchase failed: \(error)")
                purchaseError = error.localizedDescription
            }
        }
    }
}



#Preview {
    PurchaseView()
        .preferredColorScheme(.dark)
}
