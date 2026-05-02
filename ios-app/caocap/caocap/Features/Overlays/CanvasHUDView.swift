import SwiftUI

struct CanvasHUDView: View {
    let store: ProjectStore
    let viewportScale: CGFloat
    var onSignInTapped: (() -> Void)? = nil

    @Environment(AuthenticationManager.self) private var authManager
    @Environment(\.colorScheme) var colorScheme

    @State private var livePulse = false
    @State private var anonPulse = false
    var body: some View {
        VStack {
            HStack(spacing: 0) {
                // Info pill (non-interactive)
                HStack(spacing: 16) {
                    // Project Name
                    HStack(spacing: 8) {
                        Image(systemName: "folder.fill")
                            .font(.system(size: 12))
                            .foregroundStyle(.secondary)
                            .frame(width: 14)

                        Text(LocalizationManager.shared.localizedProjectName(store.projectName, fileName: store.fileName).uppercased())
                            .font(.system(size: 12, weight: .bold, design: .rounded))
                            .kerning(1)
                            .lineLimit(1)
                            .truncationMode(.middle)
                            .minimumScaleFactor(0.85)
                    }
                    .frame(maxWidth: 150, alignment: .center)
                    .clipped()

                    Divider().frame(height: 16)



                    // Zoom Level
                    HStack(spacing: 4) {
                        Text(viewportScale, format: .percent.precision(.fractionLength(0)))
                        .font(.system(size: 13, weight: .semibold, design: .monospaced))
                        .fixedSize(horizontal: true, vertical: false)

                        Image(systemName: "magnifyingglass")
                            .font(.system(size: 10))
                            .foregroundStyle(.secondary)
                    }

                    Divider().frame(height: 16)

                    // Save Status
                    HStack(spacing: 6) {
                        if store.isSaving {
                            Circle()
                                .fill(Color.orange)
                                .frame(width: 6, height: 6)
                                .shadow(color: .orange.opacity(0.5), radius: 3)
                            Text("SAVING")
                                .font(.system(size: 10, weight: .black))
                                .foregroundStyle(.orange)
                                .fixedSize(horizontal: true, vertical: false)
                        } else {
                            ZStack {
                                Circle()
                                    .fill(Color.green.opacity(0.35))
                                    .frame(width: 12, height: 12)
                                    .scaleEffect(livePulse ? 1.8 : 1.0)
                                    .opacity(livePulse ? 0 : 1)
                                Circle()
                                    .fill(Color.green)
                                    .frame(width: 6, height: 6)
                            }
                            .shadow(color: .green.opacity(0.6), radius: 4)
                            Text("LIVE")
                                .font(.system(size: 10, weight: .black))
                                .foregroundStyle(.green)
                                .fixedSize(horizontal: true, vertical: false)
                        }
                    }
                    .animation(.spring(), value: store.isSaving)
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 12)
                .allowsHitTesting(false)

                // Profile Indicator (interactive for anonymous)
                if authManager.isAnonymous {
                    Button(action: { onSignInTapped?() }) {
                        ZStack {
                            // Outer pulse ring
                            Circle()
                                .stroke(Color.orange.opacity(0.4), lineWidth: 1.5)
                                .frame(width: 22, height: 22)
                                .scaleEffect(anonPulse ? 1.5 : 1.0)
                                .opacity(anonPulse ? 0 : 0.8)
                            // Inner dot
                            Circle()
                                .fill(Color.orange)
                                .frame(width: 8, height: 8)
                                .shadow(color: .orange.opacity(0.6), radius: 4)
                        }
                        .frame(width: 28, height: 28)
                        .padding(.trailing, 16)
                        .padding(.leading, 4)
                    }
                } else if authManager.authState == .loading {
                    ProgressView()
                        .tint(.secondary)
                        .scaleEffect(0.7)
                        .frame(width: 28, height: 28)
                        .padding(.trailing, 16)
                        .padding(.leading, 4)
                } else if authManager.isAuthenticated {
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 16))
                        .foregroundStyle(
                            LinearGradient(
                                colors: [Color(hex: "5E5CE6"), Color(hex: "BF5AF2")],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        )
                        .padding(.trailing, 16)
                        .padding(.leading, 4)
                        .allowsHitTesting(false)
                }
            }
            .frame(maxWidth: UIScreen.main.bounds.width - 32)
            .background {
                Capsule()
                    .fill(.ultraThinMaterial)
                    .overlay {
                        Capsule()
                            .stroke(.white.opacity(0.1), lineWidth: 1)
                    }
            }
            .shadow(color: .black.opacity(0.2), radius: 20, x: 0, y: 10)

            Spacer()
        }
        .padding(.top, 60)
        .onAppear {
            // Pulse LIVE dot every 2 seconds
            withAnimation(.easeOut(duration: 1.2).repeatForever(autoreverses: false)) {
                livePulse = true
            }
            // Pulse anonymous dot every 1.8 seconds
            withAnimation(.easeOut(duration: 1.0).repeatForever(autoreverses: false).delay(0.3)) {
                anonPulse = true
            }
        }
    }
}

#Preview {
    ZStack {
        Color.black.ignoresSafeArea()
        CanvasHUDView(store: ProjectStore(), viewportScale: 1.0)
            .environment(AuthenticationManager())
    }
}
