import Foundation
import GoogleSignIn
import FirebaseAuth

// MARK: - GoogleSignInCoordinator

/// Handles the GIDSignIn flow and produces a Firebase AuthCredential.
///
/// Usage:
/// ```swift
/// let coordinator = GoogleSignInCoordinator()
/// let credential = try await coordinator.signIn()
/// try await authManager.signInWithGoogle(credential: credential)
/// ```
@MainActor
final class GoogleSignInCoordinator {

    /// Presents Google's native sign-in UI and converts the returned tokens into
    /// a Firebase credential for account linking or sign-in.
    func signIn() async throws -> AuthCredential {
        guard let presentingVC = topViewController() else {
            throw AuthError.missingPresentingViewController
        }

        let result = try await GIDSignIn.sharedInstance.signIn(withPresenting: presentingVC)

        guard let idToken = result.user.idToken?.tokenString else {
            throw AuthError.invalidGoogleCredential
        }

        return GoogleAuthProvider.credential(
            withIDToken: idToken,
            accessToken: result.user.accessToken.tokenString
        )
    }

    // MARK: - Private

    /// Finds the currently presented controller because Google Sign-In needs a
    /// UIKit presentation anchor even though the app shell is SwiftUI.
    private func topViewController() -> UIViewController? {
        guard let scene = UIApplication.shared.connectedScenes
            .compactMap({ $0 as? UIWindowScene })
            .first(where: { $0.activationState == .foregroundActive }),
              let root = scene.windows.first(where: { $0.isKeyWindow })?.rootViewController
        else { return nil }

        return topmostViewController(from: root)
    }

    private func topmostViewController(from vc: UIViewController) -> UIViewController {
        if let presented = vc.presentedViewController {
            return topmostViewController(from: presented)
        }
        if let nav = vc as? UINavigationController, let top = nav.topViewController {
            return topmostViewController(from: top)
        }
        if let tab = vc as? UITabBarController, let selected = tab.selectedViewController {
            return topmostViewController(from: selected)
        }
        return vc
    }
}

// MARK: - AuthError Extension

extension AuthError {
    static var invalidGoogleCredential: AuthError { .invalidAppleCredential }
}
