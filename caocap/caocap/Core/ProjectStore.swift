import Foundation
import Observation
import OSLog

@Observable
@MainActor
public class ProjectStore {
    /// The collection of nodes currently visible on the canvas.
    public var nodes: [SpatialNode] = []
    
    /// The saved offset of the infinite canvas.
    public var viewportOffset: CGSize = .zero
    
    /// The saved scale/zoom level of the infinite canvas.
    public var viewportScale: CGFloat = 1.0
    
    private let logger = Logger(subsystem: "com.ficruty.caocap", category: "Persistence")
    
    /// A reference to the pending save task used for debouncing disk writes.
    private var saveTask: Task<Void, Never>? = nil
    
    /// The internal structure used for JSON serialization of the project state.
    private struct ProjectData: Codable {
        let nodes: [SpatialNode]
        let viewportOffset: CGSize
        let viewportScale: CGFloat
    }
    
    /// Returns the local file URL where project data is stored.
    /// This property also ensures the parent directory exists.
    private var fileURL: URL {
        let paths = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)
        let appSupport = paths[0].appendingPathComponent("com.ficruty.caocap", isDirectory: true)
        
        // Create the directory if it doesn't exist (e.g., on first run)
        try? FileManager.default.createDirectory(at: appSupport, withIntermediateDirectories: true)
        
        return appSupport.appendingPathComponent("project_v1.json")
    }
    
    public init() {
        load()
    }
    
    /// Loads the project data from disk. If no file is found, initializes with default manifesto nodes.
    public func load() {
        let url = fileURL
        
        if !FileManager.default.fileExists(atPath: url.path) {
            logger.info("No saved project found. Initializing with defaults.")
            self.nodes = OnboardingProvider.manifestoNodes
            save()
            return
        }
        
        do {
            let data = try Data(contentsOf: url)
            let decoded = try JSONDecoder().decode(ProjectData.self, from: data)
            
            // Update the live state with the decoded data
            self.nodes = decoded.nodes
            self.viewportOffset = decoded.viewportOffset
            self.viewportScale = decoded.viewportScale
            
            logger.info("Successfully loaded project from disk.")
        } catch {
            logger.error("Failed to load project: \(error.localizedDescription)")
            // Fallback to onboarding content if data is corrupted
            self.nodes = OnboardingProvider.manifestoNodes
        }
    }
    
    /// Performs an atomic save of the current project state to disk.
    public func save() {
        let url = fileURL
        let tempURL = url.appendingPathExtension("tmp")
        
        do {
            let projectData = ProjectData(
                nodes: nodes,
                viewportOffset: viewportOffset,
                viewportScale: viewportScale
            )
            
            let encoder = JSONEncoder()
            encoder.outputFormatting = .prettyPrinted
            let data = try encoder.encode(projectData)
            
            // 1. Write to a temporary file first
            try data.write(to: tempURL, options: .atomic)
            
            // 2. Perform an atomic swap to prevent data corruption during write
            if FileManager.default.fileExists(atPath: url.path) {
                _ = try FileManager.default.replaceItemAt(url, withItemAt: tempURL)
            } else {
                try FileManager.default.moveItem(at: tempURL, to: url)
            }
            
            logger.info("Successfully saved project to disk.")
        } catch {
            logger.error("Failed to save project: \(error.localizedDescription)")
        }
    }
    
    /// Schedules a save operation to run after a short delay (500ms).
    /// If another save is requested before the delay expires, the previous request is cancelled.
    public func requestSave() {
        saveTask?.cancel()
        
        saveTask = Task {
            try? await Task.sleep(nanoseconds: 500_000_000) // 0.5 seconds
            
            if !Task.isCancelled {
                save()
            }
        }
    }
    
    /// Updates a specific node's position and triggers a debounced save.
    public func updateNodePosition(id: UUID, position: CGPoint) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            nodes[index].position = position
            requestSave()
        }
    }
    
    /// Updates the viewport state and triggers a debounced save.
    public func updateViewport(offset: CGSize, scale: CGFloat) {
        self.viewportOffset = offset
        self.viewportScale = scale
        requestSave()
    }
}
