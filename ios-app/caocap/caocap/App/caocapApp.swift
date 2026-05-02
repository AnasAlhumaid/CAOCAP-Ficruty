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
                .environment(\.locale, appLocale)
                .environment(\.layoutDirection, appLayoutDirection)
        }
        .commands {
            CommandGroup(replacing: .undoRedo) {
                Button("Undo") {
                    NotificationCenter.default.post(name: .performUndo, object: nil)
                }
                .keyboardShortcut("z", modifiers: .command)

                Button("Redo") {
                    NotificationCenter.default.post(name: .performRedo, object: nil)
                }
                .keyboardShortcut("z", modifiers: [.command, .shift])
            }

            CommandMenu("Commands") {
                Button("Command Palette") {
                    NotificationCenter.default.post(name: .openCommandPalette, object: nil)
                }
                .keyboardShortcut("k", modifiers: .command)

                Button("Summon CoCaptain") {
                    NotificationCenter.default.post(name: .summonCoCaptain, object: nil)
                }
                .keyboardShortcut("j", modifiers: .command)
            }
        }
    }
    
    private var colorScheme: ColorScheme? {
        switch selectedTheme {
        case "Light": return .light
        case "Dark": return .dark
        default: return nil
        }
    }

    private var appLocale: Locale {
        switch selectedLanguage {
        case "Arabic":
            return Locale(identifier: "ar")
        default:
            return Locale(identifier: "en")
        }
    }

    private var appLayoutDirection: LayoutDirection {
        switch selectedLanguage {
        case "Arabic":
            return .rightToLeft
        default:
            return .leftToRight
        }
    }
}
