import Foundation
import Testing
import CoreGraphics
@testable import caocap

struct OnboardingTests {

    @MainActor
    @Test func coordinatorAdvancesStepsCorrectly() throws {
        let coordinator = OnboardingCoordinator()
        let steps = [
            OnboardingStep(label: "Step 1", gate: .pan),
            OnboardingStep(label: "Step 2", gate: .zoom)
        ]
        
        coordinator.load(steps: steps)
        
        #expect(coordinator.currentStepIndex == 0)
        #expect(coordinator.currentStep?.label == "Step 1")
        #expect(!coordinator.isComplete)
        
        coordinator.advance()
        #expect(coordinator.currentStepIndex == 1)
        #expect(coordinator.currentStep?.label == "Step 2")
        #expect(!coordinator.isComplete)
        
        coordinator.advance()
        #expect(coordinator.currentStepIndex == 2)
        #expect(coordinator.isComplete)
        #expect(coordinator.currentStep == nil)
        
        // Should not advance past end
        coordinator.advance()
        #expect(coordinator.currentStepIndex == 2)
    }

    @Test func providerDecodesManifestWithSteps() throws {
        let json = """
        {
          "version": 1,
          "projectName": "Test Onboarding",
          "initialViewportScale": 0.5,
          "nodes": [],
          "steps": [
            {
              "id": "00000000-0000-0000-0000-000000000001",
              "label": "Test Step",
              "gate": "pan",
              "spotlightRadius": 50
            }
          ]
        }
        """.data(using: .utf8)!
        
        let manifest = try OnboardingProvider.decodeManifest(from: json)
        
        #expect(manifest.projectName == "Test Onboarding")
        #expect(manifest.steps?.count == 1)
        #expect(manifest.steps?.first?.label == "Test Step")
        #expect(manifest.steps?.first?.gate == .pan)
    }

    @Test func providerFallsBackWhenStepsMissing() throws {
        let json = """
        {
          "version": 1,
          "projectName": "Legacy Onboarding",
          "initialViewportScale": 1.0,
          "nodes": []
        }
        """.data(using: .utf8)!
        
        let manifest = try OnboardingProvider.decodeManifest(from: json)
        #expect(manifest.steps == nil)
    }
}
