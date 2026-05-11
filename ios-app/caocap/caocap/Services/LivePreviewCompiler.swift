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
        injectFirebaseHead(from: nodes, into: &compiledHTML)

        if let cssContent = nodes.first(where: { $0.role == .css })?.textContent,
           !cssContent.isEmpty {
            injectCSS(cssContent, into: &compiledHTML)
        }

        if let jsContent = nodes.first(where: { $0.role == .javascript })?.textContent,
           !jsContent.isEmpty {
            injectJavaScript(jsContent, into: &compiledHTML)
        }

        injectFirebasePreviewDiagnostics(into: &compiledHTML)

        return LivePreviewCompilation(webViewNodeID: webViewNode.id, html: compiledHTML)
    }

    private func injectFirebaseHead(from nodes: [SpatialNode], into html: inout String) {
        guard let snippet = FirebasePreviewBootstrap.headInjectionHTML(from: nodes) else { return }
        if let headStart = html.range(of: "<head", options: .caseInsensitive),
           let headOpenEnd = html.range(of: ">", range: headStart.upperBound..<html.endIndex) {
            html.insert(contentsOf: snippet, at: headOpenEnd.upperBound)
            return
        }
        if let htmlStart = html.range(of: "<html", options: .caseInsensitive),
           let htmlOpenEnd = html.range(of: ">", range: htmlStart.upperBound..<html.endIndex) {
            html.insert(contentsOf: "<head>\(snippet)</head>", at: htmlOpenEnd.upperBound)
            return
        }
        html = """
        <!DOCTYPE html>
        <html><head>\(snippet)</head><body>
        \(html)
        </body></html>
        """
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

    /// When Firestore is not ready, show a short bar in the WebView so users know to fix the Firebase node / config.
    private func injectFirebasePreviewDiagnostics(into html: inout String) {
        // Use single-quoted JS strings so Swift multiline literal stays valid.
        let snippet = #"""
        <script>
        (function(){
          function showBanner(){
            if (document.querySelector('[data-caocap-fb-diag]')) return;
            var st = window.__caocapFirestoreStatus;
            var db = window.__caocapFirestore;
            if (st === 'ready' && db) return;
            var el = document.createElement('div');
            el.setAttribute('data-caocap-fb-diag','1');
            el.setAttribute('style','position:fixed;bottom:0;left:0;right:0;padding:10px 12px;font:12px/1.35 -apple-system,BlinkMacSystemFont,sans-serif;background:rgba(18,18,20,0.96);color:#ffb454;border-top:1px solid #555;z-index:2147483646;');
            var err = (window.__caocapFirestoreLastError || '').trim();
            var msg;
            if (!st && !db) {
              msg = 'Live Preview: Firebase did not load. If you have more than one Firebase node, only the first valid config in the list is used (stubs are skipped). Paste the real Web app config from Firebase Console (Project settings → Your apps), remove YOUR_… placeholders, then tap Done on HTML / JS / Firebase to refresh.';
            } else {
              msg = 'Live Preview: Firebase status is "' + st + '"' + (err ? (' — ' + err) : '') + '. Open the Firebase node and fix the Web config (apiKey + projectId), then refresh.';
            }
            el.textContent = msg;
            (document.body || document.documentElement).appendChild(el);
          }
          if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', function(){ setTimeout(showBanner, 80); });
          } else {
            setTimeout(showBanner, 80);
          }
        })();
        </script>
        """#

        if let bodyRange = html.range(of: "</body>", options: .caseInsensitive) {
            html.insert(contentsOf: snippet, at: bodyRange.lowerBound)
        } else if let htmlRange = html.range(of: "</html>", options: .caseInsensitive) {
            html.insert(contentsOf: snippet, at: htmlRange.lowerBound)
        } else {
            html += snippet
        }
    }
}
