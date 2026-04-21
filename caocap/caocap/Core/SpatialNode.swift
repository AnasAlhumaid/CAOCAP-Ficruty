import Foundation
import CoreGraphics

public struct SpatialNode: Identifiable, Codable, Equatable {
    public let id: UUID
    public var position: CGPoint
    public var title: String
    public var subtitle: String?
    public var icon: String?
    public var color: String? // Store as hex or name
    
    public init(id: UUID = UUID(), position: CGPoint, title: String, subtitle: String? = nil, icon: String? = nil, color: String? = nil) {
        self.id = id
        self.position = position
        self.title = title
        self.subtitle = subtitle
        self.icon = icon
        self.color = color
    }
}
