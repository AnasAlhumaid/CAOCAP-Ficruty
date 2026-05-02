import Foundation
import Observation

/// Manages the state and progression of the guided onboarding tutorial.
@Observable @MainActor
public final class OnboardingCoordinator {
    /// The current step being displayed to the user.
    public private(set) var currentStepIndex: Int = 0
    /// The full list of steps in the onboarding manifest.
    public private(set) var steps: [OnboardingStep] = []
    
    /// True if the user has reached the end of the tutorial.
    public var isComplete: Bool {
        currentStepIndex >= steps.count && !steps.isEmpty
    }

    public init() {}

    /// Loads steps from a manifest.
    public func load(steps: [OnboardingStep]) {
        self.steps = steps
        self.currentStepIndex = 0
    }

    /// Advances to the next instruction if available.
    public func advance() {
        guard !isComplete else { return }
        currentStepIndex += 1
    }

    /// Returns the active instruction step, if any.
    public var currentStep: OnboardingStep? {
        guard currentStepIndex < steps.count else { return nil }
        return steps[currentStepIndex]
    }
    
    /// Reset the coordinator to the start.
    public func reset() {
        currentStepIndex = 0
    }
}
