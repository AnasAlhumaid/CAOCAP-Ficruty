import SwiftUI

struct ContentView: View {
    var body: some View {
        ZStack {
            InfiniteCanvasView()
            
            FloatingCommandButton()
        }
    }
}

#Preview {
    ContentView()
}
