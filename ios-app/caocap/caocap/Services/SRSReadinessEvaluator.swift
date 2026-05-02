import Foundation

/// Derives the formal SRSReadinessState from raw SRS text content.
/// Pure input → output: no store access, no async, no side effects.
/// This makes it unit-testable without any mocks.
public struct SRSReadinessEvaluator {
    public init() {}

    /// Evaluates the readiness state from text.
    /// - Parameters:
    ///   - text: The current raw text content of the SRS node.
    ///   - currentState: The previously persisted state. If `.stale`, that
    ///     state is preserved until the user explicitly clears it.
    /// - Returns: The derived `SRSReadinessState`.
    public func evaluate(text: String, currentState: SRSReadinessState?) -> SRSReadinessState {
        // Stale is externally signalled and requires explicit user clearance — never auto-clear.
        if currentState == .stale { return .stale }

        let wordCount = text.split(whereSeparator: \.isWhitespace).count
        guard wordCount > 0 else { return .empty }

        let missingSections = SRSScaffold.missingSections(in: text)

        // All 7 sections must be present to progress beyond draft.
        if !missingSections.isEmpty { return .draft }

        // Check for unresolved placeholders in the acceptance checks section.
        if hasUnresolvedPlaceholders(in: text) { return .needsClarification }

        // All sections present and acceptance checks filled with real content.
        if hasFilledAcceptanceChecks(in: text) { return .implementationReady }

        // All sections present but acceptance checks are still template-level.
        return .structured
    }

    // MARK: - Private helpers

    /// Returns true when any line looks like an unfilled template prompt:
    /// ends with a bare colon, or contains "TBD" / "..." / placeholder brackets.
    private func hasUnresolvedPlaceholders(in text: String) -> Bool {
        let lines = text.components(separatedBy: .newlines)
        return lines.contains { line in
            let trimmed = line.trimmingCharacters(in: .whitespaces)
            guard !trimmed.isEmpty, !trimmed.hasPrefix("#") else { return false }
            return trimmed.hasSuffix(":")
                || trimmed.uppercased().contains("TBD")
                || trimmed.contains("...")
                || (trimmed.hasPrefix("[") && trimmed.hasSuffix("]"))
        }
    }

    /// Returns true when the Acceptance Checks section has at least one
    /// non-placeholder, non-empty checklist item.
    private func hasFilledAcceptanceChecks(in text: String) -> Bool {
        let normalizedText = text.lowercased()

        // Find the acceptance section heading.
        let acceptanceMarkers = SRSScaffoldSection.acceptanceChecks.headingMarkers
        guard let markerRange = acceptanceMarkers.compactMap({ normalizedText.range(of: $0) }).first else {
            return false
        }

        // Grab text after the acceptance heading.
        let afterAcceptance = String(text[markerRange.upperBound...])
        let lines = afterAcceptance.components(separatedBy: .newlines)

        // Look for a checklist item (- [ ] or - [x]) with real content.
        return lines.contains { line in
            let trimmed = line.trimmingCharacters(in: .whitespaces)
            guard trimmed.hasPrefix("- [") else { return false }
            let content = trimmed.dropFirst(5) // drop "- [ ] " or "- [x] "
            return !content.trimmingCharacters(in: .whitespaces).isEmpty
                && !hasUnresolvedPlaceholders(in: String(content))
        }
    }
}
