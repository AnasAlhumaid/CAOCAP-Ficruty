import SwiftUI

extension Notification.Name {
    static let openCommandPalette = Notification.Name("openCommandPalette")
    static let summonCoCaptain = Notification.Name("summonCoCaptain")
    static let performUndo = Notification.Name("performUndo")
    static let performRedo = Notification.Name("performRedo")
}

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
    @State private var selectedTheme = "System"
    @State private var isLaunching = true
    @State private var onboardingCoordinator = OnboardingCoordinator()
    @State private var viewport = ViewportState()

    var body: some View {
        ZStack {
            switch router.currentWorkspace {
            case .home:
                InfiniteCanvasView(
                    store: router.homeStore,
                    viewport: $viewport,
                    currentScale: $currentScale,
                    onNodeAction: { action in
                        handleNodeAction(action)
                    }
                )
                .id("home_canvas")
            case .onboarding:
                InfiniteCanvasView(
                    store: router.onboardingStore,
                    viewport: $viewport,
                    currentScale: $currentScale,
                    onboardingCoordinator: onboardingCoordinator,
                    onNodeAction: { action in
                        handleNodeAction(action)
                    }
                )
                .id("onboarding_canvas")
            case .project(let fileName):
                InfiniteCanvasView(
                    store: router.activeStore,
                    viewport: $viewport,
                    currentScale: $currentScale,
                    onNodeAction: { action in
                        handleNodeAction(action)
                    }
                )
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

            onboardingCoordinator.load(steps: OnboardingProvider.steps)

            // Dismiss launch screen after animation
            Task {
                try? await Task.sleep(for: .seconds(2.5))
                withAnimation(.easeInOut(duration: 0.5)) {
                    isLaunching = false
                }
            }
        }
        .onChange(of: onboardingCoordinator.isComplete) { _, isComplete in
            if isComplete {
                handleNodeAction(.navigateHome)
            }
        }
        .onChange(of: router.currentWorkspace) {
            router.activeStore.undoManager = undoManager
            coCaptain.store = router.activeStore
            commandPalette.nodes = router.activeStore.nodes
            
            // Sync viewport with new store
            let isOnboarding = router.currentWorkspace == .onboarding
            viewport = ViewportState(
                offset: isOnboarding ? .zero : router.activeStore.viewportOffset,
                scale: isOnboarding ? 1.0 : router.activeStore.viewportScale
            )
        }
        .onReceive(NotificationCenter.default.publisher(for: .NSUndoManagerDidUndoChange)) { _ in
            router.activeStore.undoStackChanged += 1
        }
        .onReceive(NotificationCenter.default.publisher(for: .NSUndoManagerDidRedoChange)) { _ in
            router.activeStore.undoStackChanged += 1
        }
        .onReceive(NotificationCenter.default.publisher(for: .openCommandPalette)) { _ in
            commandPalette.setPresented(true)
        }
        .onReceive(NotificationCenter.default.publisher(for: .summonCoCaptain)) { _ in
            _ = actionDispatcher.perform(.summonCoCaptain, source: .user)
        }
        .onReceive(NotificationCenter.default.publisher(for: .performUndo)) { _ in
            undoManager?.undo()
            router.activeStore.undoStackChanged += 1
        }
        .onReceive(NotificationCenter.default.publisher(for: .performRedo)) { _ in
            undoManager?.redo()
            router.activeStore.undoStackChanged += 1
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
        case .summonCoCaptain:
            _ = actionDispatcher.perform(.summonCoCaptain, source: .user)
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
            },
            moveNode: { args in
                guard let idString = args["nodeId"], let uuid = UUID(uuidString: idString),
                      let xStr = args["x"], let x = Double(xStr),
                      let yStr = args["y"], let y = Double(yStr) else { return }
                router.activeStore.updateNodePosition(id: uuid, position: CGPoint(x: x, y: y))
            },
            themeNode: { args in
                guard let idString = args["nodeId"], let uuid = UUID(uuidString: idString),
                      let themeStr = args["theme"], let theme = NodeTheme(rawValue: themeStr) else { return }
                router.activeStore.updateNodeTheme(id: uuid, theme: theme)
            },
            transformNode: { args in
                guard let idString = args["nodeId"], let uuid = UUID(uuidString: idString),
                      let typeStr = args["type"], let type = NodeType(rawValue: typeStr) else { return }
                router.activeStore.updateNodeType(id: uuid, type: type)
            }
        )
    }

    private func setupCommandHandlers() {
        commandPalette.actions = actionDispatcher.availableActions
        commandPalette.nodes = router.activeStore.nodes
        commandPalette.onExecute = { actionID in
            _ = actionDispatcher.perform(actionID, source: .user)
        }
        commandPalette.onFlyToNode = { nodeId in
            guard let node = router.activeStore.nodes.first(where: { $0.id == nodeId }) else { return }
            withAnimation(.spring(response: 0.6, dampingFraction: 0.85)) {
                // GeometryReader size isn't easily available here, but flyTo math
                // uses containerSize to find center. Since node.position is relative 
                // to center already, we can pass .zero and it works.
                viewport.flyTo(nodePosition: node.position, containerSize: .zero)
            }
        }
        commandPalette.onSubmitPrompt = { prompt in
            coCaptain.store = router.activeStore
            coCaptain.actionDispatcher = actionDispatcher
            coCaptain.setPresented(true)
            coCaptain.sendMessage(prompt)
        }
    }
}

#Preview {
    ContentView()
}
