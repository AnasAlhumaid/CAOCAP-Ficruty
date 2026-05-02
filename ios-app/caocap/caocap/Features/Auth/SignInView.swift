import SwiftUI
import FirebaseAuth

// MARK: - SignInView

/// A premium glassmorphic sign-in sheet.
/// Shown when the user wants to save their work across devices.
/// Links the current anonymous session to a real identity — no data is lost.
struct SignInView: View {
    @Environment(AuthenticationManager.self) private var authManager
    @Environment(\.dismiss) private var dismiss

    @State private var appleCoordinator = AppleSignInCoordinator()
    @State private var googleCoordinator = GoogleSignInCoordinator()
    @State private var isLoading: Bool = false
    @State private var errorMessage: String? = nil

    // Staggered entrance animation state
    @State private var headerVisible = false
    @State private var button1Visible = false
    @State private var button2Visible = false
    @State private var button3Visible = false
    @State private var footerVisible = false

    var body: some View {
        VStack(spacing: 0) {

            Spacer().frame(height: 36)

            // Header
            VStack(spacing: 12) {
                ZStack {
                    Circle()
                        .fill(
                            LinearGradient(
                                colors: [Color(hex: "5E5CE6").opacity(0.3), Color(hex: "BF5AF2").opacity(0.2)],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        )
                        .frame(width: 80, height: 80)
                        .blur(radius: 8)

                    Image(systemName: "person.crop.circle.badge.checkmark")
                        .font(.system(size: 48, weight: .light))
                        .foregroundStyle(
                            LinearGradient(
                                colors: [Color(hex: "5E5CE6"), Color(hex: "BF5AF2")],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        )
                }

                Text("Save Your Work")
                    .font(.system(size: 28, weight: .bold, design: .rounded))
                    .foregroundColor(.primary)

                Text("Sign in to sync your projects across\nall your devices. Your current work is preserved.")
                    .font(.system(size: 15, weight: .regular))
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .lineSpacing(5)
            }
            .padding(.horizontal, 32)
            .opacity(headerVisible ? 1 : 0)
            .offset(y: headerVisible ? 0 : 12)

            Spacer().frame(height: 44)

            // Sign-In Buttons
            VStack(spacing: 12) {
                SignInButton(
                    label: "Continue with Apple",
                    foreground: .primary,
                    background: Color(uiColor: .label),
                    isApple: true,
                    leadingView: {
                        Image(systemName: "apple.logo")
                            .font(.system(size: 18, weight: .semibold))
                            .foregroundStyle(Color(uiColor: .systemBackground))
                    }
                ) {
                    await handleAppleSignIn()
                }
                .opacity(button1Visible ? 1 : 0)
                .offset(y: button1Visible ? 0 : 16)

                SignInButton(
                    label: "Continue with Google",
                    foreground: .primary,
                    background: Color(uiColor: .secondarySystemBackground),
                    stroke: Color.primary.opacity(0.1),
                    leadingView: { GoogleLogoView() }
                ) {
                    await handleGoogleSignIn()
                }
                .opacity(button2Visible ? 1 : 0)
                .offset(y: button2Visible ? 0 : 16)

                SignInButton(
                    label: "Continue with GitHub",
                    foreground: .primary,
                    background: Color(uiColor: .secondarySystemBackground),
                    stroke: Color.primary.opacity(0.1),
                    leadingView: { GitHubLogoView() }
                ) {
                    await handleGitHubSignIn()
                }
                .opacity(button3Visible ? 1 : 0)
                .offset(y: button3Visible ? 0 : 16)
            }
            .padding(.horizontal, 24)

            // Error message
            if let errorMessage {
                Text(errorMessage)
                    .font(.system(size: 13, weight: .medium))
                    .foregroundColor(Color(hex: "FF6B6B"))
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 32)
                    .padding(.top, 16)
                    .transition(.opacity.combined(with: .move(edge: .bottom)))
            }

            Spacer(minLength: 24)

            // Privacy note
            VStack(spacing: 4) {
                Text("By continuing, you agree to our")
                    .foregroundColor(.secondary.opacity(0.7))
                
                HStack(spacing: 4) {
                    Button {
                        if let url = URL(string: "https://www.azzam.ai/caocap/terms") {
                            UIApplication.shared.open(url)
                        }
                    } label: {
                        Text("Terms of Service")
                            .underline()
                            .foregroundColor(.primary.opacity(0.8))
                    }
                    
                    Text("and")
                        .foregroundColor(.secondary.opacity(0.7))
                    
                    Button {
                        if let url = URL(string: "https://www.azzam.ai/caocap/privacy") {
                            UIApplication.shared.open(url)
                        }
                    } label: {
                        Text("Privacy Policy")
                            .underline()
                            .foregroundColor(.primary.opacity(0.8))
                    }
                }
            }
            .font(.system(size: 11, weight: .semibold))
            .multilineTextAlignment(.center)
            .padding(.horizontal, 32)
            .opacity(footerVisible ? 1 : 0)
            .padding(.bottom, 16)
            .safeAreaPadding(.bottom)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background {
            // Layered background — sits behind the content without affecting layout
            ZStack {
                Color(uiColor: .systemBackground)

                Circle()
                    .fill(
                        RadialGradient(
                            colors: [Color(hex: "5E5CE6").opacity(0.3), .clear],
                            center: .center,
                            startRadius: 0,
                            endRadius: 300
                        )
                    )
                    .frame(width: 600)
                    .offset(y: -140)
                    .blur(radius: 24)
            }
            .ignoresSafeArea()
        }
        .overlay {
            // Loading overlay
            if isLoading {
                Color.black.opacity(0.55)
                    .ignoresSafeArea()
                VStack(spacing: 16) {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .primary))
                        .scaleEffect(1.4)
                    Text("Signing in...")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(.secondary)
                }
            }
        }
        .onAppear { animateEntrance() }
    }

    // MARK: - Staggered Entrance

    private func animateEntrance() {
        withAnimation(.spring(response: 0.5, dampingFraction: 0.8).delay(0.05)) {
            headerVisible = true
        }
        withAnimation(.spring(response: 0.5, dampingFraction: 0.8).delay(0.18)) {
            button1Visible = true
        }
        withAnimation(.spring(response: 0.5, dampingFraction: 0.8).delay(0.26)) {
            button2Visible = true
        }
        withAnimation(.spring(response: 0.5, dampingFraction: 0.8).delay(0.34)) {
            button3Visible = true
        }
        withAnimation(.easeOut(duration: 0.4).delay(0.42)) {
            footerVisible = true
        }
    }

    // MARK: - Actions

    private func handleAppleSignIn() async {
        await perform {
            let credential = try await appleCoordinator.signIn()
            try await authManager.signInWithApple(credential: credential)
            dismiss()
        }
    }

    private func handleGoogleSignIn() async {
        await perform {
            let credential = try await googleCoordinator.signIn()
            try await authManager.signInWithGoogle(credential: credential)
            dismiss()
        }
    }

    private func handleGitHubSignIn() async {
        await perform {
            try await authManager.signInWithGitHub()
            dismiss()
        }
    }

    /// Shared loading and error boundary for provider flows. The provider
    /// coordinators only return credentials; `AuthenticationManager` decides
    /// whether to link or sign into an existing account.
    private func perform(_ action: () async throws -> Void) async {
        withAnimation { isLoading = true; errorMessage = nil }
        do {
            try await action()
        } catch {
            withAnimation { errorMessage = error.localizedDescription }
        }
        withAnimation { isLoading = false }
    }
}

// MARK: - Google Logo

/// Google's official "G" rendered with brand colors.
private struct GoogleLogoView: View {
    var size: CGFloat = 20

    var body: some View {
        ZStack {
            Circle()
                .fill(Color.white)
                .frame(width: size, height: size)

            Text("G")
                .font(.system(size: size * 0.65, weight: .bold, design: .rounded))
                .foregroundStyle(
                    LinearGradient(
                        colors: [Color(hex: "4285F4"), Color(hex: "EA4335")],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
        }
    }
}

// MARK: - GitHub Logo

/// GitHub octocat-style mark using an SF Symbol.
private struct GitHubLogoView: View {
    var size: CGFloat = 20

    var body: some View {
        ZStack {
            Circle()
                .fill(Color.white)
                .frame(width: size, height: size)

            Image(systemName: "chevron.left.forwardslash.chevron.right")
                .font(.system(size: size * 0.42, weight: .bold))
                .foregroundColor(.black)
        }
    }
}

// MARK: - SignInButton

private struct SignInButton<LeadingView: View>: View {
    let label: LocalizedStringKey
    let foreground: Color
    let background: Color
    var stroke: Color = .clear
    var isApple: Bool = false
    let leadingView: () -> LeadingView
    let action: () async -> Void

    @State private var isPressed = false

    var body: some View {
        Button {
            Task { await action() }
        } label: {
            HStack(spacing: 12) {
                leadingView()
                    .frame(width: 22, height: 22)

                Text(label)
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundStyle(isApple ? Color(uiColor: .systemBackground) : foreground)

                Spacer()
            }
            .foregroundColor(foreground)
            .padding(.horizontal, 20)
            .frame(maxWidth: .infinity)
            .frame(height: 56)
            .background(background)
            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: 16, style: .continuous)
                    .stroke(stroke, lineWidth: 1)
            )
            .scaleEffect(isPressed ? 0.97 : 1.0)
            .animation(.spring(response: 0.2, dampingFraction: 0.6), value: isPressed)
        }
        .buttonStyle(PressableButtonStyle(isPressed: $isPressed))
    }
}

// MARK: - PressableButtonStyle

private struct PressableButtonStyle: ButtonStyle {
    @Binding var isPressed: Bool
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .onChange(of: configuration.isPressed) { _, pressed in
                isPressed = pressed
            }
    }
}

#Preview {
    SignInView()
        .environment(AuthenticationManager())
}
