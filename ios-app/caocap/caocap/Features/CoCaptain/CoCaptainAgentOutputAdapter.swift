import Foundation

public enum CoCaptainAgentOutputSource: String, Hashable {
    case xml = "xml"
    case functionCall = "function_call"
    case combined = "combined"
}

public struct CoCaptainAgentDirective: Hashable {
    public let preamble: String
    public let visibleText: String
    public let payload: CoCaptainAgentPayload?
    public let diagnostics: [String]
    public let source: CoCaptainAgentOutputSource

    public init(
        preamble: String,
        visibleText: String,
        payload: CoCaptainAgentPayload?,
        diagnostics: [String] = [],
        source: CoCaptainAgentOutputSource
    ) {
        self.preamble = preamble
        self.visibleText = visibleText
        self.payload = payload
        self.diagnostics = diagnostics
        self.source = source
    }
}

/// Converts raw model output into a directive the coordinator can validate and
/// execute. Future Gemini function-call or structured-output adapters should
/// produce this same directive so orchestration stays independent of wire shape.
public protocol CoCaptainAgentOutputAdapting {
    func visibleText(from response: String) -> String
    func directive(from response: String, functionCalls: [CoCaptainAgentFunctionCall]) -> CoCaptainAgentDirective
}

public extension CoCaptainAgentOutputAdapting {
    func directive(from response: String) -> CoCaptainAgentDirective {
        directive(from: response, functionCalls: [])
    }
}

public struct CoCaptainXMLAgentAdapter: CoCaptainAgentOutputAdapting {
    private let parser: CoCaptainAgentParser

    public init(parser: CoCaptainAgentParser = CoCaptainAgentParser()) {
        self.parser = parser
    }

    public func visibleText(from response: String) -> String {
        parser.visibleText(from: response)
    }

    public func directive(from response: String, functionCalls: [CoCaptainAgentFunctionCall]) -> CoCaptainAgentDirective {
        let parsed = parser.parse(response)
        return CoCaptainAgentDirective(
            preamble: parsed.preamble,
            visibleText: parsed.visibleText,
            payload: parsed.payload,
            diagnostics: parsed.diagnostic.map { [$0] } ?? [],
            source: .xml
        )
    }
}

public struct CoCaptainFunctionCallAgentAdapter {
    public static let requestAppActionName = "request_app_action"

    public init() {}

    public func directive(
        from functionCalls: [CoCaptainAgentFunctionCall],
        visibleText: String = ""
    ) -> CoCaptainAgentDirective {
        var safeActions: [CoCaptainAgentAction] = []
        var pendingActions: [CoCaptainAgentAction] = []
        var diagnostics: [String] = []

        for functionCall in functionCalls {
            guard functionCall.name == Self.requestAppActionName else {
                diagnostics.append("Unknown function call `\(functionCall.name)`.")
                continue
            }

            guard let actionID = nonEmptyArgument("actionId", in: functionCall) else {
                diagnostics.append("Function call `\(functionCall.name)` is missing `actionId`.")
                continue
            }

            let executionMode = nonEmptyArgument("executionMode", in: functionCall) ?? "pending"
            let action = CoCaptainAgentAction(actionID: actionID)

            switch executionMode {
            case "safe":
                safeActions.append(action)
            case "pending":
                pendingActions.append(action)
            default:
                diagnostics.append("Function call `\(functionCall.name)` has invalid `executionMode` `\(executionMode)`.")
            }
        }

        let payload = safeActions.isEmpty && pendingActions.isEmpty
            ? nil
            : CoCaptainAgentPayload(
                assistantMessage: visibleText,
                safeActions: safeActions,
                pendingActions: pendingActions,
                nodeEdits: []
            )

        return CoCaptainAgentDirective(
            preamble: visibleText,
            visibleText: visibleText,
            payload: payload,
            diagnostics: diagnostics,
            source: .functionCall
        )
    }

    private func nonEmptyArgument(_ key: String, in functionCall: CoCaptainAgentFunctionCall) -> String? {
        guard let value = functionCall.arguments[key]?.trimmingCharacters(in: .whitespacesAndNewlines),
              !value.isEmpty else {
            return nil
        }
        return value
    }
}

public struct CoCaptainCompositeAgentAdapter: CoCaptainAgentOutputAdapting {
    private let xmlAdapter: CoCaptainXMLAgentAdapter
    private let functionCallAdapter: CoCaptainFunctionCallAgentAdapter

    public init(
        xmlAdapter: CoCaptainXMLAgentAdapter = CoCaptainXMLAgentAdapter(),
        functionCallAdapter: CoCaptainFunctionCallAgentAdapter = CoCaptainFunctionCallAgentAdapter()
    ) {
        self.xmlAdapter = xmlAdapter
        self.functionCallAdapter = functionCallAdapter
    }

    public func visibleText(from response: String) -> String {
        xmlAdapter.visibleText(from: response)
    }

    public func directive(from response: String, functionCalls: [CoCaptainAgentFunctionCall]) -> CoCaptainAgentDirective {
        let fencedDirective = xmlAdapter.directive(from: response, functionCalls: [])
        guard !functionCalls.isEmpty else { return fencedDirective }

        let functionDirective = functionCallAdapter.directive(
            from: functionCalls,
            visibleText: fencedDirective.visibleText
        )

        let payload = combine(functionDirective.payload, fencedDirective.payload)
        return CoCaptainAgentDirective(
            preamble: fencedDirective.preamble,
            visibleText: fencedDirective.visibleText,
            payload: payload,
            diagnostics: functionDirective.diagnostics + fencedDirective.diagnostics,
            source: fencedDirective.payload == nil ? .functionCall : .combined
        )
    }

    private func combine(
        _ functionPayload: CoCaptainAgentPayload?,
        _ fencedPayload: CoCaptainAgentPayload?
    ) -> CoCaptainAgentPayload? {
        guard functionPayload != nil || fencedPayload != nil else { return nil }

        return CoCaptainAgentPayload(
            assistantMessage: fencedPayload?.assistantMessage ?? functionPayload?.assistantMessage ?? "",
            safeActions: functionPayload?.safeActions ?? [],
            pendingActions: functionPayload?.pendingActions ?? [],
            nodeEdits: fencedPayload?.nodeEdits ?? []
        )
    }
}
