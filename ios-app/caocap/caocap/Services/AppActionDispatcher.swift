import Foundation

public enum AppActionCategory: String, Hashable {
    case navigation
    case project
    case assistant
}

public enum AppActionID: String, CaseIterable, Identifiable, Codable, Hashable {
    case goHome = "go_home"
    case goBack = "go_back"
    case newProject = "new_project"
    case createNode = "create_node"
    case summonCoCaptain = "summon_cocaptain"
    case openFile = "open_file"
    case toggleGrid = "toggle_grid"
    case shareProject = "share_project"
    case proSubscription = "pro_subscription"
    case signIn = "sign_in"
    case openSettings = "open_settings"
    case openProfile = "open_profile"
    case openProjectExplorer = "open_project_explorer"
    case moveNode = "move_node"
    case themeNode = "theme_node"
    case transformNode = "transform_node"
    case help = "help"

    public var id: String { rawValue }
}

public struct AppActionDefinition: Identifiable, Hashable {
    public let id: AppActionID
    public let title: String
    public let icon: String
    public let category: AppActionCategory
    /// Mutating actions change user data or project structure and should be
    /// reviewed before an agent performs them.
    public let isMutating: Bool
    /// Indicates whether trusted non-user callers, such as CoCaptain, may run
    /// this action without an explicit review item.
    public let allowsAutonomousExecution: Bool

    public init(
        id: AppActionID,
        title: String,
        icon: String,
        category: AppActionCategory,
        isMutating: Bool,
        allowsAutonomousExecution: Bool
    ) {
        self.id = id
        self.title = title
        self.icon = icon
        self.category = category
        self.isMutating = isMutating
        self.allowsAutonomousExecution = allowsAutonomousExecution
    }

    public var localizedTitle: String {
        LocalizationManager.shared.localizedString(title)
    }
}

public enum AppActionSource: Hashable {
    case user
    case agentAutomatic
    case agentApproved
}

public struct AppActionResult: Hashable {
    public let actionID: AppActionID
    public let title: String
    public let executed: Bool
    public let message: String

    public init(actionID: AppActionID, title: String, executed: Bool, message: String) {
        self.actionID = actionID
        self.title = title
        self.executed = executed
        self.message = message
    }
}

@MainActor
public protocol AppActionPerforming: AnyObject {
    var availableActions: [AppActionDefinition] { get }
    func definition(for id: AppActionID) -> AppActionDefinition?
    @discardableResult
    func perform(_ id: AppActionID, source: AppActionSource, arguments: [String: String]?) -> AppActionResult
}

/// Central registry and execution boundary for commands. UI surfaces and agents
/// request actions by ID; this dispatcher owns whether they are configured and
/// safe to execute from the given source.
@MainActor
public final class AppActionDispatcher: AppActionPerforming {
    public private(set) var availableActions: [AppActionDefinition] = [
        AppActionDefinition(
            id: .goHome,
            title: "Go to Home",
            icon: "house.fill",
            category: .navigation,
            isMutating: false,
            allowsAutonomousExecution: true
        ),
        AppActionDefinition(
            id: .goBack,
            title: "Go Back",
            icon: "arrow.left.circle",
            category: .navigation,
            isMutating: false,
            allowsAutonomousExecution: true
        ),
        AppActionDefinition(
            id: .newProject,
            title: "New Project",
            icon: "plus.circle.fill",
            category: .project,
            isMutating: true,
            allowsAutonomousExecution: false
        ),
        AppActionDefinition(
            id: .createNode,
            title: "Create New Node",
            icon: "plus.square",
            category: .project,
            isMutating: true,
            allowsAutonomousExecution: false
        ),
        AppActionDefinition(
            id: .summonCoCaptain,
            title: "Summon Co-Captain",
            icon: "sparkles",
            category: .assistant,
            isMutating: false,
            allowsAutonomousExecution: true
        ),
        AppActionDefinition(
            id: .openFile,
            title: "Open File",
            icon: "doc.text.magnifyingglass",
            category: .project,
            isMutating: false,
            allowsAutonomousExecution: false
        ),
        AppActionDefinition(
            id: .toggleGrid,
            title: "Toggle Grid",
            icon: "grid",
            category: .navigation,
            isMutating: false,
            allowsAutonomousExecution: true
        ),
        AppActionDefinition(
            id: .shareProject,
            title: "Share Project",
            icon: "square.and.arrow.up",
            category: .project,
            isMutating: false,
            allowsAutonomousExecution: false
        ),
        AppActionDefinition(
            id: .proSubscription,
            title: "Pro Subscription",
            icon: "crown",
            category: .assistant,
            isMutating: false,
            allowsAutonomousExecution: false
        ),
        AppActionDefinition(
            id: .signIn,
            title: "Sign In",
            icon: "person.crop.circle.badge.checkmark",
            category: .assistant,
            isMutating: false,
            allowsAutonomousExecution: false
        ),
        AppActionDefinition(
            id: .openSettings,
            title: "Open Settings",
            icon: "gearshape.fill",
            category: .assistant,
            isMutating: false,
            allowsAutonomousExecution: true
        ),
        AppActionDefinition(
            id: .openProfile,
            title: "Open Profile",
            icon: "person.fill",
            category: .assistant,
            isMutating: false,
            allowsAutonomousExecution: true
        ),
        AppActionDefinition(
            id: .openProjectExplorer,
            title: "Project Explorer",
            icon: "folder.fill",
            category: .project,
            isMutating: false,
            allowsAutonomousExecution: true
        ),
        AppActionDefinition(
            id: .moveNode,
            title: "Move Node",
            icon: "arrow.up.and.down.and.arrow.left.and.right",
            category: .project,
            isMutating: true,
            allowsAutonomousExecution: false
        ),
        AppActionDefinition(
            id: .themeNode,
            title: "Change Node Theme",
            icon: "paintbrush.fill",
            category: .project,
            isMutating: true,
            allowsAutonomousExecution: false
        ),
        AppActionDefinition(
            id: .transformNode,
            title: "Transform Node Type",
            icon: "arrow.triangle.2.circlepath",
            category: .project,
            isMutating: true,
            allowsAutonomousExecution: false
        ),
        AppActionDefinition(
            id: .help,
            title: "Help & Documentation",
            icon: "questionmark.circle",
            category: .assistant,
            isMutating: false,
            allowsAutonomousExecution: true
        )
    ]

    private var goHomeHandler: (() -> Void)?
    private var goBackHandler: (() -> Void)?
    private var newProjectHandler: (() -> Void)?
    private var createNodeHandler: (() -> Void)?
    private var summonCoCaptainHandler: (() -> Void)?
    private var openFileHandler: (() -> Void)?
    private var toggleGridHandler: (() -> Void)?
    private var shareProjectHandler: (() -> Void)?
    private var proSubscriptionHandler: (() -> Void)?
    private var signInHandler: (() -> Void)?
    private var openSettingsHandler: (() -> Void)?
    private var openProfileHandler: (() -> Void)?
    private var openProjectExplorerHandler: (() -> Void)?
    private var helpHandler: (() -> Void)?
    private var moveNodeHandler: (([String: String]) -> Void)?
    private var themeNodeHandler: (([String: String]) -> Void)?
    private var transformNodeHandler: (([String: String]) -> Void)?

    public init() {}

    /// Injects handlers from the app shell. Definitions stay stable while the
    /// concrete closures can depend on the currently mounted views/services.
    public func configure(
        goHome: @escaping () -> Void,
        goBack: @escaping () -> Void,
        newProject: @escaping () -> Void,
        createNode: @escaping () -> Void,
        summonCoCaptain: @escaping () -> Void,
        openFile: (() -> Void)? = nil,
        toggleGrid: (() -> Void)? = nil,
        shareProject: (() -> Void)? = nil,
        proSubscription: (() -> Void)? = nil,
        signIn: (() -> Void)? = nil,
        openSettings: (() -> Void)? = nil,
        openProfile: (() -> Void)? = nil,
        openProjectExplorer: (() -> Void)? = nil,
        help: (() -> Void)? = nil,
        moveNode: (([String: String]) -> Void)? = nil,
        themeNode: (([String: String]) -> Void)? = nil,
        transformNode: (([String: String]) -> Void)? = nil
    ) {
        self.goHomeHandler = goHome
        self.goBackHandler = goBack
        self.newProjectHandler = newProject
        self.createNodeHandler = createNode
        self.summonCoCaptainHandler = summonCoCaptain
        self.openFileHandler = openFile
        self.toggleGridHandler = toggleGrid
        self.shareProjectHandler = shareProject
        self.proSubscriptionHandler = proSubscription
        self.signInHandler = signIn
        self.openSettingsHandler = openSettings
        self.openProfileHandler = openProfile
        self.openProjectExplorerHandler = openProjectExplorer
        self.helpHandler = help
        self.moveNodeHandler = moveNode
        self.themeNodeHandler = themeNode
        self.transformNodeHandler = transformNode
    }

    public func definition(for id: AppActionID) -> AppActionDefinition? {
        availableActions.first(where: { $0.id == id })
    }

    /// Executes an action if configured. Automatic agent calls are blocked from
    /// mutating or non-autonomous actions; reviewed/user calls may continue.
    @discardableResult
    public func perform(_ id: AppActionID, source: AppActionSource, arguments: [String: String]? = nil) -> AppActionResult {
        guard let definition = definition(for: id) else {
            return AppActionResult(
                actionID: id,
                title: id.rawValue,
                executed: false,
                message: LocalizationManager.shared.localizedString("Action unavailable.")
            )
        }

        if source == .agentAutomatic {
            guard definition.allowsAutonomousExecution && !definition.isMutating else {
                return AppActionResult(
                    actionID: definition.id,
                    title: definition.localizedTitle,
                    executed: false,
                    message: LocalizationManager.shared.localizedString("Action requires approval.")
                )
            }
        }

        let handler: (() -> Void)?
        switch id {
        case .goHome:
            handler = goHomeHandler
        case .goBack:
            handler = goBackHandler
        case .newProject:
            handler = newProjectHandler
        case .createNode:
            handler = createNodeHandler
        case .summonCoCaptain:
            handler = summonCoCaptainHandler
        case .openFile:
            handler = openFileHandler
        case .toggleGrid:
            handler = toggleGridHandler
        case .shareProject:
            handler = shareProjectHandler
        case .proSubscription:
            handler = proSubscriptionHandler
        case .signIn:
            handler = signInHandler
        case .openSettings:
            handler = openSettingsHandler
        case .openProfile:
            handler = openProfileHandler
        case .openProjectExplorer:
            handler = openProjectExplorerHandler
        case .help:
            handler = helpHandler
        case .moveNode:
            if let moveNodeHandler, let arguments {
                moveNodeHandler(arguments)
                return AppActionResult(actionID: definition.id, title: definition.localizedTitle, executed: true, message: "")
            }
            handler = nil
        case .themeNode:
            if let themeNodeHandler, let arguments {
                themeNodeHandler(arguments)
                return AppActionResult(actionID: definition.id, title: definition.localizedTitle, executed: true, message: "")
            }
            handler = nil
        case .transformNode:
            if let transformNodeHandler, let arguments {
                transformNodeHandler(arguments)
                return AppActionResult(actionID: definition.id, title: definition.localizedTitle, executed: true, message: "")
            }
            handler = nil
        }

        guard let handler else {
            return AppActionResult(
                actionID: definition.id,
                title: definition.localizedTitle,
                executed: false,
                message: LocalizationManager.shared.localizedString("Action is not configured.")
            )
        }

        handler()
        return AppActionResult(
            actionID: definition.id,
            title: definition.localizedTitle,
            executed: true,
            message: LocalizationManager.shared.localizedString("appAction.executedMessage", arguments: [definition.localizedTitle])
        )
    }
}
