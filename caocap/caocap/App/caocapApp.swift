//
//  caocapApp.swift
//  caocap
//
//  Created by الشيخ عزام on 20/04/2026.
//

import SwiftUI

// MARK: - App Delegate

/// Thin delegate whose only responsibility is forwarding launch
/// events to `AppConfiguration`. Never add SDK calls directly here.
final class AppDelegate: NSObject, UIApplicationDelegate {

    /// Owned here so it is guaranteed to exist before `didFinishLaunchingWithOptions` fires.
    let authManager = AuthenticationManager()

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        AppConfiguration.shared.configure(authManager: authManager)
        return true
    }
}

// MARK: - App Entry Point

@main
struct caocapApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var delegate
    @AppStorage("app_theme") private var selectedTheme = "System"
    @AppStorage("app_language") private var selectedLanguage = "English"

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(delegate.authManager)
                .preferredColorScheme(colorScheme)
                .environment(\.locale, Locale(identifier: "en"))
        }
    }
    
    private var colorScheme: ColorScheme? {
        switch selectedTheme {
        case "Light": return .light
        case "Dark": return .dark
        default: return nil
        }
    }
}


