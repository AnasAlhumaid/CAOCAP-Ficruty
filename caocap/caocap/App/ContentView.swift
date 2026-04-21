import SwiftUI

struct ContentView: View {
    @StateObject var commandPalette = CommandPaletteViewModel()
    @StateObject var coCaptain = CoCaptainViewModel()
    @State private var projectStore = ProjectStore()
    
    var body: some View {
        ZStack {
            InfiniteCanvasView(store: projectStore)
            
            FloatingCommandButton(onTap: {
                commandPalette.setPresented(true)
            })
            
            CommandPaletteView(viewModel: commandPalette)
        }
        .sheet(isPresented: $coCaptain.isPresented) {
            CoCaptainView(viewModel: coCaptain)
                .presentationDetents([.medium, .large])
                .presentationDragIndicator(.visible)
                .presentationBackground {
                    Color.white.opacity(0.4)
                        .background(.ultraThinMaterial)
                }
                .presentationBackgroundInteraction(.enabled)
        }
        .onAppear {
            setupCommandHandlers()
        }
    }
    
    private func setupCommandHandlers() {
        commandPalette.onExecute = { command in
            switch command {
            case .summonCoCaptain:
                coCaptain.setPresented(true)
            default:
                break
            }
        }
    }
}

#Preview {
    ContentView()
}
