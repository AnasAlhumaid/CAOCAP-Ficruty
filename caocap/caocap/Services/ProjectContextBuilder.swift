import Foundation

public struct ProjectContextBuilder {
    public init() {}

    @MainActor
    public func buildPromptContext(from store: ProjectStore) -> String {
        let inventory = store.nodes.map { node in
            let linkCount = (node.connectedNodeIds?.count ?? 0) + (node.nextNodeId == nil ? 0 : 1)
            return "- \(node.title) [\(node.type.rawValue)] links: \(linkCount)"
        }.joined(separator: "\n")

        let sections = NodeRole.allCases.compactMap { role -> String? in
            guard let node = node(for: role, in: store.nodes) else { return nil }
            // Keep context compact; large prompts can cause Firebase AI Logic calls
            // to fail with opaque errors (e.g. GenerateContentError error 0).
            let content = trimmed(node.textContent ?? "", limit: 1000)
            guard !content.isEmpty else { return nil }
            return "\(role.displayName):\n\(content)"
        }

        return [
            "Project Name: \(store.projectName)",
            "Workspace ID: \(store.fileName)",
            "Node Count: \(store.nodes.count)",
            "Node Graph:",
            inventory,
            sections.isEmpty ? nil : "Canonical Nodes:\n" + sections.joined(separator: "\n\n")
        ]
        .compactMap { $0 }
        .joined(separator: "\n\n")
    }

    private func node(for role: NodeRole, in nodes: [SpatialNode]) -> SpatialNode? {
        nodes.first(where: { role.matches(node: $0) })
    }

    private func trimmed(_ text: String, limit: Int) -> String {
        guard text.count > limit else { return text }
        return String(text.prefix(limit)) + "\n[TRUNCATED]"
    }
}
