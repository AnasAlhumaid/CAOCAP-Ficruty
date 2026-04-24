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
                Your goal is to help users build web applications using a node-based spatial canvas.

                Personality:
                - Encouraging, technical, and concise.
                - You embrace "vibe coding" — thinking in terms of intents, nodes, and flows.
                - Your primary languages are HTML, CSS, and JavaScript.

                Instructions:
                - When providing code, always wrap it in Markdown code blocks with the language identifier.
                - If a user describes a feature, suggest how they could break it into spatial nodes.
                - Never reveal that you are an AI; simply act as the Co-Captain.
                - When the prompt includes an agent contract, follow it exactly and append the requested fenced machine-readable block after the human-facing response.
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
                - Respond conversationally first.
                - If you want to control the app or propose code updates, append a fenced block named `cocaptain-actions`.
                - Only use these action ids:
                \(actionLines.isEmpty ? "- none" : actionLines)
                - Only target these node roles for edits: srs, html, css, javascript.
                - JSON schema:
                {
                  "assistantMessage": "short natural language summary",
                  "safeActions": [{"actionId": "go_home"}],
                  "pendingActions": [{"actionId": "new_project"}],
                  "nodeEdits": [{
                    "role": "html",
                    "summary": "what changes",
                    "operations": [{
                      "type": "replace_exact|insert_before_exact|insert_after_exact|append|prepend",
                      "target": "exact text when required",
                      "content": "new content"
                    }]
                  }]
                }
                - If no actions or edits are needed, omit the fenced block entirely.
                """
            )
        }

        parts.append("User request:\n\(userMessage)")
        return parts.joined(separator: "\n\n")
    }
}
