import SwiftUI
import Observation
import OSLog

/// UI state for the command palette. It deliberately emits only `AppActionID`
/// values so action execution remains centralized in `AppActionDispatcher`.
@Observable
public class CommandPaletteViewModel {
    private let logger = Logger(subsystem: "Ficruty", category: "CommandPalette")
    
    public enum NavigationPage {
        case main
        case createNode
    }
    
    public var currentPage: NavigationPage = .main
    public var query: String = "" {
        didSet {
            // If user types, we should probably be in the main list to show results
            if !query.isEmpty && currentPage != .main {
                currentPage = .main
            }
            selectedIndex = 0
        }
    }
    public var isPresented: Bool = false
    public var selectedIndex: Int = 0
    public var actions: [AppActionDefinition] = []
    public var nodes: [SpatialNode] = []
    
    /// Main navigation actions
    public var mainActions: [AppActionDefinition] {
        actions.filter { !$0.title.contains("Create") || $0.id == .createNode }
    }
    
    /// Specific node creation actions for the 2x2 grid
    public var creationActions: [AppActionDefinition] {
        actions.filter { $0.title.contains("Create") && $0.id != .createNode }
    }
    
    public var filteredActions: [AppActionDefinition] {
        let trimmedQuery = query.trimmingCharacters(in: .whitespacesAndNewlines)
        let source = currentPage == .main ? mainActions : creationActions
        
        if trimmedQuery.isEmpty { return source }
        
        return actions.filter {
            $0.localizedTitle.localizedCaseInsensitiveContains(trimmedQuery) ||
            $0.title.localizedCaseInsensitiveContains(trimmedQuery)
        }
    }

    public var nodeResults: [NodeSearchResult] {
        searchIndex.search(query: query, in: nodes)
    }

    private var totalResultCount: Int {
        filteredActions.count + nodeResults.count
    }
    
    public var onExecute: ((AppActionID) -> Void)?
    public var onFlyToNode: ((UUID) -> Void)?
    public var onSubmitPrompt: ((String) -> Void)?
    
    @ObservationIgnored
    private let searchIndex = NodeSearchIndex()
    
    public init() {}
    
    /// Closes back to a clean state so each palette open starts from the full
    /// command list.
    public func setPresented(_ presented: Bool) {
        isPresented = presented
        if !presented {
            query = ""
            selectedIndex = 0
        }
    }
    
    public func moveSelection(direction: Direction) {
        let count = totalResultCount
        guard count > 0 else { return }
        
        switch direction {
        case .up:
            selectedIndex = (selectedIndex - 1 + count) % count
        case .down:
            selectedIndex = (selectedIndex + 1) % count
        }
    }
    
    public func confirmSelection() {
        let actions = filteredActions
        let nodeResults = nodeResults
        
        if selectedIndex >= 0 && selectedIndex < actions.count {
            executeAction(actions[selectedIndex])
        } else if selectedIndex >= actions.count && selectedIndex < (actions.count + nodeResults.count) {
            flyToNode(nodeResults[selectedIndex - actions.count])
        } else {
            submitPromptIfNeeded()
        }
    }
    
    /// Emits the chosen action ID and dismisses. The view model does not perform
    /// side effects directly because the same action system is shared with agents.
    public func executeAction(_ action: AppActionDefinition) {
        // If they click "Create New Node" and we're on the main page, drill down
        if action.id.rawValue == "create_node" && currentPage == .main {
            withAnimation(.spring(response: 0.35, dampingFraction: 0.8)) {
                currentPage = .createNode
                selectedIndex = 0
            }
            return
        }
        
        logger.info("Executing action: \(action.title)")
        onExecute?(action.id)
        setPresented(false)
    }

    public func flyToNode(_ result: NodeSearchResult) {
        logger.info("Flying to node: \(result.title)")
        onFlyToNode?(result.id)
        setPresented(false)
    }

    public var canSubmitPrompt: Bool {
        let trimmed = query.trimmingCharacters(in: .whitespacesAndNewlines)
        return !trimmed.isEmpty && filteredActions.isEmpty && nodeResults.isEmpty
    }

    /// Emits an unmatched palette query as a CoCaptain prompt. Listed commands
    /// continue through `onExecute`; this path is only for no-result queries.
    public func submitPromptIfNeeded() {
        let prompt = query.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !prompt.isEmpty, filteredActions.isEmpty, nodeResults.isEmpty else { return }

        logger.info("Submitting unmatched command palette query to CoCaptain")
        onSubmitPrompt?(prompt)
        setPresented(false)
    }
    
    public enum Direction {
        case up, down
    }
}
