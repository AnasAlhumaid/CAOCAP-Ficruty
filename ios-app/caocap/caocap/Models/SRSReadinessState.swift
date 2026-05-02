import Foundation

/// Represents the formal readiness state of an SRS node, as defined in
/// FR-SRS-013 through FR-SRS-023. Derivation logic lives in SRSReadinessEvaluator.
public enum SRSReadinessState: String, CaseIterable, Codable, Hashable {
    /// No meaningful user-authored content.
    case empty
    /// Intent present but one or more required structural sections are missing.
    case draft
    /// All required sections exist but unresolved placeholders or missing
    /// acceptance checks remain.
    case structured
    /// Sections present but required fields contain unresolved placeholders
    /// (empty prompts, "TBD" markers).
    case needsClarification
    /// All sections filled, acceptance checks present with no placeholders —
    /// ready for CoCaptain to propose targeted code changes.
    case implementationReady
    /// Externally signalled: code has changed significantly since this SRS
    /// was last updated. Requires user acknowledgment to clear.
    case stale

    public var displayTitle: String {
        switch self {
        case .empty:              return "Ready for intent"
        case .draft:              return "Intent sketch"
        case .structured:         return "SRS taking shape"
        case .needsClarification: return "Needs clarification"
        case .implementationReady: return "Implementation-ready"
        case .stale:              return "Stale — review needed"
        }
    }

    public var icon: String {
        switch self {
        case .empty:              return "sparkles"
        case .draft:              return "pencil.circle"
        case .structured:         return "slider.horizontal.3"
        case .needsClarification: return "questionmark.circle"
        case .implementationReady: return "checkmark.seal.fill"
        case .stale:              return "exclamationmark.triangle.fill"
        }
    }

    /// Short nudge toward the next useful action.
    public var nextAction: String {
        switch self {
        case .empty:              return "Name the intent, users, and goal."
        case .draft:              return "Add missing sections to structure requirements."
        case .structured:         return "Replace placeholder lines with real decisions."
        case .needsClarification: return "Resolve open questions before generating code."
        case .implementationReady: return "Ready for CoCaptain."
        case .stale:              return "Review requirements against recent code changes."
        }
    }

    /// Prompt-friendly label included in CoCaptain context.
    public var contextLabel: String {
        switch self {
        case .empty:              return "Empty"
        case .draft:              return "Draft"
        case .structured:         return "Structured"
        case .needsClarification: return "Needs Clarification"
        case .implementationReady: return "Implementation-Ready"
        case .stale:              return "Stale"
        }
    }
}
