import Foundation
import FirebaseAuth
import OSLog
import Observation

// MARK: - Auth State

/// Represents the current authentication state of the user.
public enum AuthState: Equatable {
    /// No auth session exists yet. The app is checking for an existing user.
    case loading
    /// The user is signed in anonymously.
    case anonymous(uid: String)
    /// The user has a verified, linked identity (e.g. Sign in with Apple).
    case authenticated(uid: String)
    /// Sign-in failed and there is no valid session.
    case failed(reason: String)
}

// MARK: - AuthenticationManager

/// Manages the full Firebase authentication lifecycle.
///
/// Responsibilities:
///  - Silently signs in new users anonymously on first launch.
///  - Restores existing sessions on subsequent launches.
///  - Provides Sign in with Apple, Google, and GitHub as upgrade paths.
///  - Links provider credentials to the existing anonymous account so
///    all user data (projects, etc.) is preserved on upgrade.
///
/// Inject via SwiftUI Environment:
/// ```swift
/// @Environment(AuthenticationManager.self) private var authManager
/// ```
@Observable
@MainActor
final class AuthenticationManager {

    // MARK: - Public State

    private(set) var authState: AuthState = .loading

    /// True if the user has any valid session (anonymous or real).
    var isSignedIn: Bool {
        switch authState {
        case .anonymous, .authenticated: return true
        case .loading, .failed: return false
        }
    }

    var isAnonymous: Bool {
        if case .anonymous = authState { return true }
        return false
    }

    var isAuthenticated: Bool {
        if case .authenticated = authState { return true }
        return false
    }

    var currentUID: String? {
        switch authState {
        case .anonymous(let uid), .authenticated(let uid): return uid
        case .loading, .failed: return nil
        }
    }

    // MARK: - Private

    private let logger = Logger(subsystem: "com.ficruty.caocap", category: "Auth")

    /// Wraps the Firebase listener handle so it can be safely cancelled
    /// from `deinit`, which is always nonisolated in Swift 6.
    private final class ListenerCanceller {
        var handle: AuthStateDidChangeListenerHandle?
        deinit {
            if let handle {
                Auth.auth().removeStateDidChangeListener(handle)
            }
        }
    }
    private let listenerCanceller = ListenerCanceller()

    // MARK: - Lifecycle

    init() {}

    // MARK: - Bootstrap

    /// Attaches the Firebase session listener once at app startup and normalizes
    /// every callback into the app's small `AuthState` surface.
    func start() {
        listenerCanceller.handle = Auth.auth().addStateDidChangeListener { [weak self] _, user in
            guard let self else { return }
            Task { @MainActor in
                self.handle(user: user)
            }
        }
    }

    // MARK: - Anonymous Sign-In

    /// Silently signs in anonymously. No-ops if a session already exists.
    func signInAnonymously() async {
        guard !isSignedIn else {
            logger.debug("Already signed in — skipping anonymous sign-in.")
            return
        }
        do {
            let result = try await Auth.auth().signInAnonymously()
            logger.info("Anonymous sign-in succeeded. UID: \(result.user.uid)")
        } catch {
            logger.error("Anonymous sign-in failed: \(error.localizedDescription)")
            authState = .failed(reason: error.localizedDescription)
        }
    }

    // MARK: - Sign In with Apple

    /// Links or signs in with an Apple credential.
    /// Call this with the credential produced by `AppleSignInCoordinator`.
    func signInWithApple(credential: OAuthCredential) async throws {
        try await linkOrSignIn(with: credential, provider: "Apple")
    }

    // MARK: - Sign In with Google

    /// Links or signs in with a Google credential.
    /// Call this with the credential produced by `GoogleSignInCoordinator`.
    func signInWithGoogle(credential: AuthCredential) async throws {
        try await linkOrSignIn(with: credential, provider: "Google")
    }

    // MARK: - Sign In with GitHub

    /// Starts the GitHub web OAuth flow and links or signs in.
    /// Firebase handles the web presentation internally via ASWebAuthenticationSession.
    func signInWithGitHub() async throws {
        let provider = OAuthProvider(providerID: "github.com")
        provider.scopes = ["user:email"]

        let credential = try await provider.credential(with: nil)
        try await linkOrSignIn(with: credential, provider: "GitHub")
    }

    // MARK: - Sign Out

    func signOut() {
        do {
            try Auth.auth().signOut()
            logger.info("User signed out.")
        } catch {
            logger.error("Sign out failed: \(error.localizedDescription)")
        }
    }

    /// Deletes the Firebase user. Callers should be prepared to surface
    /// re-authentication errors when Firebase considers the session stale.
    func deleteAccount() async throws {
        guard let user = Auth.auth().currentUser else { return }
        
        do {
            try await user.delete()
            logger.info("User account deleted successfully.")
        } catch {
            logger.error("Failed to delete account: \(error.localizedDescription)")
            throw error
        }
    }

    // MARK: - Private Helpers

    /// Links `credential` to the current anonymous account if the user is anonymous,
    /// otherwise signs in fresh. This preserves all anonymous data on upgrade.
    private func linkOrSignIn(with credential: AuthCredential, provider: String) async throws {
        if let currentUser = Auth.auth().currentUser, currentUser.isAnonymous {
            // Upgrade path: link the provider to the existing anonymous account.
            // The UID stays the same — all data is preserved.
            do {
                let result = try await currentUser.link(with: credential)
                logger.info("Successfully linked \(provider) to anonymous account. UID: \(result.user.uid)")
                
                // FORCE UI UPDATE: Firebase listener might not fire immediately on link
                handle(user: result.user)
            } catch let error as NSError {
                guard let errorCode = AuthErrorCode(rawValue: error.code) else {
                    throw error
                }
                
                let conflictCodes: [AuthErrorCode] = [
                    .credentialAlreadyInUse,
                    .emailAlreadyInUse,
                    .accountExistsWithDifferentCredential
                ]
                
                if conflictCodes.contains(errorCode) {
                    // The credential belongs to a different account — sign in to that account instead.
                    logger.warning("\(provider) credential conflict (code: \(error.code)). Switching to existing account.")
                    let result = try await Auth.auth().signIn(with: credential)
                    logger.info("Signed into existing \(provider) account. UID: \(result.user.uid)")
                    handle(user: result.user)
                } else {
                    // It's a genuine error (network, cancel, etc) — rethrow
                    throw error
                }
            }
        } else {
            // Fresh sign-in (no anonymous session).
            let result = try await Auth.auth().signIn(with: credential)
            logger.info("\(provider) sign-in succeeded. UID: \(result.user.uid)")
            handle(user: result.user)
        }
    }

    /// Converts Firebase's optional user into app state. A missing user starts
    /// anonymous sign-in so the local-first app always has a UID to attach data to.
    private func handle(user: User?) {
        guard let user else {
            logger.info("No active session found. Will attempt anonymous sign-in.")
            authState = .loading
            Task { await signInAnonymously() }
            return
        }

        if user.isAnonymous {
            authState = .anonymous(uid: user.uid)
            logger.info("Session restored: anonymous user \(user.uid)")
        } else {
            authState = .authenticated(uid: user.uid)
            logger.info("Session restored: authenticated user \(user.uid)")
        }
    }
}
