import Foundation
import CoreGraphics
import OSLog

public struct OnboardingManifest: Codable, Equatable {
    public let version: Int
    public let projectName: String
    public let initialViewportScale: CGFloat
    public let nodes: [SpatialNode]
    public let steps: [OnboardingStep]?
}

public struct OnboardingProvider {
    private static let logger = Logger(subsystem: "com.ficruty.caocap", category: "Onboarding")
    private static let resourceName = "tutorial"

    public static var projectName: String {
        manifest.projectName
    }

    public static var initialViewportScale: CGFloat {
        manifest.initialViewportScale
    }

    public static var manifestoNodes: [SpatialNode] {
        manifest.nodes
    }

    public static var steps: [OnboardingStep] {
        manifest.steps ?? []
    }

    public static func decodeManifest(from data: Data) throws -> OnboardingManifest {
        try JSONDecoder().decode(OnboardingManifest.self, from: data)
    }

    private static let manifest: OnboardingManifest = loadManifest() ?? fallbackManifest

    private static func loadManifest(bundle: Bundle = .main) -> OnboardingManifest? {
        let candidates = [
            bundle.url(forResource: resourceName, withExtension: "json"),
            bundle.url(forResource: resourceName, withExtension: "json", subdirectory: "Resources")
        ].compactMap { $0 }

        for url in candidates {
            do {
                let data = try Data(contentsOf: url)
                return try decodeManifest(from: data)
            } catch {
                logger.error("Failed to decode onboarding manifest at \(url.path): \(error.localizedDescription)")
            }
        }

        logger.warning("Using fallback onboarding manifest because tutorial.json was not found in the app bundle.")
        return nil
    }

    private static var fallbackManifest: OnboardingManifest {
        OnboardingManifest(
            version: 0,
            projectName: "Onboarding",
            initialViewportScale: 1.0,
            nodes: fallbackNodes,
            steps: fallbackSteps
        )
    }

    private static var fallbackSteps: [OnboardingStep] {
        return [
            OnboardingStep(
                label: "Welcome! Try panning the canvas to look around.",
                gate: .pan
            ),
            OnboardingStep(
                label: "Great. Now pinch to zoom in or out.",
                gate: .zoom
            ),
            OnboardingStep(
                label: "Tap a node to see its details, or use the green arrow to enter Home.",
                gate: .none
            )
        ]
    }

    private static var fallbackNodes: [SpatialNode] {
        let node1Id = UUID()
        let node2Id = UUID()
        let node3Id = UUID()
        
        return [
            SpatialNode(
                id: node1Id,
                position: CGPoint(x: 0, y: -180),
                title: "Welcome to CAOCAP",
                subtitle: "This is a canvas, not a file tree. Move slowly, look around, and start from intent.",
                icon: "sparkles",
                theme: .purple
            ),
            SpatialNode(
                id: node2Id,
                type: .srs,
                position: CGPoint(x: 0, y: 40),
                title: "Start With Intent",
                subtitle: "Your first project will open with requirements, code, and preview nodes already arranged for you.",
                icon: "doc.text.fill",
                theme: .blue,
                textContent: """
                A CAOCAP project starts with intent.

                Write what the software should do, then let the workspace keep requirements, code, preview, and CoCaptain close together.
                """
            ),
            SpatialNode(
                id: node3Id,
                position: CGPoint(x: 0, y: 280),
                title: "Enter Home",
                subtitle: "Create your first spatial project from the Home workspace.",
                icon: "arrow.right.circle.fill",
                theme: .green,
                action: .navigateHome
            )
        ]
    }
}
