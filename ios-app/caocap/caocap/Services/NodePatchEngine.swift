import Foundation

public enum NodePatchOperationType: String, Codable, Hashable {
    case replaceAll = "replace_all"
    case replaceExact = "replace_exact"
    case insertBeforeExact = "insert_before_exact"
    case insertAfterExact = "insert_after_exact"
    case append
    case prepend
}

public struct NodePatchOperation: Codable, Hashable {
    public let type: NodePatchOperationType
    public let target: String?
    public let content: String

    public init(type: NodePatchOperationType, target: String? = nil, content: String) {
        self.type = type
        self.target = target
        self.content = content
    }
}

public enum NodePatchError: LocalizedError, Hashable {
    case missingNode(NodeRole)
    case conflict(String)

    public var errorDescription: String? {
        switch self {
        case .missingNode(let role):
            return LocalizationManager.shared.localizedString("Missing %@ node.", arguments: [role.localizedDisplayName])
        case .conflict(let description):
            return LocalizationManager.shared.localizedString(description)
        }
    }
}

public struct NodePatchPreview: Hashable {
    public let role: NodeRole
    public let originalText: String
    public let resultText: String
}

/// Applies deterministic text operations proposed by CoCaptain to canonical
/// project nodes. It previews changes first so the UI can keep edits
/// human-approved and conflict-aware.
public struct NodePatchEngine {
    public init() {}

    @MainActor
    public func resolveNode(for role: NodeRole, in store: ProjectStore) -> SpatialNode? {
        guard role.isEditableCanonicalRole else { return nil }
        return store.nodes.first(where: { role.matches(node: $0) })
    }

    @MainActor
    public func preview(
        role: NodeRole,
        operations: [NodePatchOperation],
        in store: ProjectStore
    ) throws -> NodePatchPreview {
        guard let node = resolveNode(for: role, in: store) else {
            throw NodePatchError.missingNode(role)
        }

        let originalText = node.textContent ?? ""
        let resultText = try apply(operations: operations, to: originalText)
        return NodePatchPreview(role: role, originalText: originalText, resultText: resultText)
    }

    /// Applies operations in order. Exact operations fail fast when their target
    /// text is missing, preventing model output from silently editing the wrong area.
    public func apply(operations: [NodePatchOperation], to text: String) throws -> String {
        var updatedText = text

        for operation in operations {
            switch operation.type {
            case .replaceAll:
                updatedText = operation.content
            case .replaceExact:
                guard let target = operation.target, let range = updatedText.range(of: target) else {
                    throw NodePatchError.conflict("Could not find exact text to replace.")
                }
                updatedText.replaceSubrange(range, with: operation.content)
            case .insertBeforeExact:
                guard let target = operation.target, let range = updatedText.range(of: target) else {
                    throw NodePatchError.conflict("Could not find exact text to insert before.")
                }
                updatedText.insert(contentsOf: operation.content, at: range.lowerBound)
            case .insertAfterExact:
                guard let target = operation.target, let range = updatedText.range(of: target) else {
                    throw NodePatchError.conflict("Could not find exact text to insert after.")
                }
                updatedText.insert(contentsOf: operation.content, at: range.upperBound)
            case .append:
                updatedText += operation.content
            case .prepend:
                updatedText = operation.content + updatedText
            }
        }

        return updatedText
    }
}
