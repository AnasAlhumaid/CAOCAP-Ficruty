<div style="display: flex; align-items: center; gap: 32px; margin-bottom: 16px;">
   <img width="200" alt="Azzam-Alrashed" src="https://github.com/user-attachments/assets/5ebe3f09-2bad-4aa3-9b30-2d88159b242d" />
   <img width="200" alt="CAOCAP-CAOCAP" src="https://github.com/user-attachments/assets/379cf647-5d89-48c5-85c8-5d83e851e298" />
</div>

# CAOCAP — MY-IDEA-OS

> *"The most dangerous thought you can have as a creative person is to think you know what you're doing."*
> — Bret Victor, [The Future of Programming](https://youtu.be/8pTEmbeENF4)

**CAOCAP is the refusal to accept today's software development process as the final form.**

**CAOCAP is not defined by any single interface, feature, or implementation. It is a relentless belief that the software development process can be improved, and a commitment to keep pushing until building software feels closer to thinking.**

The current iOS/iPadOS app is the first artifact of that belief: a native, spatial, agentic development environment where requirements, code, live preview, and AI collaboration can exist in one immediate workspace.

But CAOCAP is bigger than the current app. Spatial canvases, agents, nodes, native editors, and live previews are not the doctrine. They are experiments in service of the larger act: challenging the inherited shape of software development and building better ways to turn ideas into working systems.

---

[Mission](#the-mission) · [Principles](#principles) · [The Current Artifact](#the-current-artifact) · [What CAOCAP Does Today](#what-caocap-does-today) · [CoCaptain](#the-cocaptain) · [Tech Stack](#tech-stack) · [Status](#current-status) · [Repository Layout](#repository-layout) · [Getting Started](#getting-started) · [Devlog](#devlog) · [Contributing](#contributing) · [License](#license)

---

## The Mission

**Relentlessly improve the act of building software.**

CAOCAP starts from a simple belief: the developer experience we inherited is not sacred. Every ritual can be questioned. Every boundary can be pushed. Every slow, fragmented, overcomplicated step between an idea and a working system is a place where the future can be brought closer.

This project is not here to defend one feature, one interface, one platform, or one "new way" to program. It is here to keep asking:

- What if building software felt more immediate?
- What if tools amplified imagination instead of interrupting it?
- What if AI collaboration stayed human-in-the-loop, inspectable, and grounded in the project itself?
- What if the development environment became a medium for thought, not just a container for files?

CAOCAP is the collaborative act of pushing the software development world forward.

---

## Principles

- **No final form** — Today's development process is not the endpoint. It is material to be improved.
- **Developer experience first** — Speed, clarity, agency, and creative flow matter as core engineering concerns.
- **Implementation is not identity** — The current app is a vessel for the mission, not the limit of the mission.
- **Human-in-the-loop agents** — AI should expand the developer's control, not hide changes or erase authorship.
- **Directness over ceremony** — The path from intent to working software should keep getting shorter, clearer, and more inspectable.
- **Experiment in public** — CAOCAP grows through building, testing, shipping, learning, and pushing again.

---

## The Current Artifact

The first product expression of CAOCAP is a native iOS/iPadOS app.

CAOCAP explores a development environment where your software requirements, HTML, CSS, JavaScript, live preview, and AI collaborator can share one workspace. It is intentionally local-first, direct-manipulation-heavy, and built around the belief that programming tools can feel more alive than a stack of tabs and terminals.

The spatial model matters because it is one strong experiment in improving development. The agentic model matters because it is another. Neither is the whole point. The whole point is the relentless improvement of how software gets made.

---

## What CAOCAP Does Today

When you create a new project in CAOCAP, you open a workspace with five interconnected nodes already wired together:

```text
[SRS] ----------- [HTML] ---- [Live Preview]
                     |
               +-----+-----+
             [CSS]       [JavaScript]
```

- **SRS Node** — Write software requirements in a distraction-free Notion-style editor.
- **HTML Node** — Edit full HTML structure with native syntax highlighting and a line-number gutter.
- **CSS Node** — Style your app with real-time syntax highlighting for properties, selectors, and values.
- **JavaScript Node** — Add interactivity with keyword, comment, and string highlighting.
- **Live Preview Node** — Render the compiled app in a 9:16 `WKWebView`, with full-screen immersive preview available on tap.

Every time you edit code and tap **Done**, the Live Compilation Engine merges the HTML, CSS, and JavaScript nodes into one document and pushes it to the WebView automatically.

---

## The CoCaptain

CAOCAP's agentic path is expressed today through **CoCaptain**, an AI collaborator that understands the current project graph.

- **Context-aware intelligence**: CoCaptain reads the SRS requirements, HTML structure, CSS styles, and JavaScript logic through `ProjectContextBuilder`.
- **Agentic control**: CoCaptain can propose precise `nodeEdits` or trigger typed `AppActions`, such as creating a new node.
- **Human-in-the-loop review**: AI-proposed code changes become review items that can be inspected before they are applied.
- **Firebase AI Logic**: The current implementation streams model responses through Firebase AI Logic for low-latency collaboration.

CoCaptain is not meant to replace the developer. It is meant to make the development environment more responsive to intent while preserving inspection, authorship, and control.

---

## Tech Stack

Built with a strict focus on native performance and zero third-party dependencies for core editing, canvas, compilation, syntax highlighting, and routing logic.

| Layer | Technology |
|---|---|
| Language | Swift 5.10+ with modern concurrency |
| UI Framework | SwiftUI (`@Observable`, `GeometryReader`, `UIViewRepresentable`) |
| Backend | Firebase (Auth, AI Logic SDK, Cloud Functions) |
| AI Model | Google Gemini 3 Flash |
| Web Engine | WebKit (`WKWebView`) for HTML5/CSS3/JS execution |
| Code Editing | Native `UITextView` with custom regex-based syntax highlighting |
| Spatial Runtime | SwiftUI infinite canvas with pinch-to-zoom and pan gestures |
| Persistence | Atomic JSON writes with debounced background saves |
| Monetization | StoreKit 2 for Pro subscriptions |

---

## Current Status

**Phase 0: MVP** — Released on the App Store.

The current app implementation is fully functional:

- ✅ Infinite canvas with node linking
- ✅ Native syntax-highlighted code editors for HTML, CSS, and JavaScript
- ✅ Live compilation engine with a 500ms debounce
- ✅ Full-screen WebView previewing
- ✅ CoCaptain agentic assistant with multi-turn chat and context harvesting
- ✅ Firebase Authentication with Apple, Google, and GitHub
- ✅ StoreKit 2 Pro monetization
- ✅ App Store release
- ⏳ Post-launch onboarding polish

The current product priority is post-launch hardening: preserving trust with real users, improving first-run retention, and expanding CoCaptain into the next meaningful "wow" loop.

See [ROADMAP.md](ROADMAP.md) for the full breakdown.

---

## Repository Layout

CAOCAP is organized as a public product monorepo.

| Directory | Purpose |
|---|---|
| `ios-app/` | Native iOS/iPadOS app and Xcode project |
| `android-app/` | Reserved for the future Android app |
| `website/` | Public website, support pages, and policies |
| Root docs | Product overview, roadmap, architecture, contribution guide, and license |

---

## Getting Started

CAOCAP requires **Xcode 15+** and an iOS 17+ simulator or device.

```bash
# 1. Clone the repository
git clone https://github.com/Azzam-Alrashed/CAOCAP-CAOCAP.git

# 2. Open in Xcode
open CAOCAP-CAOCAP/ios-app/caocap/caocap.xcodeproj

# 3. Select a target and run (Cmd+R)
```

> [!TIP]
> Run on a physical iPhone for the best current CAOCAP experience. The direct manipulation model feels dramatically better on real hardware.

---

## Devlog

### 2026-05-01: Movement-First README
- **Mission Rewrite**: Reframed CAOCAP as a movement and a relentless belief in improving the software development process, with the iOS app positioned as the current product artifact.
- **Principles Added**: Added project principles that separate the mission from any single implementation, feature, or interface.

### 2026-04-30: App Store Release
- **App Store Launch**: CAOCAP is now released on the App Store, moving the project from pre-launch MVP work into post-launch iteration.
- **Post-Launch Focus**: The next product cycle prioritizes first-user feedback, onboarding refinement, reliability, and deeper CoCaptain workflows.

### 2026-04-29: Technical Debt & Refactoring
- **Project Template Extraction**: Extracted the default node graph logic out of `AppRouter` and into a dedicated `ProjectTemplateProvider`, enforcing stricter separation of concerns for the navigation layer.
- **Production Diagnostics**: Migrated legacy primitive `print(...)` logging across StoreKit, Auth, and the Command Palette systems to Apple's unified `OSLog` (`Logger`) framework for reliable production diagnostics.

### 2026-04-24: Agentic Control & Gemini 3 Flash
- **Gemini 3 Flash**: Updated the core LLM to the latest `gemini-3-flash-preview` via Firebase AI Logic, bringing improved reasoning and faster response times.
- **Agentic Control v1**: Scaffolded the `CoCaptainAgentCoordinator` architecture to support autonomous actions and structured node patching.
- **Vibe Coding Workflow**: Implemented **Review Bundles** and `NodePatchEngine` for human-in-the-loop code injection. Ask the AI to modify your CSS or HTML, and apply the diff with one tap.
- **App Action Dispatcher**: Added the `AppActionDispatcher` to allow CoCaptain to navigate the app or create nodes on behalf of the user.

### 2026-04-23: Agentic Intelligence & Firebase
- **CoCaptain v1.0**: Implemented multi-turn chat memory and scroll position persistence for a seamless AI experience.
- **Firebase AI Integration**: Switched to Firebase AI Logic SDK for Gemini-powered responses, enabling real-world agentic capabilities.
- **Secure Authentication**: Integrated Firebase Auth with support for Apple, Google, and GitHub. Added silent anonymous sign-in and account linking UI.
- **UI Polish**: Redesigned the CoCaptain input area with a sleek, auto-growing layout. Switched user message bubbles to a premium monochromatic blue gradient.
- **Interaction Design**: Implemented "Slide-to-Select" radial menu behavior for the `FloatingCommandButton`.

### 2026-04-22: Spatial WebView & Live Coding Engine
- **Live Preview WebView**: Integrated a 9:16 `WKWebView` node as the central rendering target for all spatial code.
- **Multi-Node Linking**: Refactored `SpatialNode` with `connectedNodeIds` for 1-to-N directed graph connections.
- **Native Code Editors**: Built `CodeEditorView` wrapping `UITextView` with a custom regex-based syntax highlighting engine and a synchronized line-number gutter. Zero external dependencies.
- **SRS Zen Mode**: Created a Notion-inspired `SRSEditorView` with serif typography and generous margins.
- **Live Compilation Engine**: `compileLivePreview()` in `ProjectStore` automatically merges HTML, CSS, and JS into a unified WebView payload. Debounced at 500ms.
- **Interactive Default Template**: New projects initialize with parallax mouse tracking and click-to-pulse animations.

### 2026-04-22: Architecture Refactoring
- **Type-Safe Routing**: Replaced stringly-typed node actions with a strict `NodeAction` enum for compile-time safety.
- **Non-blocking Persistence**: Offloaded disk I/O to background tasks, maintaining 120Hz canvas responsiveness.
- **Domain-Driven Structure**: Established a feature-based folder structure (`Models`, `Services`, `Navigation`, `Features`).

### 2026-04-21: Foundations
- **Infinite Canvas**: Gesture-driven pan/zoom with anchor-aware pinch scaling.
- **Persistent Nodes**: Draggable, persisted spatial nodes with an atomic JSON write layer.
- **Command Palette**: `Cmd+K` Spotlight-style Omnibox for intent-driven navigation.
- **StoreKit 2**: Initial premium subscription integration with a glassmorphic purchase sheet.

### 2026-04-20: The Vision
- Mission locked: relentless focus on improving the software development process.
- Committed to challenging the inherited developer experience and building toward better futures.

---

## Contributing

CAOCAP is in active early-stage development. The work prioritizes architectural stability, product trust, and long-term vision over unfocused feature growth.

- **Discuss First**: For major changes, open an issue to align with the project philosophy before writing code.
- **Standards**: Use `@Observable` on iOS 17+, Swift structured concurrency, and non-blocking infrastructure.
- **Clean Docs**: If your change alters the architecture, update [STRUCTURE.md](STRUCTURE.md).
- **Movement Mindset**: Contributions should improve the act of building software, strengthen the current product, or clarify the path toward better development environments.

See [CONTRIBUTING.md](CONTRIBUTING.md) for the full guide.

---

## License

Distributed under the **GNU General Public License v3.0**. See [LICENSE](LICENSE) for the full text.
