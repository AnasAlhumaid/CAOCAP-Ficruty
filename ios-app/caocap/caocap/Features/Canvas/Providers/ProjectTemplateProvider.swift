import Foundation

public struct ProjectTemplateProvider {

    /// Returns a new set of interconnected nodes for the default project template.
    public static var defaultNodes: [SpatialNode] {
        let webViewId = UUID()
        let srsId = UUID()
        let htmlId = UUID()
        let cssId = UUID()
        let jsId = UUID()

        return [
            SpatialNode(
                id: webViewId,
                type: .webView,
                position: CGPoint(x: 375, y: 0),
                title: "Live Preview",
                subtitle: "Your current build renders here.",
                icon: "play.circle.fill",
                theme: .blue
            ),
            SpatialNode(
                id: srsId,
                type: .srs,
                position: CGPoint(x: -275, y: -200),
                title: "Software Requirements (SRS)",
                subtitle: "Define intent, people, flow, and success.",
                icon: "doc.text.fill",
                theme: .purple,
                textContent: SRSScaffold.defaultText
            ),
            SpatialNode(
                id: htmlId,
                type: .code,
                position: CGPoint(x: -275, y: 0),
                title: "HTML",
                subtitle: "Document structure.",
                icon: "chevron.left.slash.chevron.right",
                theme: .orange,
                connectedNodeIds: [srsId],
                textContent: "<!DOCTYPE html>\n<html>\n<head>\n    <title>My App</title>\n</head>\n<body>\n    <h1>Hello World!</h1>\n</body>\n</html>"
            ),
            SpatialNode(
                id: cssId,
                type: .code,
                position: CGPoint(x: -475, y: 200),
                title: "CSS",
                subtitle: "Styling and layout.",
                icon: "paintpalette.fill",
                theme: .blue,
                connectedNodeIds: [htmlId],
                textContent: "body {\n    background-color: #0d0d0d;\n    color: #ffffff;\n    display: flex;\n    justify-content: center;\n    align-items: center;\n    height: 100vh;\n    margin: 0;\n    font-family: -apple-system, BlinkMacSystemFont, sans-serif;\n    overflow: hidden;\n}\n\nh1 {\n    font-size: 3rem;\n    background: linear-gradient(90deg, #00C9FF 0%, #92FE9D 100%);\n    -webkit-background-clip: text;\n    -webkit-text-fill-color: transparent;\n    cursor: pointer;\n    transition: transform 0.1s ease-out, filter 0.3s ease;\n}\n\nh1:hover {\n    filter: drop-shadow(0 0 10px rgba(0, 201, 255, 0.5));\n}"
            ),
            SpatialNode(
                id: jsId,
                type: .code,
                position: CGPoint(x: -75, y: 200),
                title: "JavaScript",
                subtitle: "Interactivity and logic.",
                icon: "script",
                theme: .green,
                connectedNodeIds: [htmlId],
                textContent: "document.addEventListener('DOMContentLoaded', () => {\n    const text = document.querySelector('h1');\n    \n    // Parallax mouse effect\n    document.addEventListener('mousemove', (e) => {\n        const x = (window.innerWidth / 2 - e.pageX) / 25;\n        const y = (window.innerHeight / 2 - e.pageY) / 25;\n        text.style.transform = `translate(${x}px, ${y}px)`;\n    });\n    \n    // Click interaction\n    text.addEventListener('click', () => {\n        text.style.transform = 'scale(1.2)';\n        setTimeout(() => {\n            text.style.transform = 'scale(1)';\n        }, 150);\n    });\n});"
            )
        ]
    }
}

enum SRSScaffoldSection: String, CaseIterable, Hashable {
    case intent
    case whyItMatters
    case people
    case coreFlow
    case requirements
    case acceptanceChecks
    case constraints

    var title: String {
        switch self {
        case .intent:
            return "Intent"
        case .whyItMatters:
            return "Why"
        case .people:
            return "People"
        case .coreFlow:
            return "Flow"
        case .requirements:
            return "Requirements"
        case .acceptanceChecks:
            return "Acceptance"
        case .constraints:
            return "Constraints"
        }
    }

    var icon: String {
        switch self {
        case .intent:
            return "scope"
        case .whyItMatters:
            return "sparkles"
        case .people:
            return "person.2.fill"
        case .coreFlow:
            return "arrow.triangle.branch"
        case .requirements:
            return "list.bullet.rectangle"
        case .acceptanceChecks:
            return "checklist"
        case .constraints:
            return "lock.fill"
        }
    }

    var headingMarkers: [String] {
        switch self {
        case .intent:
            return ["# intent", "## intent"]
        case .whyItMatters:
            return ["## why it matters", "## why", "## problem"]
        case .people:
            return ["## people", "## users", "## audience"]
        case .coreFlow:
            return ["## core flow", "## flow", "## user flow"]
        case .requirements:
            return ["## requirements", "## functional requirements"]
        case .acceptanceChecks:
            return ["## acceptance checks", "## acceptance criteria", "## done when"]
        case .constraints:
            return ["## constraints", "## guardrails"]
        }
    }

    var templateBlock: String {
        switch self {
        case .intent:
            return """
            # Intent
            Build a focused web app that turns one clear idea into a working preview.
            """
        case .whyItMatters:
            return """
            ## Why It Matters
            - Developer pain or user need:
            - Future this points toward:
            """
        case .people:
            return """
            ## People
            - Primary user:
            - Moment of use:
            """
        case .coreFlow:
            return """
            ## Core Flow
            1. The user lands on:
            2. The user can:
            3. The experience responds by:
            """
        case .requirements:
            return """
            ## Requirements
            - The interface must make the main action obvious.
            - The live preview should communicate the idea without extra explanation.
            - The app should work in a single HTML/CSS/JS bundle.
            """
        case .acceptanceChecks:
            return """
            ## Acceptance Checks
            - [ ] A first-time user understands the purpose in under 5 seconds.
            - [ ] The main action works without setup.
            - [ ] Visual feedback confirms every important interaction.
            - [ ] CoCaptain has enough context to make safe, specific edits.
            """
        case .constraints:
            return """
            ## Constraints
            - Keep the first version small enough to ship today.
            - Avoid external dependencies unless the idea requires them.
            """
        }
    }

    func isPresent(in normalizedText: String) -> Bool {
        headingMarkers.contains { normalizedText.contains($0) }
    }
}

enum SRSScaffold {
    static let defaultText: String = SRSScaffoldSection.allCases
        .map(\.templateBlock)
        .joined(separator: "\n\n") + "\n"

    static func missingSections(in text: String) -> [SRSScaffoldSection] {
        let normalizedText = text.lowercased()
        return SRSScaffoldSection.allCases.filter { !$0.isPresent(in: normalizedText) }
    }

    static func structuredText(from text: String) -> String {
        let trimmedText = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmedText.isEmpty else {
            return defaultText
        }

        let missingBlocks = missingSections(in: trimmedText).map(\.templateBlock)
        guard !missingBlocks.isEmpty else {
            return trimmedText + "\n"
        }

        return ([trimmedText] + missingBlocks).joined(separator: "\n\n") + "\n"
    }
}
