import CoreGraphics
import Foundation
import Testing
@testable import caocap

@MainActor
struct ProjectMutationTests {

    @Test func nodeAdditionRegistersUndo() throws {
        let store = ProjectStore(fileName: "test_add.json")
        let undoManager = UndoManager()
        store.undoManager = undoManager
        
        let initialCount = store.nodes.count
        store.addNode()
        
        #expect(store.nodes.count == initialCount + 1)
        #expect(undoManager.canUndo)
        
        undoManager.undo()
        #expect(store.nodes.count == initialCount)
    }

    @Test func nodeDeletionCleansUpConnections() throws {
        let node1 = SpatialNode(id: UUID(), type: .code, position: .zero, title: "N1")
        let node2Id = UUID()
        let node2 = SpatialNode(id: node2Id, type: .code, position: .zero, title: "N2")
        
        var node3 = SpatialNode(id: UUID(), type: .code, position: .zero, title: "N3")
        node3.nextNodeId = node2Id
        node3.connectedNodeIds = [node2Id]
        
        let store = ProjectStore(fileName: "test_del.json", initialNodes: [node1, node2, node3])
        
        store.deleteNode(id: node2Id)
        
        #expect(store.nodes.count == 2)
        #expect(!store.nodes.contains(where: { $0.id == node2Id }))
        
        let updatedNode3 = store.nodes.first(where: { $0.title == "N3" })!
        #expect(updatedNode3.nextNodeId == nil)
        #expect(updatedNode3.connectedNodeIds == nil)
    }

    @Test func nodeDeletionRegistersUndo() throws {
        let node1 = SpatialNode(id: UUID(), type: .code, position: .zero, title: "N1")
        let store = ProjectStore(fileName: "test_del_undo.json", initialNodes: [node1])
        let undoManager = UndoManager()
        store.undoManager = undoManager
        
        store.deleteNode(id: node1.id)
        #expect(store.nodes.isEmpty)
        #expect(undoManager.canUndo)
        
        undoManager.undo()
        #expect(store.nodes.count == 1)
        #expect(store.nodes.first?.id == node1.id)
    }
}
