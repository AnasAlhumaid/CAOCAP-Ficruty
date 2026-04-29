import Foundation
import CoreGraphics

public enum NodeAction: String, Codable, Equatable {
    case navigateHome
    case retryOnboarding
    case createNewProject
    case openSettings
    case openProfile
    case openProjectExplorer
    case resumeLastProject
}

public enum NodeType: String, Codable, Equatable {
    case standard
    case webView
    case srs
    case code
}

public struct SpatialNode: Identifiable, Codable, Equatable {
    public let id: UUID
    public var type: NodeType
    public var position: CGPoint
    public var title: String
    public var subtitle: String?
    public var icon: String?
    public var theme: NodeTheme
    public var nextNodeId: UUID?
    public var connectedNodeIds: [UUID]?
    public var action: NodeAction?
    public var htmlContent: String?
    public var textContent: String?
    
    public init(id: UUID = UUID(), type: NodeType = .standard, position: CGPoint, title: String, subtitle: String? = nil, icon: String? = nil, theme: NodeTheme = .blue, nextNodeId: UUID? = nil, connectedNodeIds: [UUID]? = nil, action: NodeAction? = nil, htmlContent: String? = nil, textContent: String? = nil) {
        self.id = id
        self.type = type
        self.position = position
        self.title = title
        self.subtitle = subtitle
        self.icon = icon
        self.theme = theme
        self.nextNodeId = nextNodeId
        self.connectedNodeIds = connectedNodeIds
        self.action = action
        self.htmlContent = htmlContent
        self.textContent = textContent
    }

    public var displayTitle: String {
        LocalizationManager.shared.localizedNodeTitle(title)
    }

    public var displaySubtitle: String? {
        subtitle.map { LocalizationManager.shared.localizedNodeSubtitle($0) }
    }
}
