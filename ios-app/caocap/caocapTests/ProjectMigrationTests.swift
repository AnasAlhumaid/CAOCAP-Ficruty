import CoreGraphics
import Foundation
import Testing
@testable import caocap

struct ProjectMigrationTests {

    @MainActor
    @Test func loadingLegacyFileMigratesToV1() throws {
        let tempDirectory = try makeTemporaryDirectory()
        defer { try? FileManager.default.removeItem(at: tempDirectory) }
        let persistence = ProjectPersistenceService(baseDirectory: tempDirectory)
        let fileName = "legacy.json"
        let legacyJSON = """
        {
            "projectName": "Legacy Project",
            "viewportOffset": {"width": 0, "height": 0},
            "viewportScale": 1.0,
            "nodes": [
                {
                    "id": "A1B2C3D4-E5F6-4A5B-8C9D-0E1F2A3B4C5D",
                    "type": "srs",
                    "position": {"x": 0, "y": 0},
                    "title": "Retry Onboarding",
                    "theme": "purple",
                    "textContent": "Sample"
                }
            ]
        }
        """

        try legacyJSON.data(using: .utf8)!.write(to: persistence.fileURL(for: fileName))

        let result = try persistence.load(fileName: fileName)

        #expect(result.sourceSchemaVersion == 0)
        #expect(result.didMigrate)
        #expect(result.snapshot.projectName == "Legacy Project")
        #expect(result.snapshot.nodes.count == 1)
        #expect(result.snapshot.nodes.first?.action == .retryOnboarding, "Action should be migrated from title")
    }

    @MainActor
    @Test func loadingCurrentVersionSucceeds() throws {
        let tempDirectory = try makeTemporaryDirectory()
        defer { try? FileManager.default.removeItem(at: tempDirectory) }
        let persistence = ProjectPersistenceService(baseDirectory: tempDirectory)
        let fileName = "v1.json"
        let v1JSON = """
        {
            "schemaVersion": 1,
            "projectName": "V1 Project",
            "viewportOffset": {"width": 10, "height": 20},
            "viewportScale": 0.5,
            "nodes": []
        }
        """

        try v1JSON.data(using: .utf8)!.write(to: persistence.fileURL(for: fileName))

        let result = try persistence.load(fileName: fileName)

        #expect(result.sourceSchemaVersion == 1)
        #expect(!result.didMigrate)
        #expect(result.snapshot.projectName == "V1 Project")
        #expect(result.snapshot.viewportScale == 0.5)
    }

    @MainActor
    @Test func loadingNewerVersionAbortsToPreventDataLoss() throws {
        let tempDirectory = try makeTemporaryDirectory()
        defer { try? FileManager.default.removeItem(at: tempDirectory) }
        let persistence = ProjectPersistenceService(baseDirectory: tempDirectory)
        let fileName = "future.json"
        let v99JSON = """
        {
            "schemaVersion": 99,
            "projectName": "Future Project",
            "viewportOffset": {"width": 0, "height": 0},
            "viewportScale": 1.0,
            "nodes": []
        }
        """

        try v99JSON.data(using: .utf8)!.write(to: persistence.fileURL(for: fileName))

        #expect(throws: ProjectPersistenceError.self) {
            try persistence.load(fileName: fileName)
        }
    }

    @MainActor
    @Test func storeFallsBackToInitialNodesWhenProjectFileIsCorrupted() throws {
        let tempDirectory = try makeTemporaryDirectory()
        defer { try? FileManager.default.removeItem(at: tempDirectory) }
        let persistence = ProjectPersistenceService(baseDirectory: tempDirectory)
        let fileName = "corrupted.json"
        try Data("{not-json}".utf8).write(to: persistence.fileURL(for: fileName))

        let fallbackNode = SpatialNode(type: .code, position: .zero, title: "HTML", textContent: "<h1>Fallback</h1>")
        let store = ProjectStore(
            fileName: fileName,
            projectName: "Fallback Project",
            initialNodes: [fallbackNode],
            persistence: persistence
        )

        #expect(store.nodes == [fallbackNode])
        #expect(store.projectName == "Fallback Project")
    }

    @Test func persistenceSaveLoadRoundTrip() throws {
        let tempDirectory = try makeTemporaryDirectory()
        defer { try? FileManager.default.removeItem(at: tempDirectory) }
        let persistence = ProjectPersistenceService(baseDirectory: tempDirectory)
        let fileName = "roundtrip.json"
        let snapshot = ProjectSnapshot(
            projectName: "Round Trip",
            nodes: [
                SpatialNode(type: .code, position: CGPoint(x: 12, y: 24), title: "HTML", textContent: "<h1>Hello</h1>")
            ],
            viewportOffset: CGSize(width: 10, height: 20),
            viewportScale: 0.75
        )

        try persistence.save(snapshot, fileName: fileName)
        let loaded = try persistence.load(fileName: fileName)

        #expect(loaded.snapshot == snapshot)
        #expect(loaded.sourceSchemaVersion == ProjectPersistenceService.currentSchemaVersion)
    }

    private func makeTemporaryDirectory() throws -> URL {
        let url = FileManager.default.temporaryDirectory
            .appendingPathComponent("ficruty-tests-\(UUID().uuidString)", isDirectory: true)
        try FileManager.default.createDirectory(at: url, withIntermediateDirectories: true)
        return url
    }
}
