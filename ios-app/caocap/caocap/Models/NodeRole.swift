import Foundation

public enum NodeRole: String, CaseIterable, Codable, Hashable {
    case srs
    case html
    case css
    case javascript
    case livePreview
    case custom

    public static let editableCanonicalRoles: [NodeRole] = [
        .srs,
        .html,
        .css,
        .javascript
    ]

    public var displayName: String {
        switch self {
        case .srs: return "SRS"
        case .html: return "HTML"
        case .css: return "CSS"
        case .javascript: return "JavaScript"
        case .livePreview: return "Live Preview"
        case .custom: return "Custom"
        }
    }

    public var localizedDisplayName: String {
        LocalizationManager.shared.localizedString(displayName)
    }

    public var isEditableCanonicalRole: Bool {
        Self.editableCanonicalRoles.contains(self)
    }

    public func matches(node: SpatialNode) -> Bool {
        node.role == self
    }
}

public extension SpatialNode {
    var role: NodeRole {
        if type == .webView {
            return .livePreview
        }

        if type == .srs || title.localizedCaseInsensitiveContains("software requirements") {
            return .srs
        }

        switch title.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() {
        case "html":
            return .html
        case "css":
            return .css
        case "javascript":
            return .javascript
        case "live preview":
            return .livePreview
        default:
            return .custom
        }
    }

    var isProtected: Bool {
        action != nil
    }
}
