import Foundation
import Testing
@testable import caocap

struct AnalysisTests {

    @Test func analyzerIdentifiesEmptyHTML() throws {
        let nodes = [
            SpatialNode(position: .zero, title: "HTML", textContent: "")
        ]
        let analyzer = ProjectAnalyzer()
        let suggestions = analyzer.analyze(nodes: nodes)
        
        #expect(suggestions.contains { $0.title == "HTML is empty" })
    }

    @Test func analyzerIdentifiesNoStylesWhenHTMLHasContent() throws {
        let nodes = [
            SpatialNode(position: .zero, title: "HTML", textContent: "<h1>Hello</h1>"),
            SpatialNode(position: .zero, title: "CSS", textContent: "")
        ]
        let analyzer = ProjectAnalyzer()
        let suggestions = analyzer.analyze(nodes: nodes)
        
        #expect(suggestions.contains { $0.title == "No styles added" })
    }

    @Test func analyzerIdentifiesMissingPreview() throws {
        let nodes: [SpatialNode] = []
        let analyzer = ProjectAnalyzer()
        let suggestions = analyzer.analyze(nodes: nodes)
        
        #expect(suggestions.contains { $0.title == "Missing Preview" })
    }

    @MainActor
    @Test func viewModelUpdatesSuggestionsOnStoreChange() throws {
        let viewModel = CoCaptainViewModel()
        let store = ProjectStore(fileName: "test_project.json", projectName: "Test")
        
        // Initial state
        #expect(viewModel.analysisItems.isEmpty)
        
        // Set store (triggering analysis)
        viewModel.store = store
        
        // Should have at least the "Missing Preview" or "SRS is blank" suggestions for a new store
        #expect(!viewModel.analysisItems.isEmpty)
    }
}
