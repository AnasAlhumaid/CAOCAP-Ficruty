import SwiftUI

struct ContentView: View {
    @StateObject var commandPalette = CommandPaletteViewModel()
    
    var body: some View {
        ZStack {
            InfiniteCanvasView()
            
            FloatingCommandButton(onTap: {
                commandPalette.setPresented(true)
            })
            
            CommandPaletteView(viewModel: commandPalette)
        }
        .onAppear {
            // Global keyboard shortcut logic
        }
    }
}

#Preview {
    ContentView()
}
