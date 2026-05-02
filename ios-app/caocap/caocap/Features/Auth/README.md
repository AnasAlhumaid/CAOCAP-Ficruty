# Auth Feature

The Auth feature lets users start immediately with an anonymous Firebase session, then upgrade that session to Apple, Google, or GitHub sign-in without losing work.

## Ownership

- `AuthenticationManager` owns Firebase session state, anonymous bootstrap, provider linking, sign-out, and account deletion.
- `SignInView` renders the upgrade/sign-in sheet and handles loading/error presentation.
- `AppleSignInCoordinator` runs Apple's native authorization flow and returns a Firebase `OAuthCredential`.
- `GoogleSignInCoordinator` runs Google Sign-In and returns a Firebase `AuthCredential`.
- GitHub sign-in is started directly through Firebase's `OAuthProvider` inside `AuthenticationManager`.

The feature's core contract is identity upgrade without data loss. Preserve anonymous UID continuity whenever a provider can be linked.

## Auth Flow

1. App startup calls `AuthenticationManager.start()`.
2. Firebase emits the current user through its auth state listener.
3. If no user exists, the manager starts anonymous sign-in.
4. If the user is anonymous, app state becomes `.anonymous(uid:)`.
5. If the user has a linked provider, app state becomes `.authenticated(uid:)`.
6. When the user chooses a provider, the provider coordinator returns a Firebase credential.
7. `AuthenticationManager` links the credential to the current anonymous account when possible.
8. If Firebase reports that the credential belongs to an existing account, the manager signs into that account instead.

## Account Linking

`linkOrSignIn(with:provider:)` is the most important method in this feature. It decides whether to:

- link a provider credential to the current anonymous Firebase user;
- switch into an existing account when the credential is already in use;
- sign in fresh when there is no anonymous session.

When changing this flow, verify that local project data remains associated with the expected UID and that the UI updates after linking.

## Provider Notes

- Apple Sign-In uses a raw nonce and SHA-256 hashed request nonce to prevent replay attacks.
- Google Sign-In needs a UIKit presenting view controller even though the app shell is SwiftUI.
- GitHub uses Firebase's OAuth provider flow and requests `user:email`.
- Provider coordinators should only produce credentials. `AuthenticationManager` owns linking/sign-in policy.

## Compliance Notes

- Keep Terms of Service and Privacy Policy links visible from sign-in surfaces.
- Account deletion can fail if Firebase requires recent authentication. Surface that error to the user instead of swallowing it.
- Avoid logging tokens, emails, provider credentials, or other sensitive identity data.

## Editing Guidance

- Keep Firebase-specific session policy in `AuthenticationManager`.
- Keep provider presentation code in provider coordinators.
- Do not duplicate account-linking decisions in views.
- Use `Logger` for diagnostics and avoid new `print(...)` calls.
- If adding a provider, add a coordinator or contained provider flow, then route it through `linkOrSignIn`.
- If auth state changes affect navigation, update `ContentView`, `AppRouter`, and related docs together.

## Verification Checklist

- Fresh install starts anonymous sign-in and reaches a usable state.
- Apple sign-in links the anonymous account and preserves work.
- Google sign-in links the anonymous account and preserves work.
- GitHub sign-in links or signs into the expected account.
- Credential conflicts switch to the existing account without crashing.
- Sign-out returns to a valid session path.
- Account deletion surfaces re-authentication or deletion failures clearly.
- Terms and Privacy links open correctly.

## Test Targets

Useful test coverage for this feature:

- `AuthState` transitions from nil user, anonymous user, and provider-linked user.
- anonymous bootstrap only runs when no session exists.
- provider conflict handling in `linkOrSignIn`.
- sign-in view loading/error state boundaries.
- account deletion error propagation.
