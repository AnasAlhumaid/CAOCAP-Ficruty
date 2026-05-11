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
        
        /* 
        // Automatic grouping connections removed to declutter the canvas.
        // The preview will still render correctly based on the node roles.
        let sourceNodeIds = nodes.filter { [.html, .css, .javascript].contains($0.role) }.map { $0.id }
        if Set(nodes[webViewIndex].inputNodeIds ?? []) != Set(sourceNodeIds) {
            nodes[webViewIndex].inputNodeIds = sourceNodeIds
        }
        
        if let srsNode = nodes.first(where: { $0.role == .srs }),
           let htmlIndex = nodes.firstIndex(where: { $0.role == .html }) {
            if !(nodes[htmlIndex].inputNodeIds ?? []).contains(srsNode.id) {
                var currentInputs = nodes[htmlIndex].inputNodeIds ?? []
                currentInputs.append(srsNode.id)
                nodes[htmlIndex].inputNodeIds = currentInputs
            }
        }
        */
        
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
            case .table:
                if nodes[index].textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty != false {
                    nodes[index].textContent = "Header 1, Header 2\nData 1, Data 2"
                }
            case .text:
                if nodes[index].textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty != false {
                    nodes[index].textContent = "Write notes here..."
                }
            case .number:
                if nodes[index].textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty != false {
                    nodes[index].textContent = "0"
                }
            case .calculation:
                if nodes[index].operation == nil {
                    nodes[index].operation = .add
                }
            case .display:
                break
            case .standard:
                break
            case .aiAgent:
                if nodes[index].promptTemplate == nil {
                    nodes[index].promptTemplate = "Compare {{input1}} and {{input2}}"
                }
            case .chart:
                if nodes[index].chartStyle == nil {
                    nodes[index].chartStyle = .bar
                }
            case .firebase:
                if nodes[index].textContent?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty != false {
                    nodes[index].textContent = FirebasePreviewBootstrap.placeholderConfigJSON()
                }
            }
            
            if persist {
                requestSave()
            }
            compileLivePreview()
        }
    }
    
    public func updateNodeChartStyle(id: UUID, style: ChartStyle) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            nodes[index].chartStyle = style
            requestSave()
        }
    }
    
    public func updateNodeChartXColumn(id: UUID, index: Int?) {
        if let idx = nodes.firstIndex(where: { $0.id == id }) {
            nodes[idx].chartXColumnIndex = index
            requestSave()
        }
    }
    
    public func updateNodeChartYColumn(id: UUID, index: Int?) {
        if let idx = nodes.firstIndex(where: { $0.id == id }) {
            nodes[idx].chartYColumnIndex = index
            requestSave()
        }
    }
    
    public func updateNodeChartHasHeaderRow(id: UUID, hasHeader: Bool) {
        if let idx = nodes.firstIndex(where: { $0.id == id }) {
            nodes[idx].chartHasHeaderRow = hasHeader
            requestSave()
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
            
            recalculateGraph()
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

    /// Automatically organizes all nodes into a context-aware layout.
    public func organizeNodes(isHome: Bool = false) {
        guard !nodes.isEmpty else { return }
        
        var nodePositions = [UUID: CGPoint]()
        
        if isHome {
            // VERTICAL STACK LAYOUT (Home) - Nodes placed closer together above each other
            let verticalSpacing: CGFloat = 150
            let startY: CGFloat = -CGFloat(nodes.count - 1) * verticalSpacing / 2
            
            for (index, node) in nodes.enumerated() {
                let y = startY + CGFloat(index) * verticalSpacing
                nodePositions[node.id] = CGPoint(x: 0, y: y)
            }
        } else {
            // CENTRALITY & GROUPED LAYOUT (Project)
            // 1. Group nodes by connectivity (Clusters)
            var clusters: [[UUID]] = []
            var unvisited = Set(nodes.map { $0.id })
            
            while let startId = unvisited.first {
                var currentCluster: [UUID] = []
                var queue = [startId]
                unvisited.remove(startId)
                
                while !queue.isEmpty {
                    let id = queue.removeFirst()
                    currentCluster.append(id)
                    
                    let node = nodes.first { $0.id == id }
                    let relatedIds = (node?.inputNodeIds ?? []) + nodes.filter { ($0.inputNodeIds ?? []).contains(id) }.map { $0.id }
                    
                    for relatedId in relatedIds {
                        if unvisited.contains(relatedId) {
                            unvisited.remove(relatedId)
                            queue.append(relatedId)
                        }
                    }
                }
                clusters.append(currentCluster)
            }
            
            // 2. Lay out each cluster
            let columnWidth: CGFloat = 800
            let rowHeight: CGFloat = 600
            
            for (clusterIndex, clusterIds) in clusters.enumerated() {
                let col = clusterIndex % 2
                let row = clusterIndex / 2
                let clusterCenter = CGPoint(x: CGFloat(col) * columnWidth, y: CGFloat(row) * rowHeight)
                
                // Find highly connected node (The Hub)
                let sortedByConnectivity = clusterIds.sorted { idA, idB in
                    let countA = (nodes.first { $0.id == idA }?.inputNodeIds?.count ?? 0) + nodes.filter { ($0.inputNodeIds ?? []).contains(idA) }.count
                    let countB = (nodes.first { $0.id == idB }?.inputNodeIds?.count ?? 0) + nodes.filter { ($0.inputNodeIds ?? []).contains(idB) }.count
                    return countA > countB
                }
                
                // Place Hub at center, others around it
                if let hubId = sortedByConnectivity.first {
                    nodePositions[hubId] = clusterCenter
                    
                    let others = sortedByConnectivity.dropFirst()
                    let radius: CGFloat = 300
                    for (i, otherId) in others.enumerated() {
                        let angle = (Double(i) / Double(others.count)) * 2.0 * .pi
                        nodePositions[otherId] = CGPoint(
                            x: clusterCenter.x + radius * cos(angle),
                            y: clusterCenter.y + radius * sin(angle)
                        )
                    }
                }
            }
        }
        
        // 3. Apply with animation
        withAnimation(.spring(response: 0.8, dampingFraction: 0.7)) {
            for (id, pos) in nodePositions {
                if let index = nodes.firstIndex(where: { $0.id == id }) {
                    nodes[index].position = pos
                }
            }
        }
        
        requestSave()
        HapticsManager.shared.notification(.success)
    }
    
    /// Adds a new node to the project at the current viewport center.
    public func addNode(type: NodeType = .code) {
        let baseTitle = type == .code ? "New Logic" : type.displayName
        let uniqueTitle = generateUniqueTitle(base: baseTitle)

        let subtitle: String?
        let initialText: String?
        switch type {
        case .code:
            subtitle = "Write your intent here."
            initialText = "// Start coding here..."
        case .firebase:
            subtitle = "Project settings → Your apps → Web app config"
            initialText = FirebasePreviewBootstrap.placeholderConfigJSON()
        default:
            subtitle = nil
            initialText = nil
        }

        let newNode = SpatialNode(
            id: UUID(),
            type: type,
            position: CGPoint(x: -viewportOffset.width / viewportScale, y: -viewportOffset.height / viewportScale),
            title: uniqueTitle,
            subtitle: subtitle,
            icon: nodeIcon(for: type),
            theme: nodeTheme(for: type),
            textContent: initialText
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
        recalculateGraph()
    }

    private func nodeIcon(for type: NodeType) -> String {
        switch type {
        case .code: return "plus.square.fill"
        case .text: return "text.justify.left"
        case .number: return "text.cursor"
        case .table: return "tablecells.fill"
        case .calculation: return "plus.forwardslash.minus"
        case .display: return "opticaldisc.fill"
        case .srs: return "doc.text.fill"
        case .webView: return "play.display"
        case .art: return "pencil.tip"
        case .standard: return "square.grid.2x2"
        case .aiAgent: return "brain.head.profile.fill"
        case .chart: return "chart.line.uptrend.xyaxis"
        case .firebase: return "flame.fill"
        }
    }

    private func nodeTheme(for type: NodeType) -> NodeTheme {
        switch type {
        case .text: return .blue
        case .number: return .blue
        case .table: return .cyan
        case .calculation: return .orange
        case .display: return .green
        case .aiAgent: return .indigo
        case .chart: return .purple
        case .firebase: return .orange
        default: return .blue
        }
    }

    private func generateUniqueTitle(base: String) -> String {
        var candidate = base
        var count = 1
        // If the base itself exists, start numbering immediately
        if nodes.contains(where: { $0.title.lowercased() == candidate.lowercased() }) {
            while nodes.contains(where: { $0.title.lowercased() == "\(base) \(count)".lowercased() }) {
                count += 1
            }
            candidate = "\(base) \(count)"
        }
        return candidate
    }

    public func updateNodeTitle(id: UUID, title: String) {
        let trimmed = title.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        
        // Prevent duplicates (excluding self)
        if nodes.contains(where: { $0.id != id && $0.title.lowercased() == trimmed.lowercased() }) {
            // Silently ignore or we could handle with feedback
            return 
        }
        
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            nodes[index].title = trimmed
            requestSave()
        }
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
                if let inputs = nodes[i].inputNodeIds {
                    nodes[i].inputNodeIds = inputs.filter { $0 != id }
                    if nodes[i].inputNodeIds?.isEmpty == true {
                        nodes[i].inputNodeIds = nil
                    }
                }
            }
        }
        
        if persist {
            requestSave()
        }
    }

    public func updateNodeOperation(id: UUID, operation: ArithmeticOperation, persist: Bool = true) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            let oldOp = nodes[index].operation ?? .add
            
            undoManager?.registerUndo(withTarget: self) { target in
                MainActor.assumeIsolated {
                    target.updateNodeOperation(id: id, operation: oldOp, persist: persist)
                }
            }
            undoStackChanged += 1
            
            nodes[index].operation = operation
            if persist {
                requestSave()
            }
            recalculateGraph()
        }
    }

    /// Evaluates the reactive calculation graph.
    /// Values flow from Text nodes -> Calculation nodes -> Display nodes.
    public func recalculateGraph() {
        var changed = false
        
        // Multi-pass to handle chains (e.g. A + B -> C, then C + D -> E)
        for _ in 0..<3 {
            var currentPassChanged = false
            
            for i in 0..<nodes.count {
                let node = nodes[i]
                
                if node.type == .calculation {
                    let inputs = (node.inputNodeIds ?? []).compactMap { id in
                        nodes.first(where: { $0.id == id })
                    }
                    
                    let values = inputs.compactMap { inputNode -> Double? in
                        // 1. Try to get a reactive output value first (for chained calculations)
                        if let outVal = inputNode.outputValue, outVal != 0 {
                            return outVal
                        }
                        
                        // 2. Fallback to parsing the text content (for Number and Text nodes)
                        if let text = inputNode.textContent {
                            // Strip non-numeric characters except decimals
                            let cleaned = text.filter { "0123456789.".contains($0) }
                            return Double(cleaned)
                        }
                        
                        return nil
                    }
                    
                    let result: Double
                    let op = node.operation ?? .add
                    
                    if values.isEmpty {
                        result = 0
                    } else {
                        switch op {
                        case .add:
                            result = values.reduce(0, +)
                        case .subtract:
                            result = values.count > 1 ? values.dropFirst().reduce(values[0], -) : (values.first ?? 0)
                        case .multiply:
                            result = values.reduce(1, *)
                        case .divide:
                            let first = values.first ?? 0
                            let others = values.dropFirst()
                            result = others.contains(0) ? 0 : others.reduce(first, /)
                        }
                    }
                    
                    if nodes[i].outputValue != result {
                        nodes[i].outputValue = result
                        currentPassChanged = true
                        changed = true
                    }
                } else if node.type == .display {
                    // Display nodes mirror their first input
                    if let inputId = node.inputNodeIds?.first,
                       let inputNode = nodes.first(where: { $0.id == inputId }) {
                        
                        // Use the same smart extraction logic
                        let value: Double
                        if let outVal = inputNode.outputValue, outVal != 0 {
                            value = outVal
                        } else {
                            let text = inputNode.textContent ?? "0"
                            let cleaned = text.filter { "0123456789.".contains($0) }
                            value = Double(cleaned) ?? 0
                        }
                        
                        if nodes[i].outputValue != value {
                            nodes[i].outputValue = value
                            currentPassChanged = true
                            changed = true
                        }
                    }
                } else if node.type == .aiAgent {
                    // AI Agents use aiResponse, but they can flow into Display nodes as well.
                    // RecalculateGraph primarily handles numeric propagation.
                }
            }
            
            if !currentPassChanged { break }
        }
    }

    public func updateNodeInputs(id: UUID, inputNodeIds: [UUID]) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            nodes[index].inputNodeIds = inputNodeIds
            recalculateGraph()
            requestSave()
            
            // Auto-evaluation removed to prevent unwanted requests while configuring.
            // Users will trigger AI agents manually via the UI.
        }
    }

    public func updateNodePrompt(id: UUID, prompt: String) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            nodes[index].promptTemplate = prompt
            // evaluateAINode(id: id) // Removed: Don't trigger while typing
            requestSave()
        }
    }

    public func updateNodeDisplayStyle(id: UUID, style: DisplayStyle) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            nodes[index].displayStyle = style
            requestSave()
        }
    }

    /// Optional Firestore path for Firebase config nodes (preview exposes `window.__caocapFirestoreDefaultPath`).
    public func updateNodeFirebaseFirestorePath(id: UUID, path: String?, persist: Bool = true) {
        if let index = nodes.firstIndex(where: { $0.id == id }) {
            let oldPath = nodes[index].firebaseFirestorePath
            undoManager?.registerUndo(withTarget: self) { target in
                MainActor.assumeIsolated {
                    target.updateNodeFirebaseFirestorePath(id: id, path: oldPath, persist: persist)
                }
            }
            undoStackChanged += 1
            nodes[index].firebaseFirestorePath = path
            if persist {
                requestSave()
            }
            compileLivePreview()
        }
    }

    public func evaluateAINode(id: UUID) {
        guard let index = nodes.firstIndex(where: { $0.id == id }),
              nodes[index].type == .aiAgent,
              let template = nodes[index].promptTemplate, !template.isEmpty else { return }
        
        // Build the prompt by injecting content from ALL nodes in the project
        // This makes it much more intuitive (Global Node Awareness)
        var finalPrompt = template
        
        for inputNode in nodes {
            // Priority: Numeric Output -> Text Content -> AI Response -> Subtitle
            var valueStr = ""
            
            if let outVal = inputNode.outputValue, outVal != 0 {
                valueStr = String(format: "%.2f", outVal)
            } else if let text = inputNode.textContent, !text.isEmpty {
                // If it's a number node, try to parse the text as a number
                if inputNode.type == .number || inputNode.type == .calculation {
                    let cleaned = text.filter { "0123456789.".contains($0) }
                    if let dbl = Double(cleaned) {
                        valueStr = String(format: "%.2f", dbl)
                    } else {
                        valueStr = text
                    }
                } else {
                    valueStr = text
                }
            } else {
                valueStr = inputNode.aiResponse ?? inputNode.subtitle ?? ""
            }
            
            // For tables, we can wrap the content in a data block to help the AI
            let processedContent = inputNode.type == .table ? "### DATA TABLE: \(inputNode.title) ###\n\(valueStr)\n###################" : valueStr
            
            // Replace title-based tags (handling both with and without spaces, case-insensitive)
            let title = inputNode.title
            let normalizedTitle = title.replacingOccurrences(of: " ", with: "")
            
            // We use case-insensitive replacement to be user-friendly
            finalPrompt = finalPrompt.replacingOccurrences(of: "{{\(title)}}", with: processedContent, options: .caseInsensitive)
            finalPrompt = finalPrompt.replacingOccurrences(of: "{{\(normalizedTitle)}}", with: processedContent, options: .caseInsensitive)
        }
        
        // Final fallback: replace any remaining {{...}} tags that weren't found
        // with "[Value not found]" to prevent the AI from echoing tags.
        let regex = try? NSRegularExpression(pattern: "\\{\\{.*?\\}\\}", options: [])
        finalPrompt = regex?.stringByReplacingMatches(in: finalPrompt, options: [], range: NSRange(location: 0, length: finalPrompt.utf16.count), withTemplate: "[Data Missing]") ?? finalPrompt

        // Add a high-priority system constraint to the START of the prompt
        let systemPrompt = "INSTRUCTION: You are a data assistant. Respond in 15 lines or less (prefer around 5-10 lines for most tasks). DO NOT echo variable names; use their actual values.\n\n"
        let fullPrompt = systemPrompt + finalPrompt
        
        // Trigger async AI call
        Task {
            nodes[index].aiResponse = "Thinking..."
            
            do {
                var response = ""
                let stream = LLMService.shared.streamResponse(for: fullPrompt)
                for try await chunk in stream {
                    response += chunk
                    // Throttle updates for UI performance if needed, but for small nodes this is fine
                    nodes[index].aiResponse = response
                }
                
                // Final result
                nodes[index].aiResponse = response
                recalculateGraph() // Trigger ripple if other nodes depend on this result
                requestSave()
            } catch {
                nodes[index].aiResponse = "Error: \(error.localizedDescription)"
            }
        }
    }

    private func findInputs(for nodeId: UUID) -> [SpatialNode] {
        nodes.filter { $0.nextNodeId == nodeId }
    }
}
