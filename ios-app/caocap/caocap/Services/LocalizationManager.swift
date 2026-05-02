import Foundation
import SwiftUI

public class LocalizationManager {
    public static let shared = LocalizationManager()
    public static let supportedLanguages = ["English", "Arabic"]
    public static let languageStorageKey = "app_language"
    private let newProjectPrefix = "New Project "
    private let appOwnedNodeTitles: Set<String> = [
        "Welcome to CAOCAP",
        "The Infinite Canvas",
        "Nodes of Intent",
        "Agentic Design",
        "Live Preview",
        "The Command Palette",
        "Your Journey Begins",
        "Profile",
        "Projects",
        "Settings",
        "New Project",
        "Retry Onboarding",
        "Software Requirements (SRS)",
        "HTML",
        "CSS",
        "JavaScript",
        "New Logic"
    ]
    private let appOwnedNodeSubtitles: Set<String> = [
        "The spatial landscape for agentic software design.",
        "Break free from the file tree. Pan and zoom to navigate your architecture.",
        "Each node represents a component, a logic block, or a vision. Drag them to organize your mind.",
        "You define the 'What'. Your Co-Captain handles the 'How'. Spatial programming starts here.",
        "See your creations come to life in real-time. This node is a live browser engine.",
        "Press the Floating Action Button to summon tools, create nodes, or talk to the AI.",
        "Enter your Home workspace to start building your first spatial project.",
        "Manage your account and preferences.",
        "View and organize your work.",
        "App configuration and tools.",
        "Start a fresh spatial journey.",
        "Revisit the guided tour and app manifesto.",
        "Your mini-game will render here.",
        "Define the core logic and rules here.",
        "Document structure.",
        "Styling and layout.",
        "Interactivity and logic.",
        "Write your intent here."
    ]
    
    private init() {}

    public var currentLanguage: String {
        let storedLanguage = UserDefaults.standard.string(forKey: Self.languageStorageKey) ?? "English"
        return Self.supportedLanguages.contains(storedLanguage) ? storedLanguage : "English"
    }

    private func localeIdentifier(for language: String) -> String {
        switch language {
        case "Arabic":
            return "ar"
        case "French":
            return "fr"
        case "German":
            return "de"
        case "Spanish":
            return "es"
        default:
            return "en"
        }
    }
    
    public func locale(for language: String) -> Locale {
        Locale(identifier: localeIdentifier(for: language))
    }

    public func layoutDirection(for language: String) -> LayoutDirection {
        switch language {
        case "Arabic":
            return .rightToLeft
        default:
            return .leftToRight
        }
    }

    public func bundle(for language: String? = nil) -> Bundle {
        let resolvedLanguage = language ?? currentLanguage
        let identifier = localeIdentifier(for: resolvedLanguage)
        guard let path = Bundle.main.path(forResource: identifier, ofType: "lproj"),
              let bundle = Bundle(path: path) else {
            return .main
        }
        return bundle
    }

    public func localizedString(_ key: String, language: String? = nil) -> String {
        let localized = bundle(for: language).localizedString(forKey: key, value: key, table: nil)
        if localized != key {
            return localized
        }
        return Bundle.main.localizedString(forKey: key, value: key, table: nil)
    }

    public func localizedString(_ key: String, arguments: [CVarArg], language: String? = nil) -> String {
        let resolvedLanguage = language ?? currentLanguage
        let format = localizedString(key, language: resolvedLanguage)
        return String(format: format, locale: locale(for: resolvedLanguage), arguments: arguments)
    }

    public func localizedProjectName(_ name: String, fileName: String? = nil, language: String? = nil) -> String {
        if fileName == "home_v2.json" || name == "Home" {
            return localizedString("Home", language: language)
        }

        if (fileName?.contains("onboarding") == true) || name == "Onboarding" {
            return localizedString("Onboarding", language: language)
        }

        if name == "Untitled Project" {
            return localizedString("Untitled Project", language: language)
        }

        if name.hasPrefix(newProjectPrefix) {
            let suffix = String(name.dropFirst(newProjectPrefix.count))
            if suffix.range(of: #"^[A-Fa-f0-9]{8}$"#, options: .regularExpression) != nil {
                return localizedString("project.generatedName", arguments: [suffix], language: language)
            }
        }

        return name
    }

    public func localizedNodeTitle(_ title: String, language: String? = nil) -> String {
        guard appOwnedNodeTitles.contains(title) else {
            return title
        }

        return localizedString(title, language: language)
    }

    public func localizedNodeSubtitle(_ subtitle: String, language: String? = nil) -> String {
        guard appOwnedNodeSubtitles.contains(subtitle) else {
            return subtitle
        }

        return localizedString(subtitle, language: language)
    }

    public func relativeDateString(for date: Date, relativeTo referenceDate: Date = Date(), language: String? = nil) -> String {
        let resolvedLanguage = language ?? currentLanguage
        let formatter = RelativeDateTimeFormatter()
        formatter.locale = locale(for: resolvedLanguage)
        formatter.unitsStyle = .full
        return formatter.localizedString(for: date, relativeTo: referenceDate)
    }
}
