import Foundation
import CoreGraphics

public struct HomeProvider {
    
    public static var homeNodes: [SpatialNode] {
        return [
            // Center Core
            SpatialNode(
                id: UUID(),
                position: .zero,
                title: "New Project",
                subtitle: "Start a fresh spatial journey.",
                icon: "plus.circle.fill",
                theme: .green,
                action: .createNewProject
            ),
            
            // Orbiting Constellation
            SpatialNode(
                id: UUID(),
                position: CGPoint(x: -250, y: -150),
                title: "Profile",
                subtitle: "Account & Preferences",
                icon: "person.crop.circle.fill",
                theme: .blue,
                action: .openProfile
            ),
            SpatialNode(
                id: UUID(),
                position: CGPoint(x: 250, y: -150),
                title: "Projects",
                subtitle: "Your Workspace Library",
                icon: "folder.fill",
                theme: .purple,
                action: .openProjectExplorer
            ),
            SpatialNode(
                id: UUID(),
                position: CGPoint(x: -250, y: 150),
                title: "Settings",
                subtitle: "App Tools & Config",
                icon: "gearshape.fill",
                theme: .orange,
                action: .openSettings
            ),
            SpatialNode(
                id: UUID(),
                position: CGPoint(x: 250, y: 150),
                title: "Onboarding",
                subtitle: "Guided Manifesto",
                icon: "graduationcap.fill",
                theme: .blue,
                action: .retryOnboarding
            ),
            
            // Daily Flow Shortcut
            SpatialNode(
                id: UUID(),
                position: CGPoint(x: 0, y: 300),
                title: "Resume",
                subtitle: "Jump back to your last vision.",
                icon: "play.circle.fill",
                theme: .pink,
                action: .resumeLastProject
            )
        ]
    }
}
