import Foundation

/// Splits model responses into user-visible text and an optional trailing
/// `cocaptain-actions` JSON payload.
public struct CoCaptainAgentParser {
    private static let fence = "```cocaptain-actions"

    public init() {}

    /// Parses the last structured fence in the response. Invalid or incomplete
    /// payloads are treated as plain chat so malformed model output remains safe.
    public func parse(_ response: String) -> CoCaptainParsedResponse {
        guard let startRange = response.range(of: Self.fence, options: .backwards) else {
            return parseLooseTrailingPayload(response)
        }

        guard let jsonStart = response[startRange.upperBound...].firstIndex(of: "\n"),
              let endRange = response.range(of: "\n```", range: jsonStart..<response.endIndex) else {
            return CoCaptainParsedResponse(
                visibleText: response.trimmingCharacters(in: .whitespacesAndNewlines),
                payload: nil,
                diagnostic: "Incomplete `cocaptain-actions` block."
            )
        }

        let visibleText = String(response[..<startRange.lowerBound]).trimmingCharacters(in: .whitespacesAndNewlines)
        let jsonRange = response.index(after: jsonStart)..<endRange.lowerBound
        let json = String(response[jsonRange]).trimmingCharacters(in: .whitespacesAndNewlines)

        guard let data = json.data(using: .utf8),
              let payload = try? JSONDecoder().decode(CoCaptainAgentPayload.self, from: data) else {
            return CoCaptainParsedResponse(
                visibleText: visibleText.isEmpty ? response.trimmingCharacters(in: .whitespacesAndNewlines) : visibleText,
                payload: nil,
                diagnostic: "Malformed JSON in `cocaptain-actions` block."
            )
        }

        let resolvedVisibleText = visibleText.isEmpty ? payload.assistantMessage : visibleText
        return CoCaptainParsedResponse(visibleText: resolvedVisibleText, payload: payload)
    }

    /// Returns the text that is safe to stream into the chat bubble while the
    /// model may still be generating a hidden structured payload or markdown code.
    /// In CAOCAP, we hide all markdown code blocks from the chat bubble because
    /// code implementation belongs on the spatial canvas nodes.
    public func visibleText(from response: String) -> String {
        // We only hide triple-backtick blocks (which are large implementations).
        // Single backtick inline code should remain visible for context.
        if let range = response.range(of: "```") {
            return sanitizedVisiblePrefix(String(response[..<range.lowerBound]))
        }
        if let loosePayloadStart = loosePayloadStart(in: response) {
            return sanitizedVisiblePrefix(String(response[..<loosePayloadStart]))
        }
        return response.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private func parseLooseTrailingPayload(_ response: String) -> CoCaptainParsedResponse {
        guard let jsonStart = loosePayloadStart(in: response) else {
            return CoCaptainParsedResponse(visibleText: response.trimmingCharacters(in: .whitespacesAndNewlines), payload: nil)
        }

        let visibleText = sanitizedVisiblePrefix(String(response[..<jsonStart]))

        guard let jsonEnd = balancedJSONObjectEnd(startingAt: jsonStart, in: response) else {
            // It is a loose payload but it is not finished. Hide it from the visible text.
            return CoCaptainParsedResponse(visibleText: visibleText, payload: nil)
        }

        let remainder = response[jsonEnd...].trimmingCharacters(in: .whitespacesAndNewlines)
        guard remainder.isEmpty else {
            // Extra text after JSON? Treat the whole thing as plain text to be safe.
            return CoCaptainParsedResponse(visibleText: response.trimmingCharacters(in: .whitespacesAndNewlines), payload: nil)
        }

        let json = String(response[jsonStart..<jsonEnd]).trimmingCharacters(in: .whitespacesAndNewlines)

        guard let data = json.data(using: .utf8),
              let payload = try? JSONDecoder().decode(CoCaptainAgentPayload.self, from: data) else {
            return CoCaptainParsedResponse(
                visibleText: visibleText,
                payload: nil,
                diagnostic: "Malformed loose CoCaptain action JSON."
            )
        }

        let resolvedVisibleText = visibleText.isEmpty ? payload.assistantMessage : visibleText
        return CoCaptainParsedResponse(visibleText: resolvedVisibleText, payload: payload)
    }

    private func loosePayloadStart(in response: String) -> String.Index? {
        // Use a regex to find the start of a JSON block containing our payload keys.
        // This is robust against curly quotes, missing spaces, and newlines.
        let pattern = #"\{\s*["'“]?(?:assistantMessage|nodeEdits|safeActions|pendingActions)["'”]?\s*:"#
        
        guard let regex = try? NSRegularExpression(pattern: pattern, options: []) else {
            return nil
        }
        
        let range = NSRange(response.startIndex..<response.endIndex, in: response)
        // We look for the FIRST occurrence of a payload-like block.
        if let match = regex.firstMatch(in: response, options: [], range: range) {
            return Range(match.range, in: response)?.lowerBound
        }
        
        return nil
    }

    private func balancedJSONObjectEnd(startingAt start: String.Index, in response: String) -> String.Index? {
        var depth = 0
        var isInsideString = false
        var isEscaped = false
        var index = start

        while index < response.endIndex {
            let character = response[index]

            if isInsideString {
                if isEscaped {
                    isEscaped = false
                } else if character == "\\" {
                    isEscaped = true
                } else if character == "\"" {
                    isInsideString = false
                }
            } else if character == "\"" {
                isInsideString = true
            } else if character == "{" {
                depth += 1
            } else if character == "}" {
                depth -= 1
                if depth == 0 {
                    return response.index(after: index)
                }
            }

            index = response.index(after: index)
        }

        return nil
    }

    private func sanitizedVisiblePrefix(_ prefix: String) -> String {
        var visibleText = prefix.trimmingCharacters(in: .whitespacesAndNewlines)
        if visibleText.lowercased().hasSuffix("json") {
            visibleText = String(visibleText.dropLast(4)).trimmingCharacters(in: .whitespacesAndNewlines)
        }
        return visibleText
    }
}
