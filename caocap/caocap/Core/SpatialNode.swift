import Foundation
import CoreGraphics

public struct SpatialNode: Identifiable, Codable, Equatable {
    public let id: UUID
    public var position: CGPoint
    public var content: String
    
    public init(id: UUID = UUID(), position: CGPoint, content: String) {
        self.id = id
        self.position = position
        self.content = content
    }
}
