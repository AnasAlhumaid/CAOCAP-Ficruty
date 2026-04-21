import SwiftUI

struct ContentView: View {
    @StateObject var commandPalette = CommandPaletteViewModel()
    @StateObject var coCaptain = CoCaptainViewModel()
    @State private var projectStore = ProjectStore(fileName: "onboarding_v1.json")
    @State private var isHomeActive = false
    @State private var homeProjectStore = ProjectStore(fileName: "home_v1.json", initialNodes: HomeProvider.homeNodes)
    @State private var showingPurchaseSheet = false
    
    var body: some View {
        ZStack {
            if isHomeActive {
                // The Home Canvas (Main Navigation Hub)
                InfiniteCanvasView(store: homeProjectStore)
            } else {
                InfiniteCanvasView(store: projectStore, onLaunchProject: {
                    withAnimation(.spring()) {
                        isHomeActive = true
                    }
                })
            }
            
            FloatingCommandButton(onTap: {
                commandPalette.setPresented(true)
            })
            
            CommandPaletteView(viewModel: commandPalette)
        }
        .background(Color.black.ignoresSafeArea())
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
        .sheet(isPresented: $showingPurchaseSheet) {
            PurchaseView()
                .presentationDragIndicator(.hidden)
                .presentationBackground(Color(hex: "050505"))
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
            case .proSubscription:
                showingPurchaseSheet = true
            default:
                break
            }
        }
    }
}

#Preview {
    ContentView()
}
