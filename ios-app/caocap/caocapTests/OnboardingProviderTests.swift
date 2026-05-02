import Foundation
import Testing
@testable import caocap

struct OnboardingProviderTests {
    @Test func tutorialManifestDecodesSpatialWorkflow() throws {
        let data = try Data(contentsOf: tutorialManifestURL())
        let manifest = try OnboardingProvider.decodeManifest(from: data)

        #expect(manifest.version == 2)
        #expect(manifest.projectName == "Onboarding")
        #expect(manifest.initialViewportScale == 1.0)
        #expect(manifest.nodes.count == 3)
        #expect(manifest.nodes.contains { $0.type == .srs })
        #expect(!manifest.nodes.contains { $0.type == .code })
        #expect(!manifest.nodes.contains { $0.type == .webView })
        #expect(!manifest.nodes.contains { $0.action == .summonCoCaptain })
        #expect(manifest.nodes.last?.action == .navigateHome)

        let nodeIDs = Set(manifest.nodes.map(\.id))
        for node in manifest.nodes {
            if let nextNodeId = node.nextNodeId {
                #expect(nodeIDs.contains(nextNodeId))
            }

            for connectedNodeId in node.connectedNodeIds ?? [] {
                #expect(nodeIDs.contains(connectedNodeId))
            }
        }
    }

    private func tutorialManifestURL() -> URL {
        URL(fileURLWithPath: #filePath)
            .deletingLastPathComponent()
            .deletingLastPathComponent()
            .appendingPathComponent("caocap/Resources/tutorial.json")
    }
}
