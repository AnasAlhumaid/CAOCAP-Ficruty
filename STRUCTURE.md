# Ficruty — Codebase Architecture

This document is the authoritative map of the Ficruty (caocap) codebase. We use a **domain-driven, feature-based structure** to maximize isolation, scalability, and developer clarity. Every directory has a single responsibility.

> [!NOTE]
> If you add a new file that changes the architecture, update this document in the same commit.

---

## Repository Root

```
Ficruty/
├── caocap/               # Xcode project and all Swift source files
├── README.md             # Project overview, mission, and devlog
├── ROADMAP.md            # Strategic milestone tracker
├── STRUCTURE.md          # This document — the architectural map
├── CONTRIBUTING.md       # Contribution standards and git workflow
└── LICENSE               # GNU GPL v3.0
```

---

## Source Tree (`caocap/caocap/`)

```
caocap/
├── App/
├── Navigation/
├── Models/
├── Services/
├── Extensions/
├── Features/
│   ├── Canvas/
│   │   ├── Components/
│   │   └── Providers/
│   ├── Omnibox/
│   ├── CoCaptain/
│   ├── Overlays/
│   └── Subscription/
├── Resources/
└── Preview Content/
```

---

## Directory Reference

### `App/`
The application shell and lifecycle management. The thinnest layer possible — no business logic lives here.

| File | Responsibility |
|---|---|
| `caocapApp.swift` | `@main` entry point. Injects `AppRouter` as an environment object. |
| `ContentView.swift` | Root view. Observes `AppRouter` and switches between Onboarding, Home, and Project workspaces. |
| `Info.plist` | System-level permissions and metadata. |

---

### `Navigation/`
Centralized, type-safe routing. All workspace transitions flow through here — nothing navigates by string.

| File | Responsibility |
|---|---|
| `AppRouter.swift` | `@Observable` class managing `WorkspaceState` (`.onboarding`, `.home`, `.project`). Owns all `ProjectStore` instances and `createNewProject()` initialization logic. |

---

### `Models/`
Pure domain data. No UI, no persistence, no side effects. These structs define the *language* of the entire app.

| File | Responsibility |
|---|---|
| `SpatialNode.swift` | The core canvas primitive. Holds `id`, `type` (`.standard`, `.webView`, `.srs`, `.code`), `position`, `textContent`, `htmlContent`, `connectedNodeIds`, and `theme`. |
| `NodeTheme.swift` | Color tokens for the six node themes (blue, purple, green, orange, red, gray). |

---

### `Services/`
Infrastructure and heavy-lifting. These are long-lived objects that outlive individual views.

| File | Responsibility |
|---|---|
| `ProjectStore.swift` | The core persistence engine. Manages `[SpatialNode]` state, atomic JSON writes, debounced `requestSave()`, viewport persistence, and the **Live Compilation Engine** (`compileLivePreview()`). |
| `SubscriptionManager.swift` | StoreKit 2 integration. Manages Pro subscription state, purchase flow, and transaction verification. |

---

### `Extensions/`
Lightweight, reusable Swift and framework extensions. No dependencies on app-specific logic.

| File | Responsibility |
|---|---|
| `Color+Hex.swift` | Hex string → `SwiftUI.Color` conversion utility. |

---

### `Features/`
All user-facing UI. Each subfolder is a self-contained feature module with its own views, components, and state.

---

#### `Canvas/`
The spatial runtime — the heart of Ficruty.

| File | Responsibility |
|---|---|
| `InfiniteCanvasView.swift` | The root spatial view. Composes the dotted grid, connection layer, and all nodes. Handles pan (`DragGesture`) and zoom (`MagnifyGesture`) with anchor-aware physics. |
| `ViewportState.swift` | Value type tracking the canvas `offset` and `scale`. Encapsulates all gesture math. |

**`Components/`** — Reusable building blocks of the canvas UI:

| File | Responsibility |
|---|---|
| `NodeView.swift` | Renders a single `SpatialNode` on the canvas. Handles the glassmorphic card, icon, title, and inline WebView embed for `.webView` nodes. |
| `NodeDetailView.swift` | The sheet-level router. Inspects `node.type` and presents the correct editor: `HTMLWebView` for `.webView`, `CodeEditorView` for `.code`, `SRSEditorView` for `.srs`. |
| `ConnectionLayer.swift` | Draws Bezier-curve connections for all `connectedNodeIds` relationships. Operates in screen-space to prevent clipping. |
| `CodeEditorView.swift` | VS Code-style editor sheet for `.code` nodes. Wraps `LineNumberedTextView` with a sleek dark tab bar and file extension label. |
| `LineNumberedTextView.swift` | `UIViewRepresentable` wrapping a dual-pane `UIView` (gutter + `UITextView`). Implements synchronized scrolling and real-time regex-based syntax highlighting for HTML, CSS, and JS. |
| `SRSEditorView.swift` | Notion-style "Zen Mode" editor for `.srs` nodes. Serif font, increased line spacing, generous padding, and a branded top bar. |
| `HTMLWebView.swift` | Thin `UIViewRepresentable` wrapping `WKWebView`. Receives compiled HTML payloads and renders them. Scroll disabled for canvas embedding. |
| `DottedBackground.swift` | The infinite dotted grid. Renders efficiently using `Canvas` and adapts to the current viewport transform. |

**`Providers/`** — Static node graph factories:

| File | Responsibility |
|---|---|
| `HomeProvider.swift` | Generates the default node graph for the Home workspace. |
| `OnboardingProvider.swift` | Generates the guided node sequence for first-run onboarding. |

---

#### `Omnibox/`
The `Cmd+K` intent-driven command palette. A floating Spotlight-style UI that surfaces project actions, navigation, and (eventually) AI commands.

---

#### `CoCaptain/`
The AI agentic sidekick interface. Currently scaffolded; full implementation in Phase 1.

---

#### `Overlays/`
Persistent floating HUD elements — the project header bar, zoom indicator, and action buttons that float above the canvas at all times.

---

#### `Subscription/`
The Pro monetization UI. Contains the glassmorphic purchase sheet, plan comparison, and StoreKit 2 purchase flow presentation.

---

### `Resources/`
Asset catalogs, app icons, and any localization files.

### `Preview Content/`
Assets used exclusively by Xcode Previews. Not included in production builds.

---

## Architectural Principles

1. **Unidirectional Data Flow**: `AppRouter` owns workspace state. `ProjectStore` owns node state. Views observe and never mutate state directly without going through a store method.
2. **No Blocking Main Thread**: All disk I/O in `ProjectStore` runs on a detached background `Task`. The canvas always renders at full frame rate.
3. **Zero Core Dependencies**: The syntax highlighter, line-number gutter, and compilation engine are all written in pure Swift/UIKit. No external packages for core functionality.
4. **Type-Safe Everything**: `NodeType`, `NodeAction`, `NodeTheme`, and `WorkspaceState` are all enums. No stringly-typed logic anywhere.
