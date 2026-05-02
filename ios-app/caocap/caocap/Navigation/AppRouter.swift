import Foundation
import Observation
import SwiftUI

public enum WorkspaceState: Equatable {
    case onboarding
    case home
    case project(String) // filename
}

/// Coordinates top-level workspace navigation and owns the active stores for
/// onboarding, home, and user-created projects.
@MainActor
@Observable
public class AppRouter {
    public var currentWorkspace: WorkspaceState
    public var projects: [String: ProjectStore] = [:]
    private var navigationStack: [WorkspaceState] = []
    
    public let onboardingStore = ProjectStore(
        fileName: "onboarding_v4.json",
        projectName: OnboardingProvider.projectName,
        initialNodes: OnboardingProvider.manifestoNodes,
        initialViewportScale: OnboardingProvider.initialViewportScale
    )
    public let homeStore = ProjectStore(fileName: "home_v6.json", projectName: "Home", initialNodes: HomeProvider.homeNodes, initialViewportScale: 0.5)
    
    /// Returns the store for the current workspace, lazily creating project
    /// stores on cold boot when navigation restores a project filename.
    public var activeStore: ProjectStore {
        switch currentWorkspace {
        case .onboarding: return onboardingStore
        case .home: return homeStore
        case .project(let fileName):
            if let store = projects[fileName] {
                return store
            }
            
            // COLD BOOT FIX: Initialize and cache synchronously to prevent race conditions
            let newStore = ProjectStore(fileName: fileName)
            projects[fileName] = newStore
            return newStore
        }
    }
    
    public init() {
        let hasCompletedOnboarding = UserDefaults.standard.bool(forKey: "hasCompletedOnboarding")
        self.currentWorkspace = hasCompletedOnboarding ? .home : .onboarding
    }
    
    /// Moves between workspaces and records onboarding completion when the user
    /// reaches Home, which makes Home the default workspace on the next launch.
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
            
            // Track last project for the Resume shortcut
            if case .project(let fileName) = workspace {
                UserDefaults.standard.set(fileName, forKey: "lastProjectFileName")
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
        
        let newStore = ProjectStore(fileName: fileName, projectName: "Untitled", initialNodes: ProjectTemplateProvider.defaultNodes, initialViewportScale: 0.3)
        projects[fileName] = newStore
        
        navigate(to: .project(fileName), animated: true)
    }
    
    public func resumeLastProject() {
        if let lastFileName = UserDefaults.standard.string(forKey: "lastProjectFileName") {
            navigate(to: .project(lastFileName), animated: true)
        }
    }
}
