import SwiftUI

public enum NodeTheme: String, Codable, CaseIterable {
    case purple, blue, pink, orange, green, indigo, cyan, secondary
    
    public var color: Color {
        switch self {
        case .purple: return .purple
        case .blue: return .blue
        case .pink: return .pink
        case .orange: return .orange
        case .green: return .green
        case .indigo: return .indigo
        case .cyan: return .cyan
        case .secondary: return .secondary
        }
    }
    
    public var glowOpacity: Double {
        switch self {
        case .secondary: return 0.05
        default: return 0.15
        }
    }

    public var localizedDisplayName: String {
        LocalizationManager.shared.localizedString(rawValue.capitalized)
    }
}
