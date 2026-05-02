import Foundation

public struct ProjectSnapshot: Codable, Equatable {
    public let schemaVersion: Int
    public let projectName: String?
    public let nodes: [SpatialNode]
    public let viewportOffset: CGSize
    public let viewportScale: CGFloat

    public init(
        schemaVersion: Int = ProjectPersistenceService.currentSchemaVersion,
        projectName: String?,
        nodes: [SpatialNode],
        viewportOffset: CGSize,
        viewportScale: CGFloat
    ) {
        self.schemaVersion = schemaVersion
        self.projectName = projectName
        self.nodes = nodes
        self.viewportOffset = viewportOffset
        self.viewportScale = viewportScale
    }
}

public struct ProjectLoadResult: Equatable {
    public let snapshot: ProjectSnapshot
    public let sourceSchemaVersion: Int

    public var didMigrate: Bool {
        sourceSchemaVersion < ProjectPersistenceService.currentSchemaVersion
    }
}

public enum ProjectPersistenceError: LocalizedError, Equatable {
    case unsupportedFutureVersion(Int, current: Int)

    public var errorDescription: String? {
        switch self {
        case .unsupportedFutureVersion(let version, let current):
            return "Project version \(version) is newer than app version \(current)."
        }
    }
}

/// Encapsulates project file layout, schema decoding, migrations, and atomic
/// JSON writes so ProjectStore can stay focused on observable project state.
public struct ProjectPersistenceService: Sendable {
    public static let currentSchemaVersion = 1

    private struct VersionCheck: Codable {
        let schemaVersion: Int?
    }

    private struct LegacySnapshot: Codable {
        let projectName: String?
        let nodes: [SpatialNode]
        let viewportOffset: CGSize
        let viewportScale: CGFloat
    }

    private let baseDirectory: URL?

    public init(baseDirectory: URL? = nil) {
        self.baseDirectory = baseDirectory
    }

    public func fileURL(for fileName: String) -> URL {
        projectDirectory().appendingPathComponent(fileName)
    }

    public func projectExists(fileName: String) -> Bool {
        FileManager.default.fileExists(atPath: fileURL(for: fileName).path)
    }

    public func load(fileName: String) throws -> ProjectLoadResult {
        let data = try Data(contentsOf: fileURL(for: fileName))
        let decoder = JSONDecoder()
        let versionCheck = try? decoder.decode(VersionCheck.self, from: data)
        let sourceVersion = versionCheck?.schemaVersion ?? 0

        guard sourceVersion <= Self.currentSchemaVersion else {
            throw ProjectPersistenceError.unsupportedFutureVersion(
                sourceVersion,
                current: Self.currentSchemaVersion
            )
        }

        let decoded: ProjectSnapshot
        if sourceVersion == 0 {
            let legacy = try decoder.decode(LegacySnapshot.self, from: data)
            decoded = ProjectSnapshot(
                schemaVersion: 0,
                projectName: legacy.projectName,
                nodes: legacy.nodes,
                viewportOffset: legacy.viewportOffset,
                viewportScale: legacy.viewportScale
            )
        } else {
            decoded = try decoder.decode(ProjectSnapshot.self, from: data)
        }

        let migrated = ProjectSnapshot(
            schemaVersion: Self.currentSchemaVersion,
            projectName: decoded.projectName,
            nodes: sourceVersion < 1 ? migrateV0ToV1(nodes: decoded.nodes) : decoded.nodes,
            viewportOffset: decoded.viewportOffset,
            viewportScale: decoded.viewportScale
        )

        return ProjectLoadResult(snapshot: migrated, sourceSchemaVersion: sourceVersion)
    }

    public func save(_ snapshot: ProjectSnapshot, fileName: String) throws {
        let url = fileURL(for: fileName)
        let tempURL = url.appendingPathExtension("\(UUID().uuidString).tmp")
        let encoder = JSONEncoder()
        encoder.outputFormatting = .prettyPrinted
        let data = try encoder.encode(snapshot)

        try data.write(to: tempURL, options: .atomic)

        if FileManager.default.fileExists(atPath: url.path) {
            _ = try FileManager.default.replaceItemAt(url, withItemAt: tempURL)
        } else {
            try FileManager.default.moveItem(at: tempURL, to: url)
        }

        try? FileManager.default.removeItem(at: tempURL)
    }

    private func projectDirectory() -> URL {
        if let baseDirectory {
            try? FileManager.default.createDirectory(
                at: baseDirectory,
                withIntermediateDirectories: true
            )
            return baseDirectory
        }

        let paths = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)
        let appSupport = paths[0].appendingPathComponent("com.ficruty.caocap", isDirectory: true)
        try? FileManager.default.createDirectory(at: appSupport, withIntermediateDirectories: true)
        return appSupport
    }

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
}

public actor ProjectPersistenceWriter {
    private let persistence: ProjectPersistenceService

    public init(persistence: ProjectPersistenceService = ProjectPersistenceService()) {
        self.persistence = persistence
    }

    public func save(_ snapshot: ProjectSnapshot, fileName: String) throws {
        try persistence.save(snapshot, fileName: fileName)
    }
}
