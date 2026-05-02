import Foundation
import CoreGraphics

/// Represents a node found during a spatial search.
public struct NodeSearchResult: Identifiable, Equatable {
    public let id: UUID
    public let title: String
    public let snippet: String
    public let role: NodeRole
    public let position: CGPoint
    public let relevanceScore: Int
}

/// A pure service for indexing and searching project nodes.
public struct NodeSearchIndex {
    public init() {}

    /// Searches the provided nodes for the given query and returns ranked results.
    public func search(query: String, in nodes: [SpatialNode]) -> [NodeSearchResult] {
        let trimmed = query.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        guard !trimmed.isEmpty else { return [] }

        return nodes
            .filter { $0.role != .livePreview } // Usually best to fly to source code, not the output itself
            .compactMap { node -> NodeSearchResult? in
                var score = 0
                let titleLower = node.title.lowercased()
                let contentLower = (node.textContent ?? "").lowercased()

                if titleLower == trimmed {
                    score += 100
                } else if titleLower.hasPrefix(trimmed) {
                    score += 60
                } else if titleLower.contains(trimmed) {
                    score += 30
                }

                if contentLower.contains(trimmed) {
                    score += 10
                }

                guard score > 0 else { return nil }

                let snippet = node.textContent.flatMap {
                    $0.isEmpty ? nil : String($0.prefix(60).replacingOccurrences(of: "\n", with: " "))
                } ?? node.subtitle ?? ""

                return NodeSearchResult(
                    id: node.id,
                    title: node.title,
                    snippet: snippet,
                    role: node.role,
                    position: node.position,
                    relevanceScore: score
                )
            }
            .sorted { $0.relevanceScore > $1.relevanceScore }
    }
}
