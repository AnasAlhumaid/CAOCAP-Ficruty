import Foundation
import Observation
import OSLog
import SwiftUI

/// Owns the mutable state for one spatial project, including nodes, viewport
/// position, persistence, undo wiring, and live preview compilation.
@Observable
@MainActor
public class ProjectStore {
    /// The display name of the project.
    public var projectName: String = "Untitled Project"
    
    /// The collection of nodes currently visible on the canvas.
    public var nodes: [SpatialNode] = []
    
    /// The saved offset of the infinite canvas.
    public var viewportOffset: CGSize = .zero
    
    /// The saved scale/zoom level of the infinite canvas.
    public var viewportScale: CGFloat = 1.0
    
    /// Tracks if a save operation is currently pending or in progress.
    public var isSaving: Bool = false
    
    /// Historical checkpoints for this project.
    public var history: [SnapshotMetadata] = []
    
    private let logger = Logger(subsystem: "com.ficruty.caocap", category: "Persistence")
    private let persistence: ProjectPersistenceService
    private let persistenceWriter: ProjectPersistenceWriter
    private let livePreviewCompiler = LivePreviewCompiler()
    
    /// A reference to the pending save task used for debouncing disk writes.
    private var saveTask: Task<Void, Never>? = nil
    
    /// The current version of the project file schema. Incremented when
    /// structural changes are made to nodes or the project envelope.
    public static let currentSchemaVersion = ProjectPersistenceService.currentSchemaVersion
    
    public let fileName: String
    
    public init(
        fileName: String = "project_v1.json",
        projectName: String = "Untitled Project",
        initialNodes: [SpatialNode]? = nil,
        initialViewportScale: CGFloat = 1.0,
        persistence: ProjectPersistenceService = ProjectPersistenceService()
    ) {
        self.fileName = fileName
        self.projectName = projectName
        self.viewportScale = initialViewportScale
        self.persistence = persistence
        self.persistenceWriter = ProjectPersistenceWriter(persistence: persistence)
        load(initialNodes: initialNodes, initialViewportScale: initialViewportScale)
    }
    
    /// Loads the project data from disk. If no file is found, initializes with default nodes.
    public func load(initialNodes: [SpatialNode]? = nil, initialViewportScale: CGFloat = 1.0) {
        if !persistence.projectExists(fileName: fileName) {
            logger.info("No saved project found for \(self.fileName). Initializing with defaults.")
            self.nodes = initialNodes ?? OnboardingProvider.manifestoNodes
            self.viewportScale = initialViewportScale
            
            // Ensure Live Preview is compiled immediately for new projects
            compileLivePreview()
            
            // Only perform an initial save for permanent project files.
            if !self.fileName.contains("onboarding") {
                save()
            }
            return
        }
        
        do {
            let result = try persistence.load(fileName: fileName)
            apply(snapshot: result.snapshot)
            logger.info("Successfully loaded project (v\(result.sourceSchemaVersion)) from disk.")
            
            // If we migrated, schedule a save to modernize the file
            if result.didMigrate {
                save()
            }
        } catch ProjectPersistenceError.unsupportedFutureVersion(let version, let current) {
            logger.error("Project version \(version) is newer than app version \(current). Aborting load to prevent data loss.")
            // Fallback to defaults to prevent a crash, but log heavily.
            self.nodes = initialNodes ?? OnboardingProvider.manifestoNodes
            return
        } catch {
            logger.error("Failed to load project: \(error.localizedDescription)")
            // Fallback to initial nodes if data is corrupted or missing
            self.nodes = initialNodes ?? OnboardingProvider.manifestoNodes
        }
        
        // Ensure the Live Preview is synced with the code nodes on startup
        compileLivePreview()
        
        // Load history
        self.history = persistence.listSnapshots(for: fileName)
    }
    
    /// Persists a snapshot of the current project state using a temporary file
    /// and atomic replacement so interrupted writes do not corrupt the main file.
    public func save() {
        let snapshot = ProjectSnapshot(
            schemaVersion: Self.currentSchemaVersion,
            projectName: projectName,
            nodes: nodes,
            viewportOffset: viewportOffset,
            viewportScale: viewportScale
        )
        
        let log = logger
        let fileName = self.fileName
        let persistenceWriter = persistenceWriter
        
        Task(priority: .background) { [weak self] in
            do {
                try await persistenceWriter.save(snapshot, fileName: fileName)
                log.info("Successfully saved project to disk.")
            } catch {
                log.error("Failed to save project: \(error.localizedDescription)")
            }
            await MainActor.run { self?.isSaving = false }
        }
    }
    
    /// Schedules a save operation to run after a short delay (500ms).
    /// If another save is requested before the delay expires, the previous request is cancelled.
    public func requestSave() {
        saveTask?.cancel()
        isSaving = true
        
        saveTask = Task {
            try? await Task.sleep(nanoseconds: 500_000_000) // 0.5 seconds
            
            if !Task.isCancelled {
                compileLivePreview()
                save()
                saveTask = nil
                isSaving = false
            }
        }
    }

    /// Creates a durable checkpoint of the current project state.
    public func createCheckpoint(label: String = "Manual Checkpoint") {
        let snapshot = currentSnapshot()
        let fileName = self.fileName
        let persistence = self.persistence
        
        Task(priority: .background) { [weak self] in
            do {
                let metadata = try persistence.saveSnapshot(snapshot, fileName: fileName, label: label)
                await MainActor.run {
                    self?.history.insert(metadata, at: 0)
                    // Keep history to last 20 for now
                    if (self?.history.count ?? 0) > 20 {
                        self?.history.removeLast()
                    }
                }
            } catch {
                self?.logger.error("Failed to create checkpoint: \(error.localizedDescription)")
            }
        }
    }

    /// Creates an automatic checkpoint before significant mutations (e.g. Co-Captain edits).
    public func createAutoCheckpoint(label: String = "Pre-AI Snapshot") {
        createCheckpoint(label: label)
    }

    /// Restores the project graph from a historical checkpoint.
    public func restore(from metadata: SnapshotMetadata) {
        do {
            let snapshot = try persistence.loadSnapshot(metadata: metadata, for: fileName)
            withAnimation(.spring()) {
                apply(snapshot: snapshot)
            }
            save()
            compileLivePreview()
        } catch {
            logger.error("Failed to restore snapshot: \(error.localizedDescription)")
        }
    }

    private func currentSnapshot() -> ProjectSnapshot {
        ProjectSnapshot(
            schemaVersion: Self.currentSchemaVersion,
            projectName: projectName,
            nodes: nodes,
            viewportOffset: viewportOffset,
            viewportScale: viewportScale
        )
    }
    
    /// Combines canonical code nodes and updates the Live Preview node.
    private func compileLivePreview() {
        guard let compilation = livePreviewCompiler.compile(nodes: nodes),
              let webViewIndex = nodes.firstIndex(where: { $0.id == compilation.webViewNodeID }) else {
            return
        }
        
        // Update the WebView node if the content changed
        if nodes[webViewIndex].htmlContent != compilation.html {
            nodes[webViewIndex].htmlContent = compilation.html
        }
    }

    private func apply(snapshot: ProjectSnapshot) {
        self.projectName = snapshot.projectName ?? self.projectName
        self.nodes = snapshot.nodes
        self.viewportOffset = snapshot.viewportOffset
        self.viewportScale = snapshot.viewportScale
    }
    
    /// A reference to the system UndoManager, injected by the view layer.
    public var undoManager: UndoManager? = nil
    
    /// Incremented whenever the undo stack changes to force UI updates.
    public var undoStackChanged: Int = 0
    
    /// Updates a specific node's position.
    /// - Parameters:
    ///   - id: The UUID of the node to update.
    ///   - position: The new position.
    ///   - persist: If true, triggers a debounced save to disk.
    public func updateNodePosition(id: UUID, position: CGPoint, persist: Bool = true) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            let oldPosition = nodes[index].position
            
            // Register Undo
            // UndoManager always calls back on the main thread;
            // assumeIsolated bridges the nonisolated closure to @MainActor.
            undoManager?.registerUndo(withTarget: self) { target in
                MainActor.assumeIsolated {
                    target.updateNodePosition(id: id, position: oldPosition, persist: persist)
                }
            }
            undoStackChanged += 1
            
            nodes[index].position = position
            if persist {
                requestSave()
            }
        }
    }

    /// Updates a specific node's theme.
    /// - Parameters:
    ///   - id: The UUID of the node to update.
    ///   - theme: The new theme.
    ///   - persist: If true, triggers a debounced save to disk.
    public func updateNodeTheme(id: UUID, theme: NodeTheme, persist: Bool = true) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            let oldTheme = nodes[index].theme
            
            // Register Undo
            undoManager?.registerUndo(withTarget: self) { target in
                MainActor.assumeIsolated {
                    target.updateNodeTheme(id: id, theme: oldTheme, persist: persist)
                }
            }
            undoStackChanged += 1
            
            nodes[index].theme = theme
            if persist {
                requestSave()
            }
        }
    }

    /// Changes a node's fundamental type (e.g. from Code to WebView).
    /// - Parameters:
    ///   - id: The UUID of the node to transform.
    ///   - type: The target NodeType.
    ///   - persist: If true, triggers a debounced save to disk.
    public func updateNodeType(id: UUID, type: NodeType, persist: Bool = true) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            let oldType = nodes[index].type
            
            // Register Undo
            undoManager?.registerUndo(withTarget: self) { target in
                MainActor.assumeIsolated {
                    target.updateNodeType(id: id, type: oldType, persist: persist)
                }
            }
            undoStackChanged += 1
            
            nodes[index].type = type
            
            // Type-specific initialization if content is empty
            switch type {
            case .srs:
                if nodes[index].textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty != false {
                    nodes[index].textContent = SRSScaffold.defaultText
                }
            case .code:
                if nodes[index].textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty != false {
                    nodes[index].textContent = "// Write code here..."
                }
            case .webView:
                // Will be populated by the Live Preview compiler if connected
                break
            case .art:
                // Drawing data starts empty
                break
            case .standard:
                break
            }
            
            if persist {
                requestSave()
            }
            compileLivePreview()
        }
    }

    /// Updates the PencilKit drawing data for an .art node.
    /// - Parameters:
    ///   - id: The UUID of the node to update.
    ///   - data: The serialized PKDrawing data.
    ///   - persist: If true, triggers a debounced save to disk.
    public func updateNodeDrawingData(id: UUID, data: Data, persist: Bool = true) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            let oldData = nodes[index].drawingData
            
            // Register Undo
            undoManager?.registerUndo(withTarget: self) { target in
                MainActor.assumeIsolated {
                    target.updateNodeDrawingData(id: id, data: oldData ?? Data(), persist: persist)
                }
            }
            undoStackChanged += 1
            
            nodes[index].drawingData = data
            if persist {
                requestSave()
            }
        }
    }
    
    /// Updates a specific node's text content.
    /// For SRS nodes, also evaluates and persists the new readiness state.
    /// - Parameters:
    ///   - id: The UUID of the node to update.
    ///   - text: The new text content.
    ///   - persist: If true, triggers a debounced save to disk.
    public func updateNodeTextContent(id: UUID, text: String, persist: Bool = true) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            let oldText = nodes[index].textContent ?? ""
            let oldReadiness = nodes[index].srsReadinessState

            // Register Undo
            // UndoManager always calls back on the main thread;
            // assumeIsolated bridges the nonisolated closure to @MainActor.
            undoManager?.registerUndo(withTarget: self) { target in
                MainActor.assumeIsolated {
                    target.updateNodeTextContent(id: id, text: oldText, persist: persist)
                }
            }
            undoStackChanged += 1

            nodes[index].textContent = text

            // Keep SRS readiness state in sync for .srs nodes.
            if nodes[index].type == .srs {
                let evaluator = SRSReadinessEvaluator()
                nodes[index].srsReadinessState = evaluator.evaluate(text: text, currentState: oldReadiness)
            }

            if persist {
                requestSave()
            }
        }
    }
    
    /// Updates the viewport state.
    /// - Parameters:
    ///   - offset: The new offset.
    ///   - scale: The new scale.
    ///   - persist: If true, triggers a debounced save to disk.
    public func updateViewport(offset: CGSize, scale: CGFloat, persist: Bool = true) {
        self.viewportOffset = offset
        self.viewportScale = scale
        if persist {
            requestSave()
        }
    }
    
    /// Resets the viewport to the center (0,0) at 100% zoom.
    public func resetViewport() {
        withAnimation(.spring(response: 0.5, dampingFraction: 0.8)) {
            self.viewportOffset = .zero
            self.viewportScale = 1.0
        }
        requestSave()
    }
    
    /// Adds a new code node to the project at the current viewport center.
    public func addNode() {
        let newNode = SpatialNode(
            id: UUID(),
            type: .code,
            position: CGPoint(x: -viewportOffset.width / viewportScale, y: -viewportOffset.height / viewportScale),
            title: "New Logic",
            subtitle: "Write your intent here.",
            icon: "plus.square.fill",
            theme: .blue,
            textContent: "// Start coding here..."
        )
        
        // Register Undo
        undoManager?.registerUndo(withTarget: self) { target in
            MainActor.assumeIsolated {
                target.deleteNode(id: newNode.id, persist: true)
            }
        }
        undoStackChanged += 1

        withAnimation(.spring()) {
            nodes.append(newNode)
        }
        requestSave()
    }

    /// Removes a node from the project and cleans up any references to it.
    /// - Parameters:
    ///   - id: The UUID of the node to delete.
    ///   - persist: If true, triggers a debounced save to disk.
    public func deleteNode(id: UUID, persist: Bool = true) {
        guard let index = self.nodes.firstIndex(where: { $0.id == id }) else { return }
        
        // Prevent deletion of protected main nodes (e.g. SRS, HTML, CSS, JS)
        if self.nodes[index].isProtected {
            self.logger.warning("Attempted to delete protected node: \(self.nodes[index].title)")
            return
        }
        
        let removedNode = nodes[index]
        
        // Register Undo
        undoManager?.registerUndo(withTarget: self) { target in
            MainActor.assumeIsolated {
                // To restore a node properly, we'd need to restore its connections too.
                // For now, we restore the node itself.
                target.nodes.append(removedNode) // Simplification: append instead of original index for now
                if persist {
                    target.requestSave()
                }
            }
        }
        undoStackChanged += 1

        withAnimation(.spring()) {
            // 1. Remove the node itself
            nodes.remove(at: index)
            
            // 2. Clean up connections in other nodes
            for i in 0..<nodes.count {
                if nodes[i].nextNodeId == id {
                    nodes[i].nextNodeId = nil
                }
                if let connections = nodes[i].connectedNodeIds {
                    nodes[i].connectedNodeIds = connections.filter { $0 != id }
                    if nodes[i].connectedNodeIds?.isEmpty == true {
                        nodes[i].connectedNodeIds = nil
                    }
                }
            }
        }
        
        if persist {
            requestSave()
        }
    }
}
