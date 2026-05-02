import SwiftUI
import PencilKit

struct ArtEditorView: View {
    let node: SpatialNode
    let store: ProjectStore
    @Environment(\.dismiss) var dismiss
    
    @State private var canvasView = PKCanvasView()
    @State private var toolPicker = PKToolPicker()
    
    var body: some View {
        VStack(spacing: 0) {
            // Header
            HStack {
                HStack(spacing: 8) {
                    Image(systemName: "paintbrush.fill")
                        .foregroundColor(node.theme.color)
                    Text(node.displayTitle)
                        .font(.system(size: 16, weight: .bold, design: .rounded))
                }
                .padding(.leading, 20)
                
                Spacer()
                
                Button(action: saveAndDismiss) {
                    Text("Done")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .background(node.theme.color)
                        .clipShape(Capsule())
                }
                .padding(.trailing, 20)
            }
            .frame(height: 64)
            .background(Color(uiColor: .systemBackground))
            
            Divider()
            
            // Canvas
            CanvasView(canvasView: $canvasView, toolPicker: toolPicker, drawingData: node.drawingData)
                .edgesIgnoringSafeArea(.bottom)
        }
    }
    
    private func saveAndDismiss() {
        let data = canvasView.drawing.dataRepresentation()
        store.updateNodeDrawingData(id: node.id, data: data)
        dismiss()
    }
}

struct CanvasView: UIViewRepresentable {
    @Binding var canvasView: PKCanvasView
    let toolPicker: PKToolPicker
    let drawingData: Data?
    
    func makeUIView(context: Context) -> PKCanvasView {
        canvasView.drawingPolicy = .anyInput
        
        if let data = drawingData, let drawing = try? PKDrawing(data: data) {
            canvasView.drawing = drawing
        }
        
        toolPicker.addObserver(canvasView)
        toolPicker.setVisible(true, forFirstResponder: canvasView)
        canvasView.becomeFirstResponder()
        
        return canvasView
    }
    
    func updateUIView(_ uiView: PKCanvasView, context: Context) {}
}
