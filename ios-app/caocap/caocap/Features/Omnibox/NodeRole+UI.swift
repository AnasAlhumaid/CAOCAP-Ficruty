import SwiftUI

extension NodeRole {
    var icon: String {
        switch self {
        case .srs: return "doc.text.fill"
        case .html: return "chevron.left.slash.chevron.right"
        case .css: return "number"
        case .javascript: return "curlybraces"
        case .livePreview: return "play.display"
        case .custom: return "square.grid.2x2.fill"
        }
    }
    
    var themeColor: Color {
        switch self {
        case .srs: return .blue
        case .html: return .orange
        case .css: return .pink
        case .javascript: return .yellow
        case .livePreview: return .green
        case .custom: return .purple
        }
    }
}
