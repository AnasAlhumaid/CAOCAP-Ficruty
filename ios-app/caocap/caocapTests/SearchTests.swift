import Foundation
import Testing
import CoreGraphics
@testable import caocap

struct SearchTests {

    @Test func searchIndexScoresTitleMatchesHigherThanContent() throws {
        let nodes = [
            SpatialNode(id: UUID(), position: .zero, title: "Login Page", textContent: "Some details about login"),
            SpatialNode(id: UUID(), position: .zero, title: "Other", textContent: "This contains the word login")
        ]
        let index = NodeSearchIndex()
        let results = index.search(query: "login", in: nodes)
        
        #expect(results.count == 2)
        #expect(results[0].title == "Login Page")
        #expect(results[0].relevanceScore > results[1].relevanceScore)
    }

    @Test func searchIndexExcludesLivePreview() throws {
        // Wait, role is computed based on title/type. I should check how role is defined.
        // If I use SpatialNode extension to check role:
        let previewNode = SpatialNode(id: UUID(), type: .standard, position: .zero, title: "Live Preview")
        #expect(previewNode.role == .livePreview)
        
        let index = NodeSearchIndex()
        let results = index.search(query: "live", in: [previewNode])
        
        #expect(results.isEmpty)
    }

    @Test func viewportFlyToCalculatesCorrectOffset() throws {
        let viewport = ViewportState()
        let nodePosition = CGPoint(x: 100, y: 200)
        let containerSize = CGSize(width: 500, height: 800)
        
        // flyTo with targetScale 1.0
        viewport.flyTo(nodePosition: nodePosition, containerSize: containerSize, targetScale: 1.0)
        
        // Offset should be -nodePosition * scale
        #expect(viewport.offset.width == -100)
        #expect(viewport.offset.height == -200)
        #expect(viewport.scale == 1.0)
    }
}
