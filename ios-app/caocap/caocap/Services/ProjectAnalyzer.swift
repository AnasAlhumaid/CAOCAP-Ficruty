import Foundation

/// Represents a potential improvement or action identified by analyzing the project nodes.
public struct ProjectSuggestion: Identifiable, Equatable {
    public let id: UUID
    /// Short title for the suggestion (e.g., "HTML node is empty").
    public let title: String
    /// More detailed explanation for the user.
    public let detail: String
    /// The prompt that will be sent to CoCaptain if the user applies this suggestion.
    public let suggestedPrompt: String
    public let severity: Severity

    public enum Severity {
        case info
        case warning
    }

    public init(id: UUID = UUID(), title: String, detail: String, suggestedPrompt: String, severity: Severity = .info) {
        self.id = id
        self.title = title
        self.detail = detail
        self.suggestedPrompt = suggestedPrompt
        self.severity = severity
    }
}

/// A pure service that inspects the current node graph and surfaces structural recommendations.
public struct ProjectAnalyzer {
    public init() {}

    /// Analyzes the given nodes and returns a list of actionable suggestions.
    public func analyze(nodes: [SpatialNode]) -> [ProjectSuggestion] {
        var suggestions: [ProjectSuggestion] = []

        let html = nodes.first(where: { $0.role == .html })
        let css = nodes.first(where: { $0.role == .css })
        let js = nodes.first(where: { $0.role == .javascript })
        let srs = nodes.first(where: { $0.role == .srs })
        let preview = nodes.first(where: { $0.role == .livePreview })

        // Rule: SRS is empty or blank
        if let srs, srs.textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true {
            suggestions.append(ProjectSuggestion(
                title: "SRS is blank",
                detail: "Describe your app idea here so CoCaptain can help you build it.",
                suggestedPrompt: "I have a blank SRS. Can you help me brainstorm requirements for a simple web app?",
                severity: .info
            ))
        }

        // Rule: HTML exists but is empty
        if let html, html.textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true {
            let detail = srs != nil ? "CoCaptain can generate starter HTML from your SRS." : "Start by adding some HTML structure."
            let prompt = srs != nil ? "Can you generate a starter HTML structure based on my SRS requirements?" : "Generate a basic HTML boilerplate for me."
            
            suggestions.append(ProjectSuggestion(
                title: "HTML is empty",
                detail: detail,
                suggestedPrompt: prompt,
                severity: .warning
            ))
        }

        // Rule: HTML has content but CSS is empty
        if let html, !(html.textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true),
           let css, css.textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true {
            suggestions.append(ProjectSuggestion(
                title: "No styles added",
                detail: "Your HTML looks good. Want me to generate some CSS for it?",
                suggestedPrompt: "Based on my current HTML, can you generate some modern CSS styles?",
                severity: .info
            ))
        }
        
        // Rule: HTML and CSS exist but JS is empty
        if let html, !(html.textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true),
           let css, !(css.textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true),
           let js, js.textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true {
            suggestions.append(ProjectSuggestion(
                title: "No interactivity",
                detail: "Add some JavaScript to make your app interactive.",
                suggestedPrompt: "Can you suggest some simple JavaScript interactivity for my current HTML and CSS?",
                severity: .info
            ))
        }

        // Rule: No live preview node
        if preview == nil {
            suggestions.append(ProjectSuggestion(
                title: "Missing Preview",
                detail: "Add a Live Preview node to see your code rendered in real-time.",
                suggestedPrompt: "I'm missing a Live Preview node. Can you help me add one to the canvas?",
                severity: .warning
            ))
        }

        return suggestions
    }
}
