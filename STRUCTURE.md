# Ficruty — Codebase Architecture

This document is the authoritative map of the Ficruty codebase. Ficruty is organized as a public product monorepo, with each platform isolated in its own top-level directory. The iOS app uses a **domain-driven, feature-based structure** to maximize isolation, scalability, and developer clarity.

> [!NOTE]
> If you add a new file that changes the architecture, update this document in the same commit.

---

## Repository Root

```
Ficruty/
├── ios-app/              # Native iOS/iPadOS app
│   └── caocap/           # Xcode project and all Swift source files
├── android-app/          # Future Android app
├── website/              # Public website, support pages, and policies
├── README.md             # Project overview, mission, and devlog
├── ROADMAP.md            # Strategic milestone tracker
├── STRUCTURE.md          # This document — the architectural map
├── CONTRIBUTING.md       # Contribution standards and git workflow
└── LICENSE               # GNU GPL v3.0
```

---

## iOS Source Tree (`ios-app/caocap/caocap/`)

```
caocap/
├── App/
├── Navigation/
├── Models/
├── Services/
├── Extensions/
├── Features/
│   ├── Auth/
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
| `caocapApp.swift` | `@main` entry point. Initializes Firebase and injects `AppRouter` as an environment object. |
| `ContentView.swift` | Root view. Observes `AppRouter` and switches between Onboarding, Auth, Home, and Project workspaces. |
| `AppConfiguration.swift` | Static configuration for Firebase Function names and environment keys. |
| `Info.plist` | System-level permissions and metadata. |

---

### `Navigation/`
Centralized, type-safe routing. All workspace transitions flow through here — nothing navigates by string.

| File | Responsibility |
|---|---|
| `AppRouter.swift` | `@Observable` class managing `WorkspaceState` (`.onboarding`, `.auth`, `.home`, `.project`). Owns all `ProjectStore` instances. and `createNewProject()` initialization logic. |

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
| `AuthenticationManager.swift` | Wraps Firebase Auth. Handles anonymous login, account linking, and social provider flows. |
| `LLMService.swift` | Interface for the Firebase AI Logic SDK. Manages streaming sessions with the Gemini backend. |
| `AppActionDispatcher.swift` | Centralized action registry. Allows the app and the AI agent to trigger high-level navigation and project mutations. |
| `ProjectContextBuilder.swift` | Logic to "harvest" the spatial graph and serialize it into a grounded prompt context for the LLM. |
| `NodePatchEngine.swift` | A precision editing engine that applies partial patches (replace/insert/append) to HTML, CSS, and JS nodes. |
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

#### `Auth/`
Identity management and account security.

| File | Responsibility |
|---|---|
| `SignInView.swift` | Multi-provider sign-in sheet with Apple, Google, and GitHub options. Supports "Save Work" account linking for anonymous users. |

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
| `OnboardingProvider.swift` | Loads the manifest-backed guided node sequence for first-run onboarding, with a hardcoded fallback. |
| `ProjectTemplateProvider.swift` | Generates the default interconnected node graph for new projects. |

---

#### `Omnibox/`
The `Cmd+K` intent-driven command palette. A floating Spotlight-style UI that surfaces project actions, navigation, and AI commands.

---

#### `CoCaptain/`
The agentic AI companion. A native sheet interface for real-time collaboration.

| File | Responsibility |
|---|---|
| `CoCaptainView.swift` | Implements a spatial chat UI with monochromatic gradients and persistent scroll states. |
| `CoCaptainViewModel.swift` | High-level state management for the CoCaptain UI. |
| `CoCaptainAgentCoordinator.swift` | The orchestrator of agentic control. Manages the dual-path execution flow and review bundle generation. |
| `CoCaptainAgentModels.swift` | Domain models for agent actions, node edits, review items, and the chat timeline. |
| `CoCaptainAgentOutputAdapter.swift` | Source-agnostic adapter layer that converts Firebase function calls and fenced JSON into directives for validation and execution. |
| `CoCaptainAgentParser.swift` | Logic to parse raw LLM text into structured `CoCaptainAgentPayload` objects. |
| `CoCaptainAgentValidator.swift` | Validates parsed agent payloads before any app action execution or review bundle generation. |

---

#### `Overlays/`
Persistent floating HUD elements — the project header bar, zoom indicator, and action buttons that float above the canvas at all times.

| File | Responsibility |
|---|---|
| `FloatingCommandButton.swift` | Implements a **Slide-to-Select radial menu** for quick access to tools. |
| `CanvasHUDView.swift` | Displays project title and current zoom percentage. |

---

#### `Subscription/`
The Pro monetization UI. Contains the glassmorphic purchase sheet, plan comparison, and StoreKit 2 purchase flow presentation.

---

### `Resources/`
Asset catalogs, app icons, localization files, and bundled product manifests such as `tutorial.json`.

### `Preview Content/`
Assets used exclusively by Xcode Previews. Not included in production builds.

---

## Architectural Principles

1. **Unidirectional Data Flow**: `AppRouter` owns workspace state. `ProjectStore` owns node state. Views observe and never mutate state directly.
2. **No Blocking Main Thread**: All disk I/O and network requests run on detached background tasks.
3. **Agentic Context Harvesting**: CoCaptain reads the *entire* spatial graph state before every prompt, ensuring grounded AI responses.
4. **Zero Core Dependencies**: Core logic (compilation, syntax highlighting) remains in pure Swift. Firebase is used exclusively for identity and AI.
5. **Type-Safe Everything**: `NodeAction`, `WorkspaceState`, and `LLMMessage` are all strict enums or structs.
