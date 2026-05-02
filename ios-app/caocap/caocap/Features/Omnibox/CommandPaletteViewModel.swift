import SwiftUI
import Observation
import OSLog

/// UI state for the command palette. It deliberately emits only `AppActionID`
/// values so action execution remains centralized in `AppActionDispatcher`.
@Observable
public class CommandPaletteViewModel {
    private let logger = Logger(subsystem: "Ficruty", category: "CommandPalette")
    
    public var query: String = "" {
        didSet {
            // Search results are rebuilt from the query, so keep keyboard
            // selection pinned to the first visible command.
            selectedIndex = 0
        }
    }
    public var isPresented: Bool = false
    public var selectedIndex: Int = 0
    public var actions: [AppActionDefinition] = []
    public var nodes: [SpatialNode] = []
    
    /// Filters against localized and canonical titles so command search works
    /// in the UI language while still matching stable English action names.
    public var filteredActions: [AppActionDefinition] {
        let trimmedQuery = query.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmedQuery.isEmpty { return actions }
        
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
