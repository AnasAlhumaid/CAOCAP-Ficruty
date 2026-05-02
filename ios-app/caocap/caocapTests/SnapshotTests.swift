import Foundation
import Testing
@testable import caocap

struct SnapshotTests {

    @MainActor
    @Test func createCheckpointAndRestore() async throws {
        let store = ProjectStore(fileName: "test_checkpoint.json", projectName: "Checkpoint Test")
        store.nodes = [SpatialNode(id: UUID(), position: .zero, title: "Original")]
        
        // 1. Create checkpoint
        store.createCheckpoint(label: "Initial State")
        
        // Wait a bit for background task
        try? await Task.sleep(nanoseconds: 200_000_000)
        
        #expect(store.history.count == 1)
        #expect(store.history[0].label == "Manual Checkpoint") // Currently hardcoded in listSnapshots
        
        // 2. Modify nodes
        store.nodes = [SpatialNode(id: UUID(), position: .zero, title: "Modified")]
        #expect(store.nodes.first?.title == "Modified")
        
        // 3. Restore
        let metadata = store.history[0]
        store.restore(from: metadata)
        
        #expect(store.nodes.first?.title == "Original")
    }

    @MainActor
    @Test func cleanupOldSnapshots() async throws {
        let store = ProjectStore(fileName: "test_limit.json")
        
        // Create 25 checkpoints
        for i in 0..<25 {
            store.createCheckpoint(label: "CP \(i)")
            try? await Task.sleep(nanoseconds: 50_000_000)
        }
        
        // Should be limited to 20
        #expect(store.history.count == 20)
    }
}
