import Foundation

public struct CoCaptainAgentValidationResult: Hashable {
    public let issues: [String]

    public var isValid: Bool {
        issues.isEmpty
    }
}

/// Validates model-produced agent payloads before any app action can execute.
/// The dispatcher remains the final execution boundary; this layer gives the
/// model deterministic feedback when it emits an unsafe or unusable contract.
public struct CoCaptainAgentValidator {
    public init() {}

    @MainActor
    public func validate(
        payload: CoCaptainAgentPayload,
        dispatcher: (any AppActionPerforming)?,
        requiresAgenticWork: Bool
    ) -> CoCaptainAgentValidationResult {
        var issues: [String] = []

        for action in payload.safeActions {
            guard let id = AppActionID(rawValue: action.actionID) else {
                issues.append("Unknown safe action id `\(action.actionID)`.")
                continue
            }

            guard let definition = dispatcher?.definition(for: id) else {
                issues.append("Safe action `\(id.rawValue)` is not currently available.")
                continue
            }

            if definition.isMutating {
                issues.append("Safe action `\(id.rawValue)` is mutating; move it to `pendingActions`.")
            }

            if !definition.allowsAutonomousExecution {
                issues.append("Safe action `\(id.rawValue)` is not autonomous; move it to `pendingActions`.")
            }
        }

        for action in payload.pendingActions {
            guard let id = AppActionID(rawValue: action.actionID) else {
                issues.append("Unknown pending action id `\(action.actionID)`.")
                continue
            }

            if dispatcher?.definition(for: id) == nil {
                issues.append("Pending action `\(id.rawValue)` is not currently available.")
            }
        }

        for edit in payload.nodeEdits {
            if edit.summary.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                issues.append("Node edit for `\(edit.role.rawValue)` needs a non-empty summary.")
            }

            if edit.operations.isEmpty {
                issues.append("Node edit for `\(edit.role.rawValue)` must include at least one operation.")
            }

            for operation in edit.operations {
                validate(operation: operation, role: edit.role, issues: &issues)
            }
        }

        if requiresAgenticWork,
           payload.safeActions.isEmpty,
           payload.pendingActions.isEmpty,
           payload.nodeEdits.isEmpty {
            issues.append("Build/edit requests must include at least one safe action, pending action, or node edit.")
        }

        return CoCaptainAgentValidationResult(issues: issues)
    }

    private func validate(
        operation: NodePatchOperation,
        role: NodeRole,
        issues: inout [String]
    ) {
        if operation.content.isEmpty {
            issues.append("Node edit for `\(role.rawValue)` has an operation with empty content.")
        }

        switch operation.type {
        case .replaceExact, .insertBeforeExact, .insertAfterExact:
            if operation.target?.isEmpty != false {
                issues.append("Operation `\(operation.type.rawValue)` for `\(role.rawValue)` requires a non-empty target.")
            }
        case .replaceAll, .append, .prepend:
            break
        }
    }
}
