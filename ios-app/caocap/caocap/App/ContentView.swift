import SwiftUI

struct ContentView: View {
    @State var commandPalette = CommandPaletteViewModel()
    @State var coCaptain = CoCaptainViewModel()
    @State private var actionDispatcher = AppActionDispatcher()
    @State private var router = AppRouter()
    @State private var showingPurchaseSheet = false
    @State private var showingSignIn = false
    @State private var showingSettings = false
    @State private var showingProfile = false
    @State private var showingProjectExplorer = false
    @State private var currentScale: CGFloat = 1.0
    @Environment(\.undoManager) var undoManager
    @Environment(\.colorScheme) var colorScheme
    @AppStorage("app_theme") private var selectedTheme = "System"
    @State private var isLaunching = true

    var body: some View {
        ZStack {
            switch router.currentWorkspace {
            case .home:
                InfiniteCanvasView(store: router.homeStore, currentScale: $currentScale, onNodeAction: { action in
                    handleNodeAction(action)
                })
                .id("home_canvas")
            case .onboarding:
                InfiniteCanvasView(store: router.onboardingStore, currentScale: $currentScale, onNodeAction: { action in
                    handleNodeAction(action)
                })
                .id("onboarding_canvas")
            case .project(let fileName):
                InfiniteCanvasView(store: router.activeStore, currentScale: $currentScale, onNodeAction: { action in
                    handleNodeAction(action)
                })
                .id("project_canvas_\(fileName)")
            }

            CanvasHUDView(
                store: router.activeStore,
                viewportScale: currentScale,
                onSignInTapped: { showingSignIn = true }
            )

            FloatingCommandButton(
                onTap: {
                    commandPalette.setPresented(true)
                },
                onUndo: {
                    undoManager?.undo()
                    router.activeStore.undoStackChanged += 1
                },
                onSummonCoCaptain: {
                    _ = actionDispatcher.perform(.summonCoCaptain, source: .user)
                },
                onRedo: {
                    undoManager?.redo()
                    router.activeStore.undoStackChanged += 1
                },
                canUndo: (router.activeStore.undoStackChanged >= 0) && (undoManager?.canUndo ?? false),
                canRedo: (router.activeStore.undoStackChanged >= 0) && (undoManager?.canRedo ?? false)
            )
            .environment(\.layoutDirection, .leftToRight)

            CommandPaletteView(viewModel: commandPalette)
        }
        .background(Color.black.ignoresSafeArea())
        .overlay {
            if isLaunching {
                LaunchScreenView()
                    .transition(.opacity)
                    .zIndex(100)
            }
        }
        .preferredColorScheme(currentColorScheme)
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
        .sheet(isPresented: $showingSignIn) {
            SignInView()
                .presentationDetents([.large])
                .presentationDragIndicator(.visible)
                .presentationBackground {
                    Color.black.opacity(0.95)
                        .background(.ultraThinMaterial)
                }
        }
        .sheet(isPresented: $showingProjectExplorer) {
            ProjectExplorerView(onSelect: { fileName in
                router.navigate(to: .project(fileName))
            })
        }
        .sheet(isPresented: $showingPurchaseSheet) {
            PurchaseView()
                .presentationDragIndicator(.hidden)
                .presentationBackground(Color(hex: "050505"))
        }
        .sheet(isPresented: $showingSettings) {
            SettingsView()
                .presentationDetents([.large])
                .presentationDragIndicator(.visible)
        }
        .sheet(isPresented: $showingProfile) {
            ProfileView(onSignIn: {
                showingSignIn = true
            }, onPro: {
                showingPurchaseSheet = true
            })
                .presentationDetents([.large])
                .presentationDragIndicator(.visible)
        }
        .onAppear {
            configureActionDispatcher()
            setupCommandHandlers()

            currentScale = router.activeStore.viewportScale
            router.activeStore.undoManager = undoManager
            router.homeStore.undoManager = undoManager
            router.onboardingStore.undoManager = undoManager

            coCaptain.store = router.activeStore
            coCaptain.actionDispatcher = actionDispatcher

            // Dismiss launch screen after animation
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) {
                withAnimation(.easeInOut(duration: 0.5)) {
                    isLaunching = false
                }
            }
        }
        .onChange(of: router.currentWorkspace) {
            router.activeStore.undoManager = undoManager
            coCaptain.store = router.activeStore
        }
    }
    
    private var currentColorScheme: ColorScheme? {
        switch selectedTheme {
        case "Light": return .light
        case "Dark": return .dark
        default: return nil
        }
    }

    private func handleNodeAction(_ action: NodeAction) {
        switch action {
        case .navigateHome:
            router.navigate(to: .home, animated: true)
            currentScale = 1.0
        case .retryOnboarding:
            router.navigate(to: .onboarding, animated: true)
            currentScale = 1.0
        case .createNewProject:
            router.createNewProject()
        case .openSettings:
            _ = actionDispatcher.perform(.openSettings, source: .user)
        case .openProfile:
            _ = actionDispatcher.perform(.openProfile, source: .user)
        case .openProjectExplorer:
            _ = actionDispatcher.perform(.openProjectExplorer, source: .user)
        case .resumeLastProject:
            router.resumeLastProject()
        }
    }

    private func configureActionDispatcher() {
        actionDispatcher.configure(
            goHome: {
                router.goHome()
                currentScale = 1.0
            },
            goBack: {
                router.goBack()
            },
            newProject: {
                router.createNewProject()
            },
            createNode: {
                router.activeStore.addNode()
            },
            summonCoCaptain: {
                coCaptain.store = router.activeStore
                coCaptain.setPresented(true)
            },
            proSubscription: {
                showingPurchaseSheet = true
            },
            signIn: {
                showingSignIn = true
            },
            openSettings: {
                showingSettings = true
            },
            openProfile: {
                showingProfile = true
            },
            openProjectExplorer: {
                showingProjectExplorer = true
            }
        )
    }

    private func setupCommandHandlers() {
        commandPalette.actions = actionDispatcher.availableActions
        commandPalette.onExecute = { actionID in
            _ = actionDispatcher.perform(actionID, source: .user)
        }
    }
}

#Preview {
    ContentView()
}
