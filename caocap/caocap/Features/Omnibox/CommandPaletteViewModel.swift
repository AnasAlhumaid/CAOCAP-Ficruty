import SwiftUI

public class CommandPaletteViewModel: ObservableObject {
    @Published public var query: String = "" {
        didSet {
            // Reset selection when search changes
            selectedIndex = 0
        }
    }
    @Published public var isPresented: Bool = false
    @Published public var selectedIndex: Int = 0
    public var commands: [Command] = Command.allCases
    
    public var filteredCommands: [Command] {
        if query.isEmpty { return commands }
        return commands.filter { $0.title.localizedCaseInsensitiveContains(query) }
    }
    
    public init() {}
    
    public func setPresented(_ presented: Bool) {
        isPresented = presented
        if !presented {
            query = ""
            selectedIndex = 0
        }
    }
    
    public func moveSelection(direction: Direction) {
        let count = filteredCommands.count
        guard count > 0 else { return }
        
        switch direction {
        case .up:
            selectedIndex = (selectedIndex - 1 + count) % count
        case .down:
            selectedIndex = (selectedIndex + 1) % count
        }
    }
    
    public func confirmSelection() {
        let filtered = filteredCommands
        if selectedIndex >= 0 && selectedIndex < filtered.count {
            let command = filtered[selectedIndex]
            executeCommand(command)
        }
    }
    
    public func executeCommand(_ command: Command) {
        print("Executing command: \(command.title)")
        // In a real app, this would trigger actual logic
        setPresented(false)
    }
    
    public enum Direction {
        case up, down
    }
}

public enum Command: String, CaseIterable, Equatable, Identifiable {
    case openFile = "Open File"
    case createNode = "Create New Node"
    case toggleGrid = "Toggle Grid"
    case shareProject = "Share Project"
    case help = "Help & Documentation"
    
    public var id: String { self.rawValue }
    
    public var title: String { self.rawValue }
    
    public var icon: String {
        switch self {
        case .openFile: return "doc.text.magnifyingglass"
        case .createNode: return "plus.square"
        case .toggleGrid: return "grid"
        case .shareProject: return "square.and.arrow.up"
        case .help: return "questionmark.circle"
        }
    }
}
