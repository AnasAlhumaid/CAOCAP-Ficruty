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
        streamResponse(
            for: prompt,
            context: nil,
            expectsStructuredResponse: false,
            availableActions: []
        )
    }

    public func streamResponse(
        for userMessage: String,
        context: String?,
        expectsStructuredResponse: Bool,
        availableActions: [AppActionDefinition]
    ) -> AsyncThrowingStream<String, Error> {
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
                            continuation.yield(text)
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
            systemInstruction: ModelContent(
                role: "system",
                parts: """
                You are Co-Captain, a spatial programming assistant for the Ficruty platform.
                You have the power to mutate the user's project directly by providing a `cocaptain-actions` JSON block.
                
                Personality:
                - You are a high-performance agentic engine. Be concise, authoritative, and proactive.
                - You do not just "assist"—you "execute mutations" on a spatial canvas.
                - Use technical, precise language. Avoid conversational fluff like "I can help with that" or "Sure thing."
                - You think in architectures and spatial relationships.
                
                Core Rule:
                - You are a direct-action agent. If a user expresses an intent, transform it into a spatial reality (nodes) immediately.
                - Never provide full code in Markdown chat. Code belongs EXCLUSIVELY in `nodeEdits`. 
                - DO NOT use triple backticks (```) for anything other than the `cocaptain-actions` block. 
                - If you suggest a change, you MUST provide the JSON to implement it.
                - Append the `cocaptain-actions` block at the end of every response that involves project changes.
                """
            )
        )
    }

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
            let actionLines = availableActions.map { action in
                "- \(action.id.rawValue): \(action.title) [mutating=\(action.isMutating)]"
            }.joined(separator: "\n")

            parts.append(
                """
                Agent contract:
                - Respond conversationally first (concise).
                - Then, for any request to build, make, create, add, change, update, fix, remove, style, implement, or improve, you MUST append a fenced block named `cocaptain-actions` with concrete `nodeEdits`.
                - CRITICAL: If you are building a game or a full feature, use `replace_all` for the html, css, and javascript nodes. 
                - NEVER provide a full file implementation inside the chat text. Put it in the `nodeEdits`.
                - Only use these action ids:
                \(actionLines.isEmpty ? "- none" : actionLines)
                - Only target these node roles for edits: srs, html, css, javascript.
                - JSON schema for `cocaptain-actions`:
                {
                  "assistantMessage": "short summary",
                  "safeActions": [{"actionId": "id"}],
                  "pendingActions": [{"actionId": "id"}],
                  "nodeEdits": [{
                    "role": "html|css|javascript|srs",
                    "summary": "what changes",
                    "operations": [{
                      "type": "replace_all|replace_exact|insert_before_exact|insert_after_exact|append|prepend",
                      "target": "exact text",
                      "content": "new content"
                    }]
                  }]
                }
                """
            )
        }

        parts.append("User request:\n\(userMessage)")
        return parts.joined(separator: "\n\n")
    }
}
