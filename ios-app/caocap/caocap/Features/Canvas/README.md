# Canvas Feature

The Canvas feature is Ficruty's spatial runtime. It renders the infinite workspace, nodes, links, embedded previews, and editor sheets.

## Ownership

- `ProjectStore` owns durable canvas state: nodes, viewport offset, viewport scale, persistence, and live preview compilation.
- `InfiniteCanvasView` owns transient interaction state: active viewport gestures, selected node, node drag offsets, and whether a node is currently being dragged.
- `ViewportState` owns pan and zoom math. Keep gesture calculations here instead of spreading geometry math through views.
- `NodeView` renders one node. It should stay presentational.
- `NodeDetailView` routes a tapped node to the correct sheet-level editor.
- Providers under `Providers/` create static node graphs for home and onboarding.

## Data Flow

1. `ContentView` provides an active `ProjectStore` from `AppRouter`.
2. `InfiniteCanvasView` renders `store.nodes`.
3. Tapping a normal node opens `NodeDetailView`; tapping an action node calls `onNodeAction`.
4. Editors call `ProjectStore` mutation methods such as `updateNodeTextContent`.
5. `ProjectStore` debounces saves and recompiles the WebView payload from HTML, CSS, and JavaScript nodes.
6. `ConnectionLayer` draws arrows from `nextNodeId` and `connectedNodeIds`.

Views should call store methods rather than mutating `store.nodes` directly.

## Coordinate Model

- `SpatialNode.position` is a canvas-space offset from the visible center.
- `ViewportState.offset` and `ViewportState.scale` transform the whole node layer.
- `ConnectionLayer` manually converts node positions into screen-space coordinates so links do not clip during pan and zoom.
- The canvas forces left-to-right layout where spatial math depends on predictable coordinates.

When changing gestures or connection rendering, test pan, zoom, drag, and arrow placement together.

## Onboarding Mode

`InfiniteCanvasView` treats onboarding specially when `onNodeAction` is present and the store filename contains `onboarding`:

- viewport starts fresh instead of loading persisted viewport state;
- node drag and viewport changes do not persist;
- action nodes can drive navigation through the callback.

`OnboardingProvider` loads the authored tutorial graph from `Resources/tutorial.json`, with a Swift fallback for bundle or decode failures. Preserve the non-persistent onboarding distinction unless replacing the guided flow end to end.

## Editing Guidance

- Put reusable node graph construction in `Providers/`, not in `AppRouter` or large views.
- Keep `NodeView` focused on visual rendering. Put editing behavior in sheet views or store methods.
- Keep `NodeDetailView` as a router; avoid adding feature logic there.
- If adding a node type, update `SpatialNode`, `NodeDetailView`, `ProjectContextBuilder`, and any CoCaptain role/patch behavior that should understand it.
- Web preview content should flow through `ProjectStore` compilation instead of being assembled in UI components.

## Verification Checklist

- Create/open a project and confirm nodes render at the expected zoom.
- Drag a node, pan the canvas, pinch zoom, then reopen the project and verify persisted state.
- Edit HTML, CSS, and JavaScript nodes and confirm the Live Preview updates.
- Open the WebView node full-screen and confirm the same compiled payload renders.
- Check connection arrows while dragging nodes and at multiple zoom levels.
- Run onboarding and confirm action nodes navigate without persisting onboarding canvas edits.

## Test Targets

Useful test coverage for this feature:

- `ViewportState` pan and zoom math.
- `ProjectStore` live preview compilation.
- save/load of node positions, links, and viewport state.
- provider output for required onboarding/home action nodes.
