import Foundation
import Observation
import SwiftUI

public enum WorkspaceState: Equatable {
    case onboarding
    case home
    case project(String) // filename
}

@MainActor
@Observable
public class AppRouter {
    public var currentWorkspace: WorkspaceState
    public var projects: [String: ProjectStore] = [:]
    private var navigationStack: [WorkspaceState] = []
    
    public let onboardingStore = ProjectStore(fileName: "onboarding_v2.json", projectName: "Onboarding")
    public let homeStore = ProjectStore(fileName: "home_v2.json", projectName: "Home", initialNodes: HomeProvider.homeNodes)
    
    public var activeStore: ProjectStore {
        switch currentWorkspace {
        case .onboarding: return onboardingStore
        case .home: return homeStore
        case .project(let fileName):
            if let store = projects[fileName] {
                return store
            }
            // Fallback (should not happen if managed correctly)
            return homeStore
        }
    }
    
    public init() {
        let hasCompletedOnboarding = UserDefaults.standard.bool(forKey: "hasCompletedOnboarding")
        self.currentWorkspace = hasCompletedOnboarding ? .home : .onboarding
    }
    
    public func navigate(to workspace: WorkspaceState, addToStack: Bool = true, animated: Bool = true) {
        let updateState = {
            if addToStack && self.currentWorkspace != workspace {
                self.navigationStack.append(self.currentWorkspace)
                // Prevent infinite stack growth
                if self.navigationStack.count > 50 {
                    self.navigationStack.removeFirst()
                }
            }
            self.currentWorkspace = workspace
            
            // Update UserDefaults if we navigate to home from onboarding
            if workspace == .home {
                UserDefaults.standard.set(true, forKey: "hasCompletedOnboarding")
            }
        }
        
        if animated {
            withAnimation(.spring()) {
                updateState()
            }
        } else {
            updateState()
        }
    }
    
    public func goBack() {
        guard let previous = navigationStack.popLast() else { return }
        navigate(to: previous, addToStack: false, animated: true)
    }
    
    public func goHome() {
        navigate(to: .home, animated: true)
    }
    
    public func createNewProject() {
        let id = UUID().uuidString.prefix(8)
        let fileName = "project_\(id).json"
        
        let webViewId = UUID()
        let srsId = UUID()
        let htmlId = UUID()
        let cssId = UUID()
        let jsId = UUID()
        
        let initialNodes = [
            SpatialNode(
                id: webViewId,
                type: .webView,
                position: CGPoint(x: 375, y: 0),
                title: "Live Preview",
                subtitle: "Your mini-game will render here.",
                icon: "play.circle.fill",
                theme: .blue,
                htmlContent: """
                <!DOCTYPE html>
                <html>
                <head>
                <style>
                    body {
                        background-color: #0d0d0d;
                        color: #ffffff;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        margin: 0;
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                    }
                    h1 {
                        font-size: 3rem;
                        background: linear-gradient(90deg, #00C9FF 0%, #92FE9D 100%);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                    }
                </style>
                </head>
                <body>
                    <h1>Hello World!</h1>
                </body>
                </html>
                """
            ),
            SpatialNode(
                id: srsId,
                position: CGPoint(x: -275, y: -200),
                title: "Software Requirements (SRS)",
                subtitle: "Define the core logic and rules here.",
                icon: "doc.text.fill",
                theme: .purple
            ),
            SpatialNode(
                id: htmlId,
                position: CGPoint(x: -275, y: 0),
                title: "HTML",
                subtitle: "Document structure.",
                icon: "chevron.left.slash.chevron.right",
                theme: .orange,
                connectedNodeIds: [srsId, webViewId]
            ),
            SpatialNode(
                id: cssId,
                position: CGPoint(x: -475, y: 200),
                title: "CSS",
                subtitle: "Styling and layout.",
                icon: "paintpalette.fill",
                theme: .blue,
                connectedNodeIds: [htmlId]
            ),
            SpatialNode(
                id: jsId,
                position: CGPoint(x: -75, y: 200),
                title: "JavaScript",
                subtitle: "Interactivity and logic.",
                icon: "script",
                theme: .green,
                connectedNodeIds: [htmlId]
            )
        ]
        
        let newStore = ProjectStore(fileName: fileName, projectName: "New Project \(id)", initialNodes: initialNodes, initialViewportScale: 0.3)
        projects[fileName] = newStore
        
        navigate(to: .project(fileName), animated: true)
    }
}
