import XCTest
import CoreGraphics
@testable import caocap

final class ProjectMigrationTests: XCTestCase {
    
    @MainActor
    func testLoadingLegacyFileMigratesToV1() throws {
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

        XCTAssertEqual(result.sourceSchemaVersion, 0)
        XCTAssertTrue(result.didMigrate)
        XCTAssertEqual(result.snapshot.projectName, "Legacy Project")
        XCTAssertEqual(result.snapshot.nodes.count, 1)
        XCTAssertEqual(result.snapshot.nodes.first?.action, .retryOnboarding, "Action should be migrated from title")
    }
    
    @MainActor
    func testLoadingCurrentVersionSucceeds() throws {
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

        XCTAssertEqual(result.sourceSchemaVersion, 1)
        XCTAssertFalse(result.didMigrate)
        XCTAssertEqual(result.snapshot.projectName, "V1 Project")
        XCTAssertEqual(result.snapshot.viewportScale, 0.5)
    }
    
    @MainActor
    func testLoadingNewerVersionAbortsToPreventDataLoss() throws {
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

        XCTAssertThrowsError(try persistence.load(fileName: fileName)) { error in
            XCTAssertEqual(
                error as? ProjectPersistenceError,
                .unsupportedFutureVersion(99, current: ProjectPersistenceService.currentSchemaVersion)
            )
        }
    }

    @MainActor
    func testStoreFallsBackToInitialNodesWhenProjectFileIsCorrupted() throws {
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

        XCTAssertEqual(store.nodes, [fallbackNode])
        XCTAssertEqual(store.projectName, "Fallback Project")
    }

    func testPersistenceSaveLoadRoundTrip() throws {
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

        XCTAssertEqual(loaded.snapshot, snapshot)
        XCTAssertEqual(loaded.sourceSchemaVersion, ProjectPersistenceService.currentSchemaVersion)
    }

    private func makeTemporaryDirectory() throws -> URL {
        let url = FileManager.default.temporaryDirectory
            .appendingPathComponent("ficruty-tests-\(UUID().uuidString)", isDirectory: true)
        try FileManager.default.createDirectory(at: url, withIntermediateDirectories: true)
        return url
    }
}
