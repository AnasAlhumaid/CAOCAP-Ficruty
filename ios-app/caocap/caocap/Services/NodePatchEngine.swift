import Foundation

public enum NodeRole: String, CaseIterable, Codable, Hashable {
    case srs
    case html
    case css
    case javascript

    public var displayName: String {
        switch self {
        case .srs: return "SRS"
        case .html: return "HTML"
        case .css: return "CSS"
        case .javascript: return "JavaScript"
        }
    }

    public var localizedDisplayName: String {
        LocalizationManager.shared.localizedString(displayName)
    }

    public func matches(node: SpatialNode) -> Bool {
        switch self {
        case .srs:
            return node.type == .srs || node.title.localizedCaseInsensitiveContains("software requirements")
        case .html:
            return node.title.caseInsensitiveCompare("HTML") == .orderedSame
        case .css:
            return node.title.caseInsensitiveCompare("CSS") == .orderedSame
        case .javascript:
            return node.title.caseInsensitiveCompare("JavaScript") == .orderedSame
        }
    }
}

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

public struct NodePatchEngine {
    public init() {}

    @MainActor
    public func resolveNode(for role: NodeRole, in store: ProjectStore) -> SpatialNode? {
        store.nodes.first(where: { role.matches(node: $0) })
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
