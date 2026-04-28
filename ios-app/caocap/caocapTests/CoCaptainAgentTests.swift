import CoreGraphics
import Foundation
import Testing
@testable import caocap

struct CoCaptainAgentTests {
    @MainActor
    @Test func projectContextIncludesCanonicalNodesAndExcludesCompiledPreview() throws {
        let store = makeStore()
        store.nodes.append(
            SpatialNode(
                type: .webView,
                position: .zero,
                title: "Live Preview",
                theme: .blue,
                htmlContent: "<html>compiled</html>"
            )
        )

        let context = ProjectContextBuilder().buildPromptContext(from: store)

        #expect(context.contains("Project Name: Test Project"))
        #expect(context.contains("SRS:"))
        #expect(context.contains("HTML:"))
        #expect(context.contains("CSS:"))
        #expect(context.contains("JavaScript:"))
        #expect(context.contains("Build a landing page"))
        #expect(!context.contains("compiled"))
    }

    @Test func nodePatchEngineAppliesOrderedOperations() throws {
        let engine = NodePatchEngine()
        let result = try engine.apply(
            operations: [
                NodePatchOperation(type: .replaceExact, target: "Hello", content: "Welcome"),
                NodePatchOperation(type: .append, content: "\n<footer>Done</footer>")
            ],
            to: "<h1>Hello</h1>"
        )

        #expect(result.contains("Welcome"))
        #expect(result.contains("<footer>Done</footer>"))
    }

    @Test func nodePatchEngineCanReplaceWholeNodeContent() throws {
        let engine = NodePatchEngine()
        let result = try engine.apply(
            operations: [
                NodePatchOperation(type: .replaceAll, content: "<main>New game shell</main>")
            ],
            to: "<h1>Old page</h1>"
        )

        #expect(result == "<main>New game shell</main>")
    }

    @Test func nodePatchEngineThrowsWhenAnchorMissing() throws {
        let engine = NodePatchEngine()

        #expect(throws: NodePatchError.self) {
            try engine.apply(
                operations: [NodePatchOperation(type: .insertAfterExact, target: "missing", content: "x")],
                to: "<h1>Hello</h1>"
            )
        }
    }

    @Test func parserExtractsTrailingStructuredBlock() throws {
        let parser = CoCaptainAgentParser()
        let response =
            """
            I can make that update.

            ```cocaptain-actions
            {
              "assistantMessage": "I can make that update.",
              "safeActions": [{"actionId":"go_home"}],
              "pendingActions": [],
              "nodeEdits": []
            }
            ```
            """

        let parsed = parser.parse(response)

        #expect(parsed.visibleText == "I can make that update.")
        #expect(parsed.payload?.safeActions.count == 1)
        #expect(parsed.payload?.safeActions.first?.actionID == "go_home")
    }

    @Test func parserFallsBackOnMalformedStructuredBlock() throws {
        let parser = CoCaptainAgentParser()
        let response =
            """
            I can help.

            ```cocaptain-actions
            {not-json}
            ```
            """

        let parsed = parser.parse(response)

        #expect(parsed.payload == nil)
        #expect(parsed.visibleText.contains("I can help."))
    }

    @MainActor
    @Test func coordinatorExecutesSafeActionsAndStagesPendingReviews() async throws {
        let dispatcher = TestActionDispatcher()
        let llm = TestLLMClient(
            response:
                """
                I moved us home and prepared an HTML update.

                ```cocaptain-actions
                {
                  "assistantMessage": "I moved us home and prepared an HTML update.",
                  "safeActions": [{"actionId":"go_home"}],
                  "pendingActions": [{"actionId":"create_node"}],
                  "nodeEdits": [{
                    "role": "html",
                    "summary": "Update the headline.",
                    "operations": [{
                      "type": "replace_exact",
                      "target": "Hello World!",
                      "content": "Agentic Hello!"
                    }]
                  }]
                }
                ```
                """
        )
        let coordinator = CoCaptainAgentCoordinator(llmClient: llm)
        let store = makeStore()

        let result = try await coordinator.run(
            userMessage: "Do it",
            store: store,
            dispatcher: dispatcher
        ) { _ in }

        #expect(dispatcher.executedActionIDs == [.goHome])
        #expect(result.executionSummary?.summary.contains("Go to Home") == true)
        #expect(result.reviewBundle?.items.count == 2)
    }

    @MainActor
    private func makeStore() -> ProjectStore {
        ProjectStore(
            fileName: "onboarding-test-\(UUID().uuidString).json",
            projectName: "Test Project",
            initialNodes: [
                SpatialNode(
                    type: .srs,
                    position: CGPoint(x: 0, y: 0),
                    title: "Software Requirements (SRS)",
                    theme: .purple,
                    textContent: "Build a landing page"
                ),
                SpatialNode(
                    type: .code,
                    position: CGPoint(x: 10, y: 0),
                    title: "HTML",
                    theme: .orange,
                    textContent: "<h1>Hello World!</h1>"
                ),
                SpatialNode(
                    type: .code,
                    position: CGPoint(x: 20, y: 0),
                    title: "CSS",
                    theme: .blue,
                    textContent: "body { color: white; }"
                ),
                SpatialNode(
                    type: .code,
                    position: CGPoint(x: 30, y: 0),
                    title: "JavaScript",
                    theme: .green,
                    textContent: "console.log('hi');"
                )
            ]
        )
    }
}

@MainActor
private final class TestLLMClient: CoCaptainLLMClient {
    let response: String

    init(response: String) {
        self.response = response
    }

    func resetChat() {}

    func streamResponse(
        for userMessage: String,
        context: String?,
        expectsStructuredResponse: Bool,
        availableActions: [AppActionDefinition]
    ) -> AsyncThrowingStream<String, Error> {
        AsyncThrowingStream { continuation in
            continuation.yield(response)
            continuation.finish()
        }
    }
}

@MainActor
private final class TestActionDispatcher: AppActionPerforming {
    let availableActions: [AppActionDefinition] = [
        AppActionDefinition(
            id: .goHome,
            title: "Go to Home",
            icon: "house.fill",
            category: .navigation,
            isMutating: false,
            allowsAutonomousExecution: true
        ),
        AppActionDefinition(
            id: .createNode,
            title: "Create New Node",
            icon: "plus.square",
            category: .project,
            isMutating: true,
            allowsAutonomousExecution: false
        )
    ]

    var executedActionIDs: [AppActionID] = []

    func definition(for id: AppActionID) -> AppActionDefinition? {
        availableActions.first(where: { $0.id == id })
    }

    @discardableResult
    func perform(_ id: AppActionID, source: AppActionSource) -> AppActionResult {
        guard let definition = definition(for: id) else {
            return AppActionResult(actionID: id, title: id.rawValue, executed: false, message: "Missing")
        }

        if source == .agentAutomatic && (definition.isMutating || !definition.allowsAutonomousExecution) {
            return AppActionResult(actionID: id, title: definition.title, executed: false, message: "Blocked")
        }

        executedActionIDs.append(id)
        return AppActionResult(actionID: id, title: definition.title, executed: true, message: "\(definition.title) executed.")
    }
}
