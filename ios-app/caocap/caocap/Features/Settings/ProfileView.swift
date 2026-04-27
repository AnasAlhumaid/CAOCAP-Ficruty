import SwiftUI
import FirebaseAuth

struct ProfileView: View {
    @Environment(AuthenticationManager.self) private var authManager
    @Environment(\.dismiss) private var dismiss
    @AppStorage("app_theme") private var selectedTheme = "System"
    
    var onSignIn: (() -> Void)? = nil
    var onPro: (() -> Void)? = nil
    
    @State private var showingDeleteAlert = false
    @State private var showingSignOutAlert = false
    
    var body: some View {
        NavigationStack {
            ZStack {
                // MARK: - Background
                Color(uiColor: .systemBackground).ignoresSafeArea()
                
                // Subtle Glow
                Circle()
                    .fill(Color(hex: "5E5CE6").opacity(0.15))
                    .frame(width: 400, height: 400)
                    .blur(radius: 60)
                    .offset(x: -150, y: -200)
                
                ScrollView {
                    VStack(spacing: 32) {
                        // MARK: - Profile Header
                        VStack(spacing: 16) {
                            ZStack {
                                Circle()
                                    .fill(.ultraThinMaterial)
                                    .frame(width: 100, height: 100)
                                    .overlay(Circle().stroke(.primary.opacity(0.1), lineWidth: 1))
                                
                                Image(systemName: "person.fill")
                                    .font(.system(size: 40))
                                    .foregroundStyle(.primary.opacity(0.8))
                                
                                if !authManager.isAnonymous {
                                    VStack {
                                        Spacer()
                                        HStack {
                                            Spacer()
                                            Image(systemName: "checkmark.seal.fill")
                                                .foregroundStyle(.blue)
                                                .background(Circle().fill(.white).padding(2))
                                                .font(.system(size: 24))
                                        }
                                    }
                                    .frame(width: 100, height: 100)
                                }
                            }
                            
                            VStack(spacing: 4) {
                                Group {
                                    switch authManager.authState {
                                    case .loading:
                                        Text("Syncing Session...")
                                    case .anonymous:
                                        Text("Guest Workspace")
                                    case .authenticated:
                                        Text("Authenticated User")
                                    case .failed(let reason):
                                        Text("Auth Error")
                                            .onAppear { print("Auth failed: \(reason)") }
                                    }
                                }
                                .font(.system(size: 20, weight: .bold, design: .rounded))
                                .foregroundStyle(.primary)
                                
                                if let email = Auth.auth().currentUser?.email {
                                    Text(email)
                                        .font(.system(size: 14))
                                        .foregroundStyle(.secondary)
                                } else {
                                    let uidPreview = authManager.currentUID.map { String($0.prefix(8)) }
                                        ?? LocalizationManager.shared.localizedString("Unknown")
                                    Text(
                                        LocalizationManager.shared.localizedString(
                                            "UID: %@...",
                                            arguments: [uidPreview]
                                        )
                                    )
                                        .font(.system(size: 12, design: .monospaced))
                                        .foregroundStyle(.secondary)
                                }
                            }
                        }
                        .padding(.top, 20)
                        
                        // MARK: - Sections
                        VStack(spacing: 24) {
                            // Account Section
                            SettingsSection("Account") {
                                if authManager.isAnonymous {
                                    SettingsRow(icon: "person.badge.key.fill", title: "Link Account", subtitle: "Save your work permanently", color: .orange) {
                                        dismiss()
                                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                                            onSignIn?()
                                        }
                                    }
                                }
                                
                                if authManager.isAuthenticated {
                                    SettingsRow(icon: "rectangle.portrait.and.arrow.right", title: "Sign Out", color: .red) {
                                        showingSignOutAlert = true
                                    }
                                }
                                
                                SettingsRow(icon: "trash.fill", title: "Delete Account", subtitle: "Permanently remove all data", color: .red) {
                                    showingDeleteAlert = true
                                }
                            }
                            
                            // Pro Section
                            SettingsSection("Monetization") {
                                SettingsRow(icon: "crown.fill", title: "CAOCAP Pro", subtitle: "Manage your subscription", color: .yellow) {
                                    dismiss()
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                                        onPro?()
                                    }
                                }
                            }
                            
                            // Support & Legal
                            SettingsSection("Support & Legal") {
                                SettingsRow(icon: "questionmark.circle.fill", title: "Contact Support", color: .blue) {
                                    if let url = URL(string: "https://www.azzam.ai/caocap/support") {
                                        UIApplication.shared.open(url)
                                    }
                                }
                                
                                SettingsRow(icon: "shield.lefthalf.filled", title: "Privacy Policy", color: .secondary) {
                                    if let url = URL(string: "https://www.azzam.ai/caocap/privacy") {
                                        UIApplication.shared.open(url)
                                    }
                                }
                                
                                SettingsRow(icon: "doc.text.fill", title: "Terms of Service", color: .secondary) {
                                    if let url = URL(string: "https://www.azzam.ai/caocap/terms") {
                                        UIApplication.shared.open(url)
                                    }
                                }
                            }
                        }
                        .padding(.horizontal, 20)
                        
                        // MARK: - Footer
                        VStack(spacing: 8) {
                            Text(
                                LocalizationManager.shared.localizedString(
                                    "CAOCAP v%@",
                                    arguments: [Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0.0"]
                                )
                            )
                                .font(.system(size: 12, weight: .bold))
                                .foregroundStyle(.secondary)
                            Text("Made for the spatial era.")
                                .font(.system(size: 10))
                                .foregroundStyle(.tertiary)
                        }
                        .padding(.vertical, 40)
                    }
                }
            }
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Text("Profile")
                        .font(.system(size: 24, weight: .black, design: .rounded))
                        .foregroundStyle(.primary)
                }
                
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "xmark")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundStyle(.primary.opacity(0.6))
                            .padding(8)
                            .background(.primary.opacity(0.1))
                            .clipShape(Circle())
                    }
                }
            }
            .alert("Sign Out", isPresented: $showingSignOutAlert) {
                Button("Cancel", role: .cancel) { }
                Button("Sign Out", role: .destructive) {
                    authManager.signOut()
                    dismiss()
                }
            } message: {
                Text("Are you sure you want to sign out? Your local projects will remain safe.")
            }
            .alert("Delete Account", isPresented: $showingDeleteAlert) {
                Button("Cancel", role: .cancel) { }
                Button("Delete Everything", role: .destructive) {
                    Task {
                        do {
                            try await authManager.deleteAccount()
                            dismiss()
                        } catch {
                            // In a real app, show a re-auth prompt here
                            print("Deletion failed: \(error)")
                        }
                    }
                }
            } message: {
                Text("This action cannot be undone. All your projects, nodes, and subscription data will be permanently deleted from our servers.")
            }
            .preferredColorScheme(currentColorScheme)
        }
    }
    
    private var currentColorScheme: ColorScheme? {
        switch selectedTheme {
        case "Light": return .light
        case "Dark": return .dark
        default: return nil
        }
    }
}

#Preview {
    ProfileView()
        .environment(AuthenticationManager())
}
