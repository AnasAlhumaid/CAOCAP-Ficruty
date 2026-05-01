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
    
    private let logger = Logger(subsystem: "com.ficruty.caocap", category: "Persistence")
    
    /// A reference to the pending save task used for debouncing disk writes.
    private var saveTask: Task<Void, Never>? = nil
    
    /// The current version of the project file schema. Incremented when
    /// structural changes are made to nodes or the project envelope.
    public static let currentSchemaVersion: Int = 1

    /// The internal structure used for JSON serialization of the project state.
    private struct ProjectData: Codable {
        let schemaVersion: Int
        let projectName: String?
        let nodes: [SpatialNode]
        let viewportOffset: CGSize
        let viewportScale: CGFloat
    }

    /// A minimal representation used to check the version before full decoding.
    private struct VersionCheck: Codable {
        let schemaVersion: Int?
    }
    
    /// Returns the local file URL where project data is stored.
    /// This property also ensures the parent directory exists.
    public let fileName: String
    
    private var fileURL: URL {
        let paths = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)
        let appSupport = paths[0].appendingPathComponent("com.ficruty.caocap", isDirectory: true)
        
        // Create the directory if it doesn't exist (e.g., on first run)
        try? FileManager.default.createDirectory(at: appSupport, withIntermediateDirectories: true)
        
        return appSupport.appendingPathComponent(self.fileName)
    }
    
    public init(fileName: String = "project_v1.json", projectName: String = "Untitled Project", initialNodes: [SpatialNode]? = nil, initialViewportScale: CGFloat = 1.0) {
        self.fileName = fileName
        self.projectName = projectName
        self.viewportScale = initialViewportScale
        load(initialNodes: initialNodes, initialViewportScale: initialViewportScale)
    }
    
    /// Loads the project data from disk. If no file is found, initializes with default nodes.
    public func load(initialNodes: [SpatialNode]? = nil, initialViewportScale: CGFloat = 1.0) {
        let url = fileURL
        
        if !FileManager.default.fileExists(atPath: url.path) {
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
            let data = try Data(contentsOf: url)
            let decoder = JSONDecoder()
            
            // 1. Check the version
            let versionCheck = try? decoder.decode(VersionCheck.self, from: data)
            let sourceVersion = versionCheck?.schemaVersion ?? 0
            
            if sourceVersion > Self.currentSchemaVersion {
                logger.error("Project version \(sourceVersion) is newer than app version \(Self.currentSchemaVersion). Aborting load to prevent data loss.")
                // Fallback to defaults to prevent a crash, but log heavily.
                self.nodes = initialNodes ?? OnboardingProvider.manifestoNodes
                return
            }
            
            // 2. Decode the data
            let decoded: ProjectData
            if sourceVersion == 0 {
                // Decode legacy format (no schemaVersion)
                struct LegacyData: Codable {
                    let projectName: String?
                    let nodes: [SpatialNode]
                    let viewportOffset: CGSize
                    let viewportScale: CGFloat
                }
                let legacy = try decoder.decode(LegacyData.self, from: data)
                decoded = ProjectData(
                    schemaVersion: 0,
                    projectName: legacy.projectName,
                    nodes: legacy.nodes,
                    viewportOffset: legacy.viewportOffset,
                    viewportScale: legacy.viewportScale
                )
            } else {
                decoded = try decoder.decode(ProjectData.self, from: data)
            }
            
            // 3. Apply migrations
            var migratedNodes = decoded.nodes
            if sourceVersion < 1 {
                migratedNodes = migrateV0ToV1(nodes: migratedNodes)
            }
            
            // Update the live state with the decoded data
            self.projectName = decoded.projectName ?? self.projectName
            self.nodes = migratedNodes
            self.viewportOffset = decoded.viewportOffset
            self.viewportScale = decoded.viewportScale
            
            logger.info("Successfully loaded project (v\(sourceVersion)) from disk.")
            
            // If we migrated, schedule a save to modernize the file
            if sourceVersion < Self.currentSchemaVersion {
                save()
            }
        } catch {
            logger.error("Failed to load project: \(error.localizedDescription)")
            // Fallback to initial nodes if data is corrupted or missing
            self.nodes = initialNodes ?? OnboardingProvider.manifestoNodes
        }
        
        // Ensure the Live Preview is synced with the code nodes on startup
        compileLivePreview()
    }

    /// Migration from Version 0 (Legacy/Versionless) to Version 1.
    /// Handles the ad-hoc 'action' property assignment for canonical nodes.
    private func migrateV0ToV1(nodes: [SpatialNode]) -> [SpatialNode] {
        var migrated = nodes
        for i in 0..<migrated.count {
            if migrated[i].action == nil {
                switch migrated[i].title {
                case "Retry Onboarding": migrated[i].action = .retryOnboarding
                case "Go to the Home workspace": migrated[i].action = .navigateHome
                case "New Project": migrated[i].action = .createNewProject
                case "Settings": migrated[i].action = .openSettings
                case "Profile": migrated[i].action = .openProfile
                case "Projects": migrated[i].action = .openProjectExplorer
                case "Ask CoCaptain": migrated[i].action = .summonCoCaptain
                default: break
                }
            }
        }
        return migrated
    }
    
    /// Persists a snapshot of the current project state using a temporary file
    /// and atomic replacement so interrupted writes do not corrupt the main file.
    public func save() {
        let url = fileURL
        let tempURL = url.appendingPathExtension("\(UUID().uuidString).tmp")
        
        let projectData = ProjectData(
            schemaVersion: Self.currentSchemaVersion,
            projectName: projectName,
            nodes: nodes,
            viewportOffset: viewportOffset,
            viewportScale: viewportScale
        )
        
        let log = logger
        
        Task.detached(priority: .background) {
            do {
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
                
                log.info("Successfully saved project to disk.")
            } catch {
                log.error("Failed to save project: \(error.localizedDescription)")
            }
            
            // Clean up the temp file if the atomic swap failed or it wasn't consumed
            try? FileManager.default.removeItem(at: tempURL)
        }
        
        // Reset isSaving only if no other task is pending
        if saveTask == nil {
            isSaving = false
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
    
    /// Combines HTML, CSS, and JS node contents and updates the Live Preview node.
    /// The compiler intentionally keys off canonical node titles used by the
    /// project template, so template changes should keep those roles stable.
    private func compileLivePreview() {
        guard let webViewIndex = nodes.firstIndex(where: { $0.type == .webView }),
              let htmlNode = nodes.first(where: { $0.title.lowercased() == "html" }) else {
            return
        }
        
        var compiledHTML = htmlNode.textContent ?? ""
        
        // Inject CSS
        if let cssNode = nodes.first(where: { $0.title.lowercased() == "css" }),
           let cssContent = cssNode.textContent, !cssContent.isEmpty {
            let styleTag = "\n<style>\n\(cssContent)\n</style>\n"
            if let headRange = compiledHTML.range(of: "</head>", options: .caseInsensitive) {
                compiledHTML.insert(contentsOf: styleTag, at: headRange.lowerBound)
            } else if let htmlRange = compiledHTML.range(of: "<html>", options: .caseInsensitive) {
                compiledHTML.insert(contentsOf: "<head>\n\(styleTag)\n</head>\n", at: htmlRange.upperBound)
            } else {
                compiledHTML = styleTag + compiledHTML
            }
        }
        
        // Inject JS
        if let jsNode = nodes.first(where: { $0.title.lowercased() == "javascript" }),
           let jsContent = jsNode.textContent, !jsContent.isEmpty {
            let scriptTag = "\n<script>\n\(jsContent)\n</script>\n"
            if let bodyRange = compiledHTML.range(of: "</body>", options: .caseInsensitive) {
                compiledHTML.insert(contentsOf: scriptTag, at: bodyRange.lowerBound)
            } else if let htmlRange = compiledHTML.range(of: "</html>", options: .caseInsensitive) {
                compiledHTML.insert(contentsOf: scriptTag, at: htmlRange.lowerBound)
            } else {
                compiledHTML += scriptTag
            }
        }
        
        // Update the WebView node if the content changed
        if nodes[webViewIndex].htmlContent != compiledHTML {
            nodes[webViewIndex].htmlContent = compiledHTML
        }
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
        
        withAnimation(.spring()) {
            nodes.append(newNode)
        }
        requestSave()
    }
}
