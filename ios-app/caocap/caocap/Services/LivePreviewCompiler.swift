import Foundation

public struct LivePreviewCompilation: Hashable {
    public let webViewNodeID: UUID
    public let html: String
}

/// Produces the WebView payload from the canonical HTML, CSS, and JavaScript
/// nodes without mutating project state.
public struct LivePreviewCompiler {
    public init() {}

    public func compile(nodes: [SpatialNode]) -> LivePreviewCompilation? {
        guard let webViewNode = nodes.first(where: { $0.role == .livePreview }),
              let htmlNode = nodes.first(where: { $0.role == .html }) else {
            return nil
        }

        var compiledHTML = htmlNode.textContent ?? ""

        if let cssContent = nodes.first(where: { $0.role == .css })?.textContent,
           !cssContent.isEmpty {
            injectCSS(cssContent, into: &compiledHTML)
        }

        if let jsContent = nodes.first(where: { $0.role == .javascript })?.textContent,
           !jsContent.isEmpty {
            injectJavaScript(jsContent, into: &compiledHTML)
        }

        return LivePreviewCompilation(webViewNodeID: webViewNode.id, html: compiledHTML)
    }

    private func injectCSS(_ cssContent: String, into html: inout String) {
        let styleTag = "\n<style>\n\(cssContent)\n</style>\n"
        if let headRange = html.range(of: "</head>", options: .caseInsensitive) {
            html.insert(contentsOf: styleTag, at: headRange.lowerBound)
        } else if let htmlRange = html.range(of: "<html>", options: .caseInsensitive) {
            html.insert(contentsOf: "<head>\n\(styleTag)\n</head>\n", at: htmlRange.upperBound)
        } else {
            html = styleTag + html
        }
    }

    private func injectJavaScript(_ jsContent: String, into html: inout String) {
        let scriptTag = "\n<script>\n\(jsContent)\n</script>\n"
        if let bodyRange = html.range(of: "</body>", options: .caseInsensitive) {
            html.insert(contentsOf: scriptTag, at: bodyRange.lowerBound)
        } else if let htmlRange = html.range(of: "</html>", options: .caseInsensitive) {
            html.insert(contentsOf: scriptTag, at: htmlRange.lowerBound)
        } else {
            html += scriptTag
        }
    }
}
