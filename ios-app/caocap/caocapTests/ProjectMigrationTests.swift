import XCTest
import CoreGraphics
@testable import caocap

final class ProjectMigrationTests: XCTestCase {
    
    @MainActor
    func testLoadingLegacyFileMigratesToV1() throws {
        // 1. Create a versionless (v0) JSON
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
        let data = legacyJSON.data(using: .utf8)!
        
        let fileName = "test-legacy-\(UUID().uuidString).json"
        let store = ProjectStore(fileName: fileName, initialNodes: [])
        
        // Write legacy data to disk manually
        let paths = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)
        let appSupport = paths[0].appendingPathComponent("com.ficruty.caocap", isDirectory: true)
        let fileURL = appSupport.appendingPathComponent(fileName)
        try data.write(to: fileURL)
        
        // 2. Load it
        store.load()
        
        // 3. Verify migration
        XCTAssertEqual(store.projectName, "Legacy Project")
        XCTAssertEqual(store.nodes.count, 1)
        XCTAssertEqual(store.nodes.first?.action, .retryOnboarding, "Action should be migrated from title")
        
        // Cleanup
        try? FileManager.default.removeItem(at: fileURL)
    }
    
    @MainActor
    func testLoadingCurrentVersionSucceeds() throws {
        let v1JSON = """
        {
            "schemaVersion": 1,
            "projectName": "V1 Project",
            "viewportOffset": {"width": 10, "height": 20},
            "viewportScale": 0.5,
            "nodes": []
        }
        """
        let data = v1JSON.data(using: .utf8)!
        let fileName = "test-v1-\(UUID().uuidString).json"
        let store = ProjectStore(fileName: fileName, initialNodes: [])
        
        let paths = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)
        let appSupport = paths[0].appendingPathComponent("com.ficruty.caocap", isDirectory: true)
        let fileURL = appSupport.appendingPathComponent(fileName)
        try data.write(to: fileURL)
        
        store.load()
        
        XCTAssertEqual(store.projectName, "V1 Project")
        XCTAssertEqual(store.viewportScale, 0.5)
        
        try? FileManager.default.removeItem(at: fileURL)
    }
    
    @MainActor
    func testLoadingNewerVersionAbortsToPreventDataLoss() throws {
        let v99JSON = """
        {
            "schemaVersion": 99,
            "projectName": "Future Project",
            "viewportOffset": {"width": 0, "height": 0},
            "viewportScale": 1.0,
            "nodes": []
        }
        """
        let data = v99JSON.data(using: .utf8)!
        let fileName = "test-future-\(UUID().uuidString).json"
        let store = ProjectStore(fileName: fileName, initialNodes: [])
        
        let paths = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)
        let appSupport = paths[0].appendingPathComponent("com.ficruty.caocap", isDirectory: true)
        let fileURL = appSupport.appendingPathComponent(fileName)
        try data.write(to: fileURL)
        
        store.load()
        
        // Should fallback to defaults (OnboardingProvider.manifestoNodes)
        XCTAssertNotEqual(store.projectName, "Future Project")
        
        try? FileManager.default.removeItem(at: fileURL)
    }
}
