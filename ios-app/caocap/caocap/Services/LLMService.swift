import Foundation
import FirebaseAILogic
import OSLog

/// A singleton service that manages the interaction with the Gemini LLM via Firebase AI Logic.
///
/// Uses `FirebaseAI.firebaseAI(backend: .googleAI())` — the correct Firebase AI Logic
/// Swift API as of the `FirebaseAILogic` SDK.
///
/// Provides a streaming interface and maintains chat history for multi-turn conversations.
@MainActor
public final class LLMService {

    public static let shared = LLMService()

    private let logger = Logger(subsystem: "com.ficruty.caocap", category: "LLMService")

    // MARK: - Model & Session

    /// Lazily initialised so Firebase is guaranteed to be configured before first use.
    private lazy var model: GenerativeModel = makeModel(modelName: preferredModelName)

    /// Currently-selected model name (can be overridden via `UserDefaults`).
    ///
    /// Rationale: `FirebaseAILogic.GenerateContentError` can surface as a generic `error 0`
    /// for misconfigured/unsupported model names; using a stable default and allowing
    /// overrides helps unblock runtime debugging without code changes.
    private var preferredModelName: String {
        if let overridden = UserDefaults.standard.string(forKey: "cocaptain.modelName"),
           !overridden.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return overridden
        }
        // Prefer a stable, non-retired model name.
        // Firebase AI Logic retired all Gemini 1.5 models on 2025-09-24, and Gemini 2.x models on 2026-03-09.
        return "gemini-3-flash-preview"
    }

    /// The active chat session that maintains history.
    private var chat: Chat?

    private init() {}

    // MARK: - API

    /// Resets the current chat session, clearing all history.
    public func resetChat() {
        chat = nil
        logger.info("Chat session reset.")
    }

    /// Generates a streaming response for the given user prompt, maintaining conversation history.
    ///
    /// - Parameter prompt: The raw user message.
    /// - Returns: An `AsyncThrowingStream` of partial response strings.
    public func streamResponse(for prompt: String) -> AsyncThrowingStream<String, Error> {
        let events = streamAgentEvents(
            for: prompt,
            context: nil,
            expectsStructuredResponse: false,
            availableActions: []
        )

        return AsyncThrowingStream { continuation in
            let task = Task {
                do {
                    for try await event in events {
                        if case .text(let text) = event {
                            continuation.yield(text)
                        }
                    }
                    continuation.finish()
                } catch {
                    continuation.finish(throwing: error)
                }
            }
            continuation.onTermination = { _ in task.cancel() }
        }
    }

    public func streamAgentEvents(
        for userMessage: String,
        context: String?,
        expectsStructuredResponse: Bool,
        availableActions: [AppActionDefinition]
    ) -> AsyncThrowingStream<CoCaptainLLMStreamEvent, Error> {
        // Initialize chat session if it doesn't exist
        if chat == nil {
            // Ensure model is initialised with the latest preferred name at first use.
            model = makeModel(modelName: preferredModelName)
            chat = model.startChat()
        }

        let prompt = buildPrompt(
            userMessage: userMessage,
            context: context,
            expectsStructuredResponse: expectsStructuredResponse,
            availableActions: availableActions
        )

        return AsyncThrowingStream { continuation in
            let task = Task {
                do {
                    logger.debug("Starting LLM stream with history.")
                    logger.debug("Model: \(self.preferredModelName, privacy: .public) structured=\(expectsStructuredResponse, privacy: .public) contextChars=\((context ?? "").count, privacy: .public)")
                    
                    // Use sendMessageStream to participate in the multi-turn session
                    let stream = try chat!.sendMessageStream(prompt)
                    
                    for try await chunk in stream {
                        if let text = chunk.text {
                            continuation.yield(.text(text))
                        }

                        let functionCalls = chunk.functionCalls.map(CoCaptainAgentFunctionCall.init)
                        if !functionCalls.isEmpty {
                            continuation.yield(.functionCalls(functionCalls))
                        }
                    }
                    continuation.finish()
                    logger.info("LLM stream completed.")
                } catch {
                    let reflected = String(reflecting: error)
                    logger.error("LLM stream error: \(reflected, privacy: .public)")

                    // Attempt a one-time recovery by resetting the chat session.
                    // This helps when the underlying session is in a bad state.
                    self.chat = nil
                    continuation.finish(throwing: error)
                }
            }
            // Support cooperative cancellation from the caller side
            continuation.onTermination = { _ in task.cancel() }
        }
    }

    private func makeModel(modelName: String) -> GenerativeModel {
        FirebaseAI.firebaseAI(backend: .googleAI()).generativeModel(
            modelName: modelName,
            tools: [.functionDeclarations([Self.requestAppActionDeclaration])],
            toolConfig: ToolConfig(
                functionCallingConfig: .auto()
            ),
            systemInstruction: ModelContent(
                role: "system",
                parts: """
                You are Co-Captain, a spatial programming assistant for the Ficruty platform.
                You can request app actions with the `request_app_action` function and request node edits with a `cocaptain_actions` XML block. The app validates every requested action before execution.
                
                Personality:
                - You are a high-performance agentic engine. Be concise, authoritative, and proactive.
                - You can execute mutations on a spatial canvas when the user asks for canvas changes.
                - Use technical, precise language. Avoid conversational fluff like "I can help with that" or "Sure thing."
                - You think in architectures and spatial relationships.
                
                Core Rule:
                - Answer ordinary questions, opinions, and advice conversationally without app actions or node edits.
                - Use app actions or node edits only when the user explicitly asks to navigate, use a tool, create, edit, write, document, apply, implement, or otherwise change the current canvas.
                - Never provide full code in Markdown chat. Code belongs EXCLUSIVELY in `node_edits`. 
                - If the user asks you to apply a change, you MUST provide the XML to implement it.
                - Use `request_app_action` for app navigation and app-level tool actions.
                - Append the `cocaptain_actions` block at the end of every response that involves node content changes.
                - Safe actions are only for non-mutating autonomous app actions. Mutating or review-required app actions must use executionMode `pending`.
                """
            )
        )
    }

    private static let requestAppActionDeclaration = FunctionDeclaration(
        name: CoCaptainFunctionCallAgentAdapter.requestAppActionName,
        description: "Requests a Ficruty app action. The app validates and either executes or stages the action for user review.",
        parameters: [
            "actionId": .string(description: "The exact app action id to request."),
            "executionMode": .enumeration(
                values: ["safe", "pending"],
                description: "`safe` only for non-mutating autonomous actions. `pending` for mutating or review-required actions."
            ),
            "reason": .string(description: "Short reason for requesting the action.")
        ],
        optionalParameters: ["reason"]
    )

    private func buildPrompt(
        userMessage: String,
        context: String?,
        expectsStructuredResponse: Bool,
        availableActions: [AppActionDefinition]
    ) -> String {
        var parts: [String] = []

        if let context, !context.isEmpty {
            parts.append("Current canvas context:\n\(context)")
        }

        if expectsStructuredResponse {
            let autonomousActionLines = availableActions
                .filter { !$0.isMutating && $0.allowsAutonomousExecution }
                .map { action in
                    "- \(action.id.rawValue): \(action.title)"
                }
                .joined(separator: "\n")

            let reviewActionLines = availableActions
                .filter { $0.isMutating || !$0.allowsAutonomousExecution }
                .map { action in
                    "- \(action.id.rawValue): \(action.title) [mutating=\(action.isMutating), autonomous=\(action.allowsAutonomousExecution)]"
                }
                .joined(separator: "\n")

            parts.append(
                """
                Agent contract:
                - Respond conversationally first (concise).
                - If the user is only asking a question, asking for advice, or asking for an opinion, do not request app actions and do not append `cocaptain_actions`.
                - For app navigation or app-level tool actions, use the `request_app_action` function instead of manually writing app actions in XML.
                - For any explicit request to build, make, create, add, change, update, fix, remove, style, implement, document, write to the canvas, or improve existing canvas content, you MUST append an XML block named `cocaptain_actions` with concrete `node_edits`.
                - CRITICAL: If you are building a game or a full feature, use `replace_all` for the html, css, and javascript nodes. 
                - NEVER provide a full file implementation inside the chat text. Put it in the `node_edits`.

                App actions:
                - Prefer `request_app_action(actionId, executionMode, reason)` for app actions.
                - Use executionMode `safe` ONLY for these non-mutating autonomous action ids:
                \(autonomousActionLines.isEmpty ? "- none" : autonomousActionLines)
                - Use executionMode `pending` for these review-required or mutating action ids:
                \(reviewActionLines.isEmpty ? "- none" : reviewActionLines)
                - Never request a mutating or non-autonomous action with executionMode `safe`.

                Node edits:
                - Only target these node roles for edits: srs, html, css, javascript.
                - Code/content changes belong in `node_edits`, not app actions.
                - Every node edit needs a non-empty summary and at least one operation.
                - Exact operations require a non-empty `target`; append/prepend/replace_all do not.

                - XML schema for `cocaptain_actions`:
                
                <cocaptain_actions>
                  <assistant_message>short summary</assistant_message>
                  <safe_actions>
                    <action id="id" />
                  </safe_actions>
                  <pending_actions>
                    <action id="id" />
                  </pending_actions>
                  <node_edits>
                    <node_edit role="html|css|javascript|srs" summary="what changes">
                      <operation type="replace_all|replace_exact|insert_before_exact|insert_after_exact|append|prepend">
                        <target>exact text (only for exact operations)</target>
                        <content><![CDATA[new content]]></content>
                      </operation>
                    </node_edit>
                  </node_edits>
                </cocaptain_actions>
                """
            )
        }

        parts.append("User request:\n\(userMessage)")
        return parts.joined(separator: "\n\n")
    }
}

private extension CoCaptainAgentFunctionCall {
    init(_ functionCall: FunctionCallPart) {
        self.init(
            name: functionCall.name,
            arguments: functionCall.args.compactMapValues(\.cocaptainStringValue),
            id: functionCall.functionId
        )
    }
}

private extension JSONValue {
    var cocaptainStringValue: String? {
        switch self {
        case .string(let value):
            return value
        case .number(let value):
            return String(value)
        case .bool(let value):
            return value ? "true" : "false"
        case .null, .object, .array:
            return nil
        }
    }
}
