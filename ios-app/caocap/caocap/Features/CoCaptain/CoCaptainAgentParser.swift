import Foundation

/// Splits model responses into user-visible text and an optional trailing
/// `cocaptain_actions` XML payload.
public struct CoCaptainAgentParser {
    private static let startTag = "<cocaptain_actions>"
    private static let endTag = "</cocaptain_actions>"

    public init() {}

    /// Parses the structured XML block in the response.
    public func parse(_ response: String) -> CoCaptainParsedResponse {
        guard let startRange = response.range(of: Self.startTag, options: .backwards),
              let endRange = response.range(of: Self.endTag, options: .backwards),
              startRange.lowerBound < endRange.lowerBound else {
            return CoCaptainParsedResponse(
                preamble: response.trimmingCharacters(in: .whitespacesAndNewlines),
                payload: nil
            )
        }

        let preamble = String(response[..<startRange.lowerBound]).trimmingCharacters(in: .whitespacesAndNewlines)
        let xmlRange = startRange.lowerBound..<endRange.upperBound
        let xml = String(response[xmlRange])

        let assistantMessage = extractTag(name: "assistant_message", from: xml) ?? ""
        
        let safeActions = extractTags(name: "safe_actions", from: xml).flatMap { 
            extractSelfClosingTags(name: "action", from: $0) 
        }.compactMap { attrs -> CoCaptainAgentAction? in
            guard let id = attrs["id"] else { return nil }
            return CoCaptainAgentAction(actionID: id)
        }
        
        let pendingActions = extractTags(name: "pending_actions", from: xml).flatMap { 
            extractSelfClosingTags(name: "action", from: $0) 
        }.compactMap { attrs -> CoCaptainAgentAction? in
            guard let id = attrs["id"] else { return nil }
            return CoCaptainAgentAction(actionID: id)
        }
        
        let nodeEdits = extractTagMatches(name: "node_edit", from: xml).compactMap { item -> CoCaptainNodeEditProposal? in
            let content = item.content
            let attrs = item.attributes
            guard let roleStr = attrs["role"], 
                  let role = NodeRole(rawValue: roleStr) else { return nil }
            
            let summary = attrs["summary"] ?? ""
            let operations = extractTagMatches(name: "operation", from: content).compactMap { opItem -> NodePatchOperation? in
                let opContent = opItem.content
                let opAttrs = opItem.attributes
                guard let typeStr = opAttrs["type"],
                      let type = NodePatchOperationType(rawValue: typeStr) else { return nil }
                
                let target = extractTag(name: "target", from: opContent)
                let body = extractCDATA(from: opContent) ?? extractTag(name: "content", from: opContent) ?? ""
                
                return NodePatchOperation(type: type, target: target, content: body)
            }
            
            return CoCaptainNodeEditProposal(role: role, summary: summary, operations: operations)
        }

        let payload = CoCaptainAgentPayload(
            assistantMessage: assistantMessage,
            safeActions: safeActions,
            pendingActions: pendingActions,
            nodeEdits: nodeEdits
        )

        return CoCaptainParsedResponse(preamble: preamble, payload: payload)
    }

    /// Returns the text that is safe to stream into the chat bubble.
    public func visibleText(from response: String) -> String {
        if let range = response.range(of: Self.startTag) {
            return response[..<range.lowerBound].trimmingCharacters(in: .whitespacesAndNewlines)
        }
        return response.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    // MARK: - XML Extraction Helpers

    private func extractTag(name: String, from text: String) -> String? {
        let pattern = "<\(name)>(.*?)</\(name)>"
        let regex = try? NSRegularExpression(pattern: pattern, options: [.dotMatchesLineSeparators])
        let nsRange = NSRange(text.startIndex..<text.endIndex, in: text)
        if let match = regex?.firstMatch(in: text, options: [], range: nsRange) {
            if let range = Range(match.range(at: 1), in: text) {
                return String(text[range]).trimmingCharacters(in: .whitespacesAndNewlines)
            }
        }
        return nil
    }

    private struct TagMatch {
        let content: String
        let attributes: [String: String]
    }

    private func extractTags(name: String, from text: String) -> [String] {
        let pattern = "<\(name)[^>]*>(.*?)</\(name)>"
        let regex = try? NSRegularExpression(pattern: pattern, options: [.dotMatchesLineSeparators])
        let nsRange = NSRange(text.startIndex..<text.endIndex, in: text)
        let matches = regex?.matches(in: text, options: [], range: nsRange) ?? []
        return matches.compactMap { match in
            if let range = Range(match.range(at: 1), in: text) {
                return String(text[range]).trimmingCharacters(in: .whitespacesAndNewlines)
            }
            return nil
        }
    }

    private func extractTagMatches(name: String, from text: String) -> [TagMatch] {
        let pattern = "<\(name)([^>]*)>(.*?)</\(name)>"
        let regex = try? NSRegularExpression(pattern: pattern, options: [.dotMatchesLineSeparators])
        let nsRange = NSRange(text.startIndex..<text.endIndex, in: text)
        let matches = regex?.matches(in: text, options: [], range: nsRange) ?? []
        return matches.compactMap { match in
            guard let attrRange = Range(match.range(at: 1), in: text),
                  let contentRange = Range(match.range(at: 2), in: text) else { return nil }
            
            let attrString = String(text[attrRange])
            let content = String(text[contentRange]).trimmingCharacters(in: .whitespacesAndNewlines)
            let attributes = parseAttributes(attrString)
            
            return TagMatch(content: content, attributes: attributes)
        }
    }

    private func extractSelfClosingTags(name: String, from text: String) -> [[String: String]] {
        let pattern = "<\(name)\\s+([^>]*?)/>"
        let regex = try? NSRegularExpression(pattern: pattern, options: [])
        let nsRange = NSRange(text.startIndex..<text.endIndex, in: text)
        let matches = regex?.matches(in: text, options: [], range: nsRange) ?? []
        return matches.compactMap { match in
            if let range = Range(match.range(at: 1), in: text) {
                return parseAttributes(String(text[range]))
            }
            return nil
        }
    }

    private func parseAttributes(_ attrString: String) -> [String: String] {
        var attributes: [String: String] = [:]
        let pattern = "(\\w+)=[\"'](.*?)[\"']"
        let regex = try? NSRegularExpression(pattern: pattern, options: [])
        let nsRange = NSRange(attrString.startIndex..<attrString.endIndex, in: attrString)
        let matches = regex?.matches(in: attrString, options: [], range: nsRange) ?? []
        for match in matches {
            if let keyRange = Range(match.range(at: 1), in: attrString),
               let valRange = Range(match.range(at: 2), in: attrString) {
                attributes[String(attrString[keyRange])] = String(attrString[valRange])
            }
        }
        return attributes
    }

    private func extractCDATA(from text: String) -> String? {
        let pattern = "<!\\[CDATA\\[(.*?)\\]\\]>"
        let regex = try? NSRegularExpression(pattern: pattern, options: [.dotMatchesLineSeparators])
        let nsRange = NSRange(text.startIndex..<text.endIndex, in: text)
        if let match = regex?.firstMatch(in: text, options: [], range: nsRange) {
            if let range = Range(match.range(at: 1), in: text) {
                return String(text[range])
            }
        }
        return nil
    }
}
