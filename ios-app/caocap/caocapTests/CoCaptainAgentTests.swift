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

    @Test func nodeRoleInferenceRecognizesCanonicalTemplateNodes() {
        #expect(SpatialNode(type: .srs, position: .zero, title: "Software Requirements (SRS)").role == .srs)
        #expect(SpatialNode(type: .code, position: .zero, title: "HTML").role == .html)
        #expect(SpatialNode(type: .code, position: .zero, title: "CSS").role == .css)
        #expect(SpatialNode(type: .code, position: .zero, title: "JavaScript").role == .javascript)
        #expect(SpatialNode(type: .webView, position: .zero, title: "Live Preview").role == .livePreview)
        #expect(SpatialNode(type: .code, position: .zero, title: "New Logic").role == .custom)
    }

    @Test func livePreviewCompilerInjectsCSSAndJavaScriptIntoDocumentTags() throws {
        let nodes = makePreviewNodes(
            html: "<html><head></head><body><h1>Hello</h1></body></html>",
            css: "body { color: white; }",
            javascript: "console.log('hi');"
        )

        let compilation = try #require(LivePreviewCompiler().compile(nodes: nodes))

        #expect(compilation.html.contains("<style>\nbody { color: white; }\n</style>"))
        #expect(compilation.html.contains("<script>\nconsole.log('hi');\n</script>"))
        #expect(compilation.html.range(of: "<style>")!.lowerBound < compilation.html.range(of: "</head>")!.lowerBound)
        #expect(compilation.html.range(of: "<script>")!.lowerBound < compilation.html.range(of: "</body>")!.lowerBound)
    }

    @Test func livePreviewCompilerHandlesMissingHeadAndBodyTags() throws {
        let nodes = makePreviewNodes(
            html: "<main>Hello</main>",
            css: ".title { color: orange; }",
            javascript: "window.ready = true;"
        )

        let compilation = try #require(LivePreviewCompiler().compile(nodes: nodes))

        #expect(compilation.html.hasPrefix("\n<style>\n.title { color: orange; }\n</style>\n"))
        #expect(compilation.html.hasSuffix("\n<script>\nwindow.ready = true;\n</script>\n"))
    }

    @Test func livePreviewCompilerRequiresPreviewAndHTMLNodes() {
        let compiler = LivePreviewCompiler()
        let htmlOnly = [SpatialNode(type: .code, position: .zero, title: "HTML", textContent: "<h1>Hello</h1>")]
        let previewOnly = [SpatialNode(type: .webView, position: .zero, title: "Live Preview")]

        #expect(compiler.compile(nodes: htmlOnly) == nil)
        #expect(compiler.compile(nodes: previewOnly) == nil)
    }

    @Test func chatBubbleMarkdownPreservesVisibleContent() {
        let bubble = ChatBubbleItem(
            text: """
            **Next steps**

            - Tighten layout
            - Improve contrast
            """,
            isUser: false
        )

        let renderedText = String(bubble.markdownText.characters)

        #expect(renderedText.contains("Next steps"))
        #expect(renderedText.contains("Tighten layout"))
        #expect(renderedText.contains("Improve contrast"))
    }

    @MainActor
    @Test func commandIntentResolverMatchesEnglishProjectCommands() throws {
        let resolver = CommandIntentResolver()
        let actions = TestActionDispatcher().availableActions

        #expect(resolver.resolve("create a project", availableActions: actions) == .newProject)
        #expect(resolver.resolve("please create a project", availableActions: actions) == .newProject)
        #expect(resolver.resolve("new project", availableActions: actions) == .newProject)
        #expect(resolver.resolve("open settings", availableActions: actions) == .openSettings)
        #expect(resolver.resolve("make a home page", availableActions: actions) == nil)
        #expect(resolver.resolve("do not create a project", availableActions: actions) == nil)
    }

    @MainActor
    @Test func commandIntentResolverMatchesArabicProjectCommands() throws {
        let resolver = CommandIntentResolver()
        let actions = TestActionDispatcher().availableActions

        #expect(resolver.resolve("أنشئ مشروع جديد", availableActions: actions) == .newProject)
        #expect(resolver.resolve("لو سمحت أنشئ مشروع جديد", availableActions: actions) == .newProject)
        #expect(resolver.resolve("افتح الإعدادات", availableActions: actions) == .openSettings)
        #expect(resolver.resolve("اعرض المشاريع", availableActions: actions) == .openProjectExplorer)
        #expect(resolver.resolve("لا تنشئ مشروع جديد", availableActions: actions) == nil)
    }

    @MainActor
    @Test func commandPaletteSubmitsUnmatchedQueryAsPrompt() {
        let viewModel = CommandPaletteViewModel()
        viewModel.actions = TestActionDispatcher().availableActions
        viewModel.query = "  make a tiny platformer  "

        var submittedPrompt: String?
        var executedAction: AppActionID?
        viewModel.onSubmitPrompt = { submittedPrompt = $0 }
        viewModel.onExecute = { executedAction = $0 }

        viewModel.confirmSelection()

        #expect(submittedPrompt == "make a tiny platformer")
        #expect(executedAction == nil)
        #expect(viewModel.isPresented == false)
    }

    @MainActor
    @Test func commandPalettePrefersListedCommandOverPrompt() {
        let viewModel = CommandPaletteViewModel()
        viewModel.actions = TestActionDispatcher().availableActions
        viewModel.query = "settings"

        var submittedPrompt: String?
        var executedAction: AppActionID?
        viewModel.onSubmitPrompt = { submittedPrompt = $0 }
        viewModel.onExecute = { executedAction = $0 }

        viewModel.confirmSelection()

        #expect(executedAction == .openSettings)
        #expect(submittedPrompt == nil)
    }

    @Test func parserExtractsTrailingStructuredBlock() throws {
        let parser = CoCaptainAgentParser()
        let response =
            """
            I can make that update.

            <cocaptain_actions>
              <assistant_message>I can make that update.</assistant_message>
              <safe_actions>
                <action id="go_home" />
              </safe_actions>
              <pending_actions></pending_actions>
              <node_edits></node_edits>
            </cocaptain_actions>
            """

        let parsed = parser.parse(response)

        #expect(parsed.preamble == "I can make that update.")
        #expect(parsed.visibleText == "I can make that update.")
        #expect(parsed.payload?.safeActions.count == 1)
        #expect(parsed.payload?.safeActions.first?.actionID == "go_home")
    }

    @Test func parserDetectsLoosePayloadWithoutWhitespace() throws {
        let parser = CoCaptainAgentParser()
        let response = "aesthetic.<cocaptain_actions><assistant_message>Implementing...</assistant_message></cocaptain_actions>"

        let parsed = parser.parse(response)
        #expect(parsed.preamble == "aesthetic.")
        #expect(parsed.payload?.assistantMessage == "Implementing...")
    }

    @Test func parserHandlesCurlyQuotesInLoosePayload() throws {
        let parser = CoCaptainAgentParser()
        // Some models send smart quotes like “assistantMessage”
        let response = "OK. { “assistantMessage”: “Hello” }"

        let parsed = parser.parse(response)
        #expect(parsed.preamble == "OK.")
        #expect(parsed.payload?.assistantMessage == "Hello")
    }

    @Test func parserHidesLooseTrailingActionXML() throws {
        let parser = CoCaptainAgentParser()
        let response =
            """
            I can document that preference.

            <cocaptain_actions>
              <assistant_message>Documented the preference.</assistant_message>
              <node_edits>
                <node_edit role="srs" summary="Document color preference.">
                  <operation type="append">
                    <content><![CDATA[\nPrimary color: Slate Grey.]]></content>
                  </operation>
                </node_edit>
              </node_edits>
            </cocaptain_actions>
            """

        let parsed = parser.parse(response)

        #expect(parsed.preamble == "I can document that preference.")
        #expect(parsed.payload?.nodeEdits.count == 1)
    }

    @Test func parserHidesIncompleteLooseTrailingActionXML() throws {
        let parser = CoCaptainAgentParser()
        let response =
            """
            Working on it.

            <cocaptain_actions>
              <assistant_message>Still generating...
            """

        let parsed = parser.parse(response)

        // Should NOT show the XML even if it's not closed yet.
        #expect(parsed.preamble == "Working on it.")
        #expect(parsed.payload == nil)
    }

    @Test func chatBubbleMarkdownFallsBackToInlineSyntax() {
        let bubble = ChatBubbleItem(
            text: "Hello *world*",
            isUser: false
        )

        // This should always succeed and at least render the italics if possible.
        let renderedText = String(bubble.markdownText.characters)
        #expect(renderedText.contains("world"))
    }

    @Test func chatBubbleMarkdownStylesInlineCode() {
        let bubble = ChatBubbleItem(
            text: "Use `let x = 5` here",
            isUser: false
        )

        let attributed = bubble.markdownText
        // Check if the parser identifies inline code
        var foundInlineCode = false
        for run in attributed.runs {
            if let intent = run.inlinePresentationIntent, intent.contains(.code) {
                foundInlineCode = true
            }
        }
        #expect(foundInlineCode)
    }

    @Test func parserHandlesMultiLineXML() throws {
        let parser = CoCaptainAgentParser()
        let response = """
        Updating:
        <cocaptain_actions>
          <assistant_message>Multi-line</assistant_message>
        </cocaptain_actions>
        """

        let parsed = parser.parse(response)
        #expect(parsed.visibleText == "Updating:")
        #expect(parsed.payload?.assistantMessage == "Multi-line")
    }

    @Test func parserFallsBackOnMissingClosingTag() throws {
        let parser = CoCaptainAgentParser()
        let response =
            """
            I can help.

            <cocaptain_actions>
              <assistant_message>Incomplete
            """

        let parsed = parser.parse(response)

        #expect(parsed.payload == nil)
        #expect(parsed.preamble == "I can help.")
    }

    @Test func xmlAdapterProducesCoordinatorDirective() throws {
        let adapter = CoCaptainXMLAgentAdapter()
        let response =
            """
            Done.

            <cocaptain_actions>
              <assistant_message>Done.</assistant_message>
              <safe_actions><action id="go_home"/></safe_actions>
              <pending_actions></pending_actions>
              <node_edits></node_edits>
            </cocaptain_actions>
            """

        let directive = adapter.directive(from: response)

        #expect(directive.preamble == "Done.")
        #expect(directive.visibleText == "Done.")
        #expect(directive.payload?.safeActions.first?.actionID == "go_home")
        #expect(directive.diagnostics.isEmpty)
        #expect(directive.source == .xml)
    }

    @Test func functionCallAdapterMapsSafeAction() throws {
        let adapter = CoCaptainFunctionCallAgentAdapter()

        let directive = adapter.directive(from: [
            CoCaptainAgentFunctionCall(
                name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
                arguments: ["actionId": "go_home", "executionMode": "safe"]
            )
        ])

        #expect(directive.payload?.safeActions.first?.actionID == "go_home")
        #expect(directive.payload?.pendingActions.isEmpty == true)
        #expect(directive.diagnostics.isEmpty)
        #expect(directive.source == .functionCall)
    }

    @Test func functionCallAdapterMapsPendingAction() throws {
        let adapter = CoCaptainFunctionCallAgentAdapter()

        let directive = adapter.directive(from: [
            CoCaptainAgentFunctionCall(
                name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
                arguments: ["actionId": "create_node", "executionMode": "pending"]
            )
        ])

        #expect(directive.payload?.pendingActions.first?.actionID == "create_node")
        #expect(directive.payload?.safeActions.isEmpty == true)
        #expect(directive.diagnostics.isEmpty)
    }

    @Test func functionCallAdapterReportsMalformedCalls() throws {
        let adapter = CoCaptainFunctionCallAgentAdapter()

        let missingAction = adapter.directive(from: [
            CoCaptainAgentFunctionCall(
                name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
                arguments: ["executionMode": "safe"]
            )
        ])
        let unknownFunction = adapter.directive(from: [
            CoCaptainAgentFunctionCall(name: "unknown_function", arguments: ["actionId": "go_home"])
        ])

        #expect(missingAction.payload == nil)
        #expect(missingAction.diagnostics.first?.contains("missing `actionId`") == true)
        #expect(unknownFunction.payload == nil)
        #expect(unknownFunction.diagnostics.first?.contains("Unknown function call") == true)
    }

    @Test func compositeAdapterMergesFunctionActionsAndFencedNodeEdits() throws {
        let adapter = CoCaptainCompositeAgentAdapter()
        let response =
            """
            I updated the project.

            <cocaptain_actions>
              <assistant_message>I updated the project.</assistant_message>
              <node_edits>
                <node_edit role="html" summary="Update HTML.">
                  <operation type="replace_all">
                    <content><![CDATA[<h1>Fixed</h1>]]></content>
                  </operation>
                </node_edit>
              </node_edits>
            </cocaptain_actions>
            """

        let directive = adapter.directive(
            from: response,
            functionCalls: [
                CoCaptainAgentFunctionCall(
                    name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
                    arguments: ["actionId": "go_home", "executionMode": "safe"]
                )
            ]
        )

        #expect(directive.payload?.safeActions.first?.actionID == "go_home")
        #expect(directive.payload?.nodeEdits.first?.role == .html)
        #expect(directive.source == .combined)
    }

    @MainActor
    @Test func coordinatorRetriesMalformedStructuredPayload() async throws {
        let dispatcher = TestActionDispatcher()
        let llm = TestLLMClient(
            responses: [
                """
                I prepared an edit.

                <cocaptain_actions>
                  <assistant_message>Incomplete
                """,
                """
                I prepared a valid HTML edit.

                <cocaptain_actions>
                  <assistant_message>I prepared a valid HTML edit.</assistant_message>
                  <node_edits>
                    <node_edit role="html" summary="Update HTML.">
                      <operation type="replace_all">
                        <content><![CDATA[<h1>Fixed</h1>]]></content>
                      </operation>
                    </node_edit>
                  </node_edits>
                </cocaptain_actions>
                """
            ]
        )
        let coordinator = CoCaptainAgentCoordinator(llmClient: llm)

        let result = try await coordinator.run(
            userMessage: "update the HTML",
            store: makeStore(),
            dispatcher: dispatcher
        ) { _ in }

        #expect(llm.receivedMessages.count == 2)
        #expect(llm.receivedMessages.last?.contains("satisfied the machine-readable CoCaptain action contract") == true)
        #expect(result.reviewBundle?.items.first?.status == .pending)
    }

    @MainActor
    @Test func coordinatorExecutesSafeActionsAndStagesPendingReviews() async throws {
        let dispatcher = TestActionDispatcher()
        let llm = TestLLMClient(
            response:
                """
                I moved us home and prepared an HTML update.

                <cocaptain_actions>
                  <assistant_message>I moved us home and prepared an HTML update.</assistant_message>
                  <safe_actions><action id="go_home"/></safe_actions>
                  <pending_actions><action id="create_node"/></pending_actions>
                  <node_edits>
                    <node_edit role="html" summary="Update the headline.">
                      <operation type="replace_exact">
                        <target>Hello World!</target>
                        <content><![CDATA[Agentic Hello!]]></content>
                      </operation>
                    </node_edit>
                  </node_edits>
                </cocaptain_actions>
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
    @Test func coordinatorExecutesFunctionCalledSafeAction() async throws {
        let dispatcher = TestActionDispatcher()
        let llm = TestLLMClient(
            response: "Opening settings.",
            functionCalls: [[
                CoCaptainAgentFunctionCall(
                    name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
                    arguments: ["actionId": "open_settings", "executionMode": "safe"]
                )
            ]]
        )
        let coordinator = CoCaptainAgentCoordinator(llmClient: llm)

        let result = try await coordinator.run(
            userMessage: "open settings",
            store: makeStore(),
            dispatcher: dispatcher
        ) { _ in }

        #expect(dispatcher.executedActionIDs == [.openSettings])
        #expect(result.executionSummary?.summary.contains("Open Settings") == true)
    }

    @MainActor
    @Test func coordinatorStagesFunctionCalledPendingAction() async throws {
        let dispatcher = TestActionDispatcher()
        let llm = TestLLMClient(
            response: "I prepared the action for review.",
            functionCalls: [[
                CoCaptainAgentFunctionCall(
                    name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
                    arguments: ["actionId": "create_node", "executionMode": "pending"]
                )
            ]]
        )
        let coordinator = CoCaptainAgentCoordinator(llmClient: llm)

        let result = try await coordinator.run(
            userMessage: "create a node",
            store: makeStore(),
            dispatcher: dispatcher
        ) { _ in }

        #expect(dispatcher.executedActionIDs.isEmpty)
        #expect(result.reviewBundle?.items.first?.targetLabel == "Create New Node")
    }

    @MainActor
    @Test func coordinatorRetriesUnsafeFunctionCalledSafeAction() async throws {
        let dispatcher = TestActionDispatcher()
        let llm = TestLLMClient(
            responses: [
                "I will create a node.",
                "I prepared the action for review."
            ],
            functionCalls: [
                [
                    CoCaptainAgentFunctionCall(
                        name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
                        arguments: ["actionId": "create_node", "executionMode": "safe"]
                    )
                ],
                [
                    CoCaptainAgentFunctionCall(
                        name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
                        arguments: ["actionId": "create_node", "executionMode": "pending"]
                    )
                ]
            ]
        )
        let coordinator = CoCaptainAgentCoordinator(llmClient: llm)

        let result = try await coordinator.run(
            userMessage: "create a node",
            store: makeStore(),
            dispatcher: dispatcher
        ) { _ in }

        #expect(dispatcher.executedActionIDs.isEmpty)
        #expect(llm.receivedMessages.count == 2)
        #expect(llm.receivedMessages.last?.contains("move it to `pendingActions`") == true)
        #expect(result.reviewBundle?.items.first?.targetLabel == "Create New Node")
    }

    @MainActor
    @Test func coordinatorDoesNotPartiallyExecuteMalformedFunctionCall() async throws {
        let dispatcher = TestActionDispatcher()
        let llm = TestLLMClient(
            responses: [
                "Opening settings.",
                "Opening settings."
            ],
            functionCalls: [
                [
                    CoCaptainAgentFunctionCall(
                        name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
                        arguments: ["actionId": "open_settings", "executionMode": "safe"]
                    ),
                    CoCaptainAgentFunctionCall(
                        name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
                        arguments: ["executionMode": "safe"]
                    )
                ],
                [
                    CoCaptainAgentFunctionCall(
                        name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
                        arguments: ["actionId": "open_settings", "executionMode": "safe"]
                    )
                ]
            ]
        )
        let coordinator = CoCaptainAgentCoordinator(llmClient: llm)

        _ = try await coordinator.run(
            userMessage: "open settings",
            store: makeStore(),
            dispatcher: dispatcher
        ) { _ in }

        #expect(dispatcher.executedActionIDs == [.openSettings])
        #expect(llm.receivedMessages.count == 2)
        #expect(llm.receivedMessages.last?.contains("missing `actionId`") == true)
    }

    @MainActor
    @Test func coordinatorDoesNotExecuteInvalidSafeActionBeforeRetry() async throws {
        let dispatcher = TestActionDispatcher()
        let llm = TestLLMClient(
            responses: [
                """
                I will create a node.

                <cocaptain_actions>
                  <assistant_message>I will create a node.</assistant_message>
                  <safe_actions><action id="create_node"/></safe_actions>
                </cocaptain_actions>
                """,
                """
                I prepared the action for review.

                <cocaptain_actions>
                  <assistant_message>I prepared the action for review.</assistant_message>
                  <pending_actions><action id="create_node"/></pending_actions>
                </cocaptain_actions>
                """
            ]
        )
        let coordinator = CoCaptainAgentCoordinator(llmClient: llm)

        let result = try await coordinator.run(
            userMessage: "create a node",
            store: makeStore(),
            dispatcher: dispatcher
        ) { _ in }

        #expect(dispatcher.executedActionIDs.isEmpty)
        #expect(llm.receivedMessages.count == 2)
        #expect(llm.receivedMessages.last?.contains("move it to `pendingActions`") == true)
        #expect(result.reviewBundle?.items.count == 1)
    }

    @MainActor
    @Test func coordinatorReturnsValidationReviewWhenRetryPayloadIsStillInvalid() async throws {
        let dispatcher = TestActionDispatcher()
        let llm = TestLLMClient(
            response:
                """
                I will use an unknown action.

                <cocaptain_actions>
                  <assistant_message>I will use an unknown action.</assistant_message>
                  <safe_actions><action id="launch_rocket"/></safe_actions>
                </cocaptain_actions>
                """
        )
        let coordinator = CoCaptainAgentCoordinator(llmClient: llm)

        let result = try await coordinator.run(
            userMessage: "create something",
            store: makeStore(),
            dispatcher: dispatcher
        ) { _ in }

        #expect(dispatcher.executedActionIDs.isEmpty)
        #expect(result.executionSummary == nil)
        #expect(result.reviewBundle?.items.first?.status == .conflicted)
        #expect(result.reviewBundle?.items.first?.preview.contains("Unknown safe action id `launch_rocket`.") == true)
    }

    @MainActor
    @Test func coordinatorRetriesEmptyNodeEditOperations() async throws {
        let dispatcher = TestActionDispatcher()
        let llm = TestLLMClient(
            responses: [
                """
                I prepared an edit.

                <cocaptain_actions>
                  <assistant_message>I prepared an edit.</assistant_message>
                  <node_edits>
                    <node_edit role="html" summary="Update HTML.">
                    </node_edit>
                  </node_edits>
                </cocaptain_actions>
                """,
                """
                I prepared a valid HTML edit.

                <cocaptain_actions>
                  <assistant_message>I prepared a valid HTML edit.</assistant_message>
                  <node_edits>
                    <node_edit role="html" summary="Update HTML.">
                      <operation type="replace_all">
                        <content><![CDATA[<h1>Fixed</h1>]]></content>
                      </operation>
                    </node_edit>
                  </node_edits>
                </cocaptain_actions>
                """
            ]
        )
        let coordinator = CoCaptainAgentCoordinator(llmClient: llm)

        let result = try await coordinator.run(
            userMessage: "update the HTML",
            store: makeStore(),
            dispatcher: dispatcher
        ) { _ in }

        #expect(llm.receivedMessages.count == 2)
        #expect(llm.receivedMessages.last?.contains("must include at least one operation") == true)
        #expect(result.reviewBundle?.items.first?.status == .pending)
    }

    @MainActor
    @Test func applyReviewItemConflictsWhenNodeEditedAfterSuggestion() {
        let store = makeStore()
        let vm = CoCaptainViewModel()
        vm.store = store

        let htmlNode = store.nodes.first(where: { $0.title == "HTML" })!
        let baseText = htmlNode.textContent ?? ""
        let bundleID = UUID()
        let itemID = UUID()

        vm.items.append(CoCaptainTimelineItem(
            id: bundleID,
            content: .reviewBundle(ReviewBundleItem(
                id: bundleID,
                items: [PendingReviewItem(
                    id: itemID,
                    targetLabel: "HTML",
                    summary: "Update headline",
                    preview: "<h1>Agentic Hello!</h1>",
                    source: .nodeEdit(
                        role: .html,
                        operations: [NodePatchOperation(type: .replaceAll, content: "<h1>Agentic Hello!</h1>")],
                        baseText: baseText
                    )
                )]
            ))
        ))

        // User edits the HTML node before clicking Apply — stale scenario.
        store.updateNodeTextContent(id: htmlNode.id, text: "<h1>User wrote this instead</h1>", persist: false)
        vm.applyReviewItem(bundleID: bundleID, itemID: itemID)

        guard case .reviewBundle(let bundle) = vm.items.first(where: { $0.id == bundleID })?.content,
              let result = bundle.items.first(where: { $0.id == itemID }) else {
            Issue.record("Review bundle or item not found")
            return
        }

        #expect(result.status == .conflicted)
        #expect(result.conflictDescription?.contains("edited after") == true)
    }

    @MainActor
    @Test func applyReviewItemSucceedsWhenNodeUnchanged() {
        let store = makeStore()
        let vm = CoCaptainViewModel()
        vm.store = store

        let htmlNode = store.nodes.first(where: { $0.title == "HTML" })!
        let baseText = htmlNode.textContent ?? ""
        let bundleID = UUID()
        let itemID = UUID()

        vm.items.append(CoCaptainTimelineItem(
            id: bundleID,
            content: .reviewBundle(ReviewBundleItem(
                id: bundleID,
                items: [PendingReviewItem(
                    id: itemID,
                    targetLabel: "HTML",
                    summary: "Update headline",
                    preview: "<h1>Agentic Hello!</h1>",
                    source: .nodeEdit(
                        role: .html,
                        operations: [NodePatchOperation(type: .replaceAll, content: "<h1>Agentic Hello!</h1>")],
                        baseText: baseText
                    )
                )]
            ))
        ))

        // No user edits between suggestion and apply — should succeed.
        vm.applyReviewItem(bundleID: bundleID, itemID: itemID)

        guard case .reviewBundle(let bundle) = vm.items.first(where: { $0.id == bundleID })?.content,
              let result = bundle.items.first(where: { $0.id == itemID }) else {
            Issue.record("Review bundle or item not found")
            return
        }

        #expect(result.status == .applied)
        #expect(result.conflictDescription == nil)
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

    private func makePreviewNodes(
        html: String,
        css: String,
        javascript: String
    ) -> [SpatialNode] {
        [
            SpatialNode(type: .webView, position: .zero, title: "Live Preview"),
            SpatialNode(type: .code, position: .zero, title: "HTML", textContent: html),
            SpatialNode(type: .code, position: .zero, title: "CSS", textContent: css),
            SpatialNode(type: .code, position: .zero, title: "JavaScript", textContent: javascript)
        ]
    }
}

@MainActor
private final class TestLLMClient: CoCaptainLLMClient {
    private let responses: [String]
    private let functionCalls: [[CoCaptainAgentFunctionCall]]
    private var streamCount = 0
    var receivedMessages: [String] = []

    init(response: String) {
        self.responses = [response]
        self.functionCalls = []
    }

    init(response: String, functionCalls: [[CoCaptainAgentFunctionCall]]) {
        self.responses = [response]
        self.functionCalls = functionCalls
    }

    init(responses: [String]) {
        self.responses = responses
        self.functionCalls = []
    }

    init(responses: [String], functionCalls: [[CoCaptainAgentFunctionCall]]) {
        self.responses = responses
        self.functionCalls = functionCalls
    }

    func resetChat() {}

    func streamAgentEvents(
        for userMessage: String,
        context: String?,
        expectsStructuredResponse: Bool,
        availableActions: [AppActionDefinition]
    ) -> AsyncThrowingStream<CoCaptainLLMStreamEvent, Error> {
        receivedMessages.append(userMessage)
        let index = streamCount
        let response = responses[min(index, responses.count - 1)]
        let calls = functionCalls.indices.contains(index) ? functionCalls[index] : []
        streamCount += 1

        return AsyncThrowingStream { continuation in
            continuation.yield(.text(response))
            if !calls.isEmpty {
                continuation.yield(.functionCalls(calls))
            }
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
        ),
        AppActionDefinition(
            id: .openSettings,
            title: "Open Settings",
            icon: "gearshape.fill",
            category: .assistant,
            isMutating: false,
            allowsAutonomousExecution: true
        ),
        AppActionDefinition(
            id: .openProjectExplorer,
            title: "Project Explorer",
            icon: "folder.fill",
            category: .project,
            isMutating: false,
            allowsAutonomousExecution: true
        )
    ]

    var executedActionIDs: [AppActionID] = []

    func definition(for id: AppActionID) -> AppActionDefinition? {
        availableActions.first(where: { $0.id == id })
    }

    @discardableResult
    func perform(_ id: AppActionID, source: AppActionSource, arguments: [String: String]? = nil) -> AppActionResult {
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
