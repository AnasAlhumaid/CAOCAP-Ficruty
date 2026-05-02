import Foundation

/// Defines a single guided instruction in the onboarding flow.
public struct OnboardingStep: Codable, Identifiable, Equatable {
    public let id: UUID
    /// Optional node to highlight. If nil, the instruction is general to the canvas.
    public let spotlightNodeId: UUID?
    /// Instruction text shown in the focus ring.
    public let label: String
    /// The gesture required to unlock the next step.
    public let gate: GateGesture
    /// The radius of the focus ring.
    public let spotlightRadius: CGFloat

    public enum GateGesture: String, Codable {
        /// Automatically advance after a short delay (e.g. for purely informational steps).
        case none
        /// User must pan the canvas.
        case pan
        /// User must pinch-zoom the canvas.
        case zoom
        /// User must long-press the canvas background.
        case longPress
        /// User must tap the specific spotlit node.
        case tapNode
    }

    public init(
        id: UUID = UUID(),
        spotlightNodeId: UUID? = nil,
        label: String,
        gate: GateGesture = .none,
        spotlightRadius: CGFloat = 80
    ) {
        self.id = id
        self.spotlightNodeId = spotlightNodeId
        self.label = label
        self.gate = gate
        self.spotlightRadius = spotlightRadius
    }
}
