import SwiftUI
import UIKit

/// Renders one spatial workspace and owns the transient gesture state needed to
/// pan, zoom, select, and drag nodes without changing the durable project model
/// until a gesture is committed.
struct InfiniteCanvasView: View {
    @Environment(\.colorScheme) var colorScheme
    
    /// Tracks the current panning and zooming state of the canvas.
    @Binding var viewport: ViewportState
    
    /// Real-time scale feedback for external overlays.
    @Binding var currentScale: CGFloat
    
    /// The central store managing node data and persistence.
    var store: ProjectStore
    
    /// Callback triggered when a specialized action node is tapped. Its
    /// presence also marks the canvas as non-persistent onboarding mode.
    var onNodeAction: ((NodeAction) -> Void)? = nil
    
    /// Optional coordinator for guided onboarding steps.
    var onboardingCoordinator: OnboardingCoordinator? = nil
    
    init(store: ProjectStore, viewport: Binding<ViewportState>, currentScale: Binding<CGFloat>, onboardingCoordinator: OnboardingCoordinator? = nil, onNodeAction: ((NodeAction) -> Void)? = nil) {
        self.store = store
        self._viewport = viewport
        self._currentScale = currentScale
        self.onboardingCoordinator = onboardingCoordinator
        self.onNodeAction = onNodeAction
    }
    
    // Drag offsets stay local until the drag ends so links and nodes can track
    // the finger smoothly without writing every intermediate frame to ProjectStore.
    @State private var selectedNode: SpatialNode?
    @State private var nodeDragOffsets: [UUID: CGSize] = [:]
    @State private var isDraggingNode = false
    
    var body: some View {
        GeometryReader { geometry in
            // Node positions are stored as offsets from the visible center, so
            // the center point is the bridge between canvas-space and screen-space.
            let center = CGPoint(x: geometry.size.width / 2, y: geometry.size.height / 2)
            
            ZStack {
                // Layer 1: The Infinite Dotted Grid
                DottedBackground(offset: viewport.offset, scale: viewport.scale)
                
                // Layer 2: Node Connections (Drawn in screen space to prevent clipping and layout bugs)
                ConnectionLayer(nodes: store.nodes, dragOffsets: nodeDragOffsets, viewport: viewport, center: center)
                
                // Layer 3: The Spatial Core (Scaled & Offset)
                ZStack {
                    // Layer 2.5: Spatial Centerpiece (Universal)
                    Color.clear
                        .frame(width: 0, height: 0)
                        .overlay(
                            Image("SpaceSketchBG")
                                .resizable()
                                .scaledToFill()
                                .frame(width: 2000, height: 2000)
                                .opacity(colorScheme == .dark ? 0.40 : 0.25)
                                .blendMode(colorScheme == .dark ? .screen : .multiply)
                                .allowsHitTesting(false)
                        )
                    
                    ForEach(store.nodes) { node in
                        let currentOffset = nodeDragOffsets[node.id] ?? .zero
                        let isDraggingThisNode = nodeDragOffsets[node.id] != nil
                        
                        NodeView(node: node, isDragging: isDraggingThisNode)
                            .offset(
                                x: node.position.x + currentOffset.width,
                                y: node.position.y + currentOffset.height
                            )
                            .onTapGesture {
                                if let action = node.action {
                                    onNodeAction?(action)
                                } else {
                                    selectedNode = node
                                }
                                
                                // ONBOARDING: Check for tapNode gate
                                if let step = onboardingCoordinator?.currentStep, 
                                   step.gate == .tapNode, 
                                   step.spotlightNodeId == node.id {
                                    onboardingCoordinator?.advance()
                                }
                            }
                            .contextMenu {
                                if !node.isProtected {
                                    Button(role: .destructive) {
                                        HapticsManager.shared.notification(.warning)
                                        store.deleteNode(id: node.id, persist: !isOnboardingCanvas)
                                    } label: {
                                        Label("Delete Node", systemImage: "trash")
                                    }
                                }
                                
                                Button {
                                    selectedNode = node
                                } label: {
                                    Label("Inspect", systemImage: "info.circle")
                                }
                            }
                            .highPriorityGesture(
                                DragGesture(minimumDistance: 5)
                                    .onChanged { value in
                                        // The node drag gesture has priority, but the canvas
                                        // pan gesture still observes events; this flag prevents
                                        // both transforms from applying to the same drag.
                                        isDraggingNode = true
                                        nodeDragOffsets[node.id] = value.translation
                                    }
                                    .onEnded { value in
                                        // Finalize the node position with a smooth spring animation.
                                        withAnimation(.spring(response: 0.4, dampingFraction: 0.75)) {
                                            let finalX = node.position.x + value.translation.width
                                            let finalY = node.position.y + value.translation.height
                                            
                                            // Onboarding edits are session-only; project edits
                                            // persist because they are user-authored layout state.
                                            store.updateNodePosition(
                                                id: node.id,
                                                position: CGPoint(x: finalX, y: finalY),
                                                persist: !isOnboardingCanvas
                                            )
                                            
                                            nodeDragOffsets[node.id] = nil
                                            isDraggingNode = false
                                            HapticsManager.shared.selectionChanged()
                                        }
                                    }
                            )
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .scaleEffect(viewport.scale)
                .offset(viewport.offset)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .contentShape(Rectangle()) // Ensure the entire area is gesture-sensitive.
            .gesture(
                TrackpadPanGesture(
                    onChanged: { translation in
                        guard !isDraggingNode else { return }
                        viewport.handleDragTranslation(translation)
                    },
                    onEnded: {
                        guard !isDraggingNode else { return }
                        viewport.handleDragEnded()
                        persistViewportIfNeeded()
                        
                        // ONBOARDING: Check for pan gate
                        if let step = onboardingCoordinator?.currentStep, step.gate == .pan {
                            onboardingCoordinator?.advance()
                        }
                    }
                )
            )
            .simultaneousGesture(
                DragGesture()
                    .onChanged { value in
                        // Only pan the background if no node is currently being dragged.
                        if !isDraggingNode {
                            viewport.handleDragChanged(value)
                        }
                    }
                    .onEnded { _ in 
                        if !isDraggingNode {
                            viewport.handleDragEnded()
                            persistViewportIfNeeded()
                        }
                    }
            )
            .simultaneousGesture(
                MagnifyGesture()
                    .onChanged { value in
                        let location = CGPoint(
                            x: value.startAnchor.x * geometry.size.width,
                            y: value.startAnchor.y * geometry.size.height
                        )
                        viewport.handleMagnificationChanged(value.magnification, at: location, in: geometry.size)
                        currentScale = viewport.scale
                    }
                    .onEnded { _ in 
                        viewport.handleMagnificationEnded()
                        currentScale = viewport.scale
                        persistViewportIfNeeded()
                        
                        // ONBOARDING: Check for zoom gate
                        if let step = onboardingCoordinator?.currentStep, step.gate == .zoom {
                            onboardingCoordinator?.advance()
                        }
                    }
            )
            .environment(\.layoutDirection, .leftToRight)
            .overlay {
                // Layer 4: Onboarding Focus Ring
                if let step = onboardingCoordinator?.currentStep {
                    let spotlightPos: CGPoint = {
                        if let nodeId = step.spotlightNodeId, 
                           let node = store.nodes.first(where: { $0.id == nodeId }) {
                            // Node position is in canvas space (offset from center).
                            // Screen pos = center + viewportOffset + (nodePos * viewportScale)
                            return CGPoint(
                                x: center.x + viewport.offset.width + (node.position.x * viewport.scale),
                                y: center.y + viewport.offset.height + (node.position.y * viewport.scale)
                            )
                        }
                        return center // Fallback to screen center
                    }()
                    
                    FocusRingOverlay(step: step, screenPosition: spotlightPos)
                        .allowsHitTesting(false)
                }
            }
        }
        .background(backgroundColor)
        .edgesIgnoringSafeArea(.all)
        .sheet(item: $selectedNode) { node in
            NodeDetailView(node: node, store: store)
        }
        .onAppear {
            currentScale = viewport.scale
            
            // ONBOARDING: Handle .none gate (auto-advance)
            checkAutoAdvance()
        }
        .onChange(of: onboardingCoordinator?.currentStepIndex) {
            checkAutoAdvance()
        }
    }
    
    private func checkAutoAdvance() {
        if let step = onboardingCoordinator?.currentStep, step.gate == .none {
            Task {
                try? await Task.sleep(for: .seconds(2))
                if onboardingCoordinator?.currentStep?.id == step.id {
                    onboardingCoordinator?.advance()
                }
            }
        }
    }
    
    private var backgroundColor: Color {
        colorScheme == .dark ? Color(white: 0.05) : Color(white: 0.95)
    }

    private func persistViewportIfNeeded() {
        // Persist viewport only for real projects. Onboarding should remain a
        // stable authored path on every run.
        store.updateViewport(
            offset: viewport.offset,
            scale: viewport.scale,
            persist: !isOnboardingCanvas
        )
    }

    private var isOnboardingCanvas: Bool {
        onNodeAction != nil && store.fileName.contains("onboarding")
    }
}

private struct TrackpadPanGesture: UIGestureRecognizerRepresentable {
    var onChanged: (CGSize) -> Void
    var onEnded: () -> Void

    func makeUIGestureRecognizer(context: Context) -> UIPanGestureRecognizer {
        let recognizer = UIPanGestureRecognizer()
        recognizer.allowedScrollTypesMask = .continuous
        recognizer.delegate = context.coordinator
        recognizer.cancelsTouchesInView = false
        return recognizer
    }

    func handleUIGestureRecognizerAction(_ recognizer: UIPanGestureRecognizer, context: Context) {
        let translation = recognizer.translation(in: recognizer.view)
        let canvasTranslation = CGSize(width: translation.x, height: translation.y)

        switch recognizer.state {
        case .began, .changed:
            onChanged(canvasTranslation)
        case .ended, .cancelled, .failed:
            onEnded()
        default:
            break
        }
    }

    func makeCoordinator(converter: CoordinateSpaceConverter) -> Coordinator {
        Coordinator()
    }

    final class Coordinator: NSObject, UIGestureRecognizerDelegate {
        func gestureRecognizer(
            _ gestureRecognizer: UIGestureRecognizer,
            shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer
        ) -> Bool {
            true
        }
    }
}



#Preview {
    InfiniteCanvasView(store: ProjectStore(), viewport: .constant(ViewportState()), currentScale: .constant(1.0), onNodeAction: nil)
}
