<div style="display: flex; align-items: center; gap: 32px; margin-bottom: 16px;">
   <img width="200" alt="Azzam-Alrashed" src="https://github.com/user-attachments/assets/5ebe3f09-2bad-4aa3-9b30-2d88159b242d" />
   <img width="200" alt="CAOCAP-Ficruty" src="https://github.com/user-attachments/assets/379cf647-5d89-48c5-85c8-5d83e851e298" />
</div>

# CAOCAP Ficruty — MY-IDEA-OS

> *"The most dangerous thought you can have as a creative person is to think you know what you're doing."*
> — Bret Victor, [The Future of Programming](https://youtu.be/8pTEmbeENF4)

**CAOCAP is my integrated development environment agentic operating system for turning ideas into software.**

Ficruty is built natively for iOS/iPadOS as the workspace where that agent comes to life: your software requirements, HTML, CSS, JavaScript, and live preview can exist on an infinite canvas as interconnected nodes — all compiling and running in real-time.

---

[Mission](#the-mission) · [What It Does](#what-it-does) · [Philosophy](#the-philosophy) · [Tech Stack](#tech-stack) · [Status](#current-status) · [Repository Layout](#repository-layout) · [Getting Started](#getting-started) · [Devlog](#devlog) · [Contributing](#contributing) · [License](#license)

---

## The Mission
**Push the boundaries. Improve the experience.**

I am not here to advocate for a specific niche or a "new way" to program. I am here to relentlessly challenge how software is built. If a boundary exists that limits a developer's creativity, I want to push it. If an experience is broken, I want to fix it.

Ficruty is the pursuit of the ultimate developer experience, by any means necessary.

---

## What It Does

When you create a new project in Ficruty, you don't open a file. You open a **spatial workspace** with five interconnected nodes already wired together:

```
[SRS] ──────────── [HTML] ──── [Live Preview]
                     │
               ┌─────┴─────┐
             [CSS]       [JavaScript]
```

- **SRS Node** — Write your software requirements in a distraction-free Notion-style editor.
- **HTML Node** — Edit full HTML structure with native syntax highlighting and a line-number gutter.
- **CSS Node** — Style your app with real-time syntax highlighting (properties, selectors, values).
- **JavaScript Node** — Add interactivity with keyword, comment, and string highlighting.
- **Live Preview Node** — A 9:16 `WKWebView` that automatically compiles all three code nodes and renders them live. Tap it for a full-screen immersive preview.

**Every time you edit code and tap "Done", the Live Compilation Engine merges your HTML, CSS, and JavaScript into a single document and pushes it to the WebView — automatically.**

---

## The CoCaptain 🧠
Ficruty isn't just spatial; it's **agentic**. The **CoCaptain** is your AI sidekick that understands the entire spatial graph.

- **Context-Aware Intelligence**: CoCaptain reads the SRS requirements, HTML structure, CSS styles and JS logic via the `ProjectContextBuilder` to provide grounded coding assistance.
- **Agentic Control (Vibe Coding)**: Ask CoCaptain to "fix the layout" or "add a dark mode," and it can generate precise `nodeEdits` or trigger `AppActions` (like "Create New Node").
- **Human-in-the-Loop**: All AI-proposed changes are bundled into **Review Items**, allowing you to preview and "Apply" them with a single tap.
- **Firebase AI Logic**: Powered by Google **Gemini 3 Flash** through Firebase AI Logic for low-latency, streaming responses.

---

## The Philosophy
Ficruty is a technical pursuit of the "Forgotten Future" described by **Bret Victor** in [The Future of Programming](https://youtu.be/8pTEmbeENF4). The premise: in 1973, the future of programming was spatial, direct, and immediate. We ended up in a world of text files and compilers instead.

Ficruty is the correction.

---

## Tech Stack
Built with a strict focus on **native performance** and **zero third-party dependencies** for core functionality.

| Layer | Technology |
|---|---|
| Language | Swift 5.10+ with modern concurrency |
| UI Framework | SwiftUI (`@Observable`, `GeometryReader`, `UIViewRepresentable`) |
| Backend | Firebase (Auth, AI Logic SDK, Cloud Functions) |
| AI Model | Google Gemini 3 Flash |
| Web Engine | WebKit (`WKWebView`) for HTML5/CSS3/JS execution |
| Code Editing | Native `UITextView` with custom regex-based syntax highlighting |
| Spatial Engine | SwiftUI infinite canvas with pinch-to-zoom and pan gestures |
| Persistence | Atomic JSON writes with debounced background saves |
| Monetization | StoreKit 2 for Pro subscriptions |

---

## Current Status

**Phase 0: MVP** — Completing the final pre-launch checklist.

The core spatial development environment is fully functional:
- ✅ Infinite canvas with node linking
- ✅ Native syntax-highlighted code editors (HTML, CSS, JS)
- ✅ Live compilation engine (500ms debounce)
- ✅ Full-screen WebView previewing
- ✅ CoCaptain Agentic Assistant (Multi-turn chat, context harvesting)
- ✅ Firebase Authentication (Apple, Google, GitHub)
- ✅ StoreKit 2 Pro monetization
- ⏳ Onboarding polish
- ⏳ App Store compliance & TestFlight

See [ROADMAP.md](ROADMAP.md) for the full breakdown.

---

## Repository Layout

Ficruty is organized as a public product monorepo.

| Directory | Purpose |
|---|---|
| `ios-app/` | Native iOS/iPadOS app and Xcode project |
| `android-app/` | Reserved for the future Android app |
| `website/` | Reserved for the public website, support pages, and policies |
| Root docs | Product overview, roadmap, architecture, contribution guide, and license |

---

## Getting Started

Ficruty requires **Xcode 15+** and an iOS 17+ simulator or device.

```bash
# 1. Clone the repository
git clone https://github.com/Azzam-Alrashed/CAOCAP-Ficruty.git

# 2. Open in Xcode
open CAOCAP-Ficruty/ios-app/caocap/caocap.xcodeproj

# 3. Select a target and run (Cmd+R)
```

> [!TIP]
> Run on a physical iPhone for the best spatial canvas experience. Pinch-to-zoom feels dramatically better on real hardware.

---

## Devlog

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
- Mission locked: relentless focus on **Developer Experience (DX)**.
- Committed to the Bret Victor "Forgotten Future" philosophy as the north star.

---

## Contributing

Ficruty is in active early-stage development ("War Room" mode). I prioritize **architectural stability** and **long-term vision** over rapid feature growth.

- **Discuss First**: For major changes, open an issue to align with the project philosophy before writing code.
- **Standards**: `@Observable` (iOS 17+) for state, `async/await` for concurrency, no blocking `@MainActor` I/O.
- **Clean Docs**: If your change alters the architecture, update [STRUCTURE.md](STRUCTURE.md).

See [CONTRIBUTING.md](CONTRIBUTING.md) for the full guide.

---

## License
Distributed under the **GNU General Public License v3.0**. See [LICENSE](LICENSE) for the full text.
