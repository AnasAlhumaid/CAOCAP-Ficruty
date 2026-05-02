# Ficruty Project Roadmap

This document outlines the strategic milestones for Ficruty (caocap). Our goal is to move from a spatial foundation to a full-fledged agentic development environment — the "Forgotten Future" of programming.

---

## 🏁 Phase 0: MVP — First User *(Released on the App Store)*
*Focus: The minimum viable experience that is stable, polished, and shippable.*

- [x] **Spatial Canvas**: Infinite grid with gesture-driven pan, zoom, and 30% default entry zoom.
- [x] **Project Management**: Create, persist, and navigate named projects via the Home workspace.
- [x] **Omnibox / Command Palette**: `Cmd+K` intent-driven command palette with spatial search.
- [x] **Node Linking**: Visual Bezier-curve connections between nodes (1-to-N directed graph).
- [x] **Live Preview WebView**: 9:16 `WKWebView` node with full-screen immersive sheet.
- [x] **Native Code Editors**: Syntax-highlighted `CodeEditorView` (HTML/CSS/JS) + SRS Zen Mode editor.
- [x] **Live Compilation Engine**: Real-time HTML+CSS+JS merging into WebView, debounced at 500ms.
- [x] **Monetization (Pro)**: StoreKit 2 subscription integration.
- [x] **Firebase Integration**: Authentication (Apple, Google, GitHub) and AI Logic infrastructure.
- [x] **App Store Compliance**: Privacy Policy, Terms of Service, data usage declarations.
- [x] **App Store Release**: Ficruty is available to users through the App Store.

### Post-Launch Polish

- [/] **Onboarding Polish**: Continue refining the guided first-run experience after launch.
    - [x] **Tutorial Manifest**: Create a `tutorial.json` with pre-placed learning nodes.
    - [ ] **Spatial Markers**: Implement animated "Focus Rings" to highlight UI elements during steps.
    - [ ] **Gesture Gates**: Add logic that unlocks the next step only after a specific pan/zoom/long-press action.
- [ ] **Post-Launch Feedback Loop**: Make user support, issue triage, and release follow-up part of the product rhythm.
- [ ] **Release Hardening**: Re-check account deletion, privacy links, subscriptions, restore purchases, onboarding reset, and first project creation after each App Store update.

---

## 🧠 Phase 1: Agentic Intelligence *(Next)*
*Focus: Integrating AI deeply into the spatial workflow to enable true "Vibe Coding."*

- [x] **Firebase Auth**: Secure login and anonymous account linking for seamless cross-device persistence.
- [x] **CoCaptain UI**: A polished, floating AI sidekick panel with glassmorphic design.
- [x] **Context Engine**: Logic to "harvest" the current canvas state (nodes, connections) via `ProjectContextBuilder`.
- [ ] **Project Analysis**: Auto-analysis of canvas to suggest missing nodes (e.g., "You have HTML but no CSS").
- [x] **The "Apply" Flow**: A UI interaction to preview and inject AI-generated code directly into a selected node.
- [x] **Multi-turn Chat**: Persistent conversation memory with scroll position preservation.
- [x] **Streaming UI**: Token-aware text view powered by Firebase AI Logic SDK.
- [x] **Agentic Actions**: Implementation of `AppActionDispatcher` to allow the AI to control app navigation and project state.
- [x] **Code Generation**: CoCaptain generates HTML/CSS/JS from a natural language SRS node.
- [ ] **Intent-to-Node**: Transform a natural language prompt directly into a fully wired node graph.

---

## ⚡ Phase 2: The Code Runtime *(Next)*
*Focus: Making the spatial canvas a true execution environment.*

- [ ] **Omnibox Canvas Search**: Search-to-fly functionality.
    - [ ] **Search Index**: Real-time indexing of node titles and text content.
    - [ ] **Flight Engine**: Implementation of smooth viewport interpolation (Ease-In-Out) to "fly" to a node.
    - [ ] **Focus Zoom**: Automatically adjust zoom level to fit the targeted node perfectly.
- [ ] **Multi-Project Templates**: A library of starter templates (games, landing pages, tools) selectable from the Omnibox.
- [ ] **Spatial Debugger**: Visualize variable flow, console output, and execution state as canvas overlays.
- [ ] **Console Node**: A dedicated node type that captures `console.log` output from the WebView in real-time.
- [ ] **Project Templates**: A library of starter templates (games, landing pages, tools) selectable from the Omnibox.
- [ ] **File System Bridge**: Export projects as a standard HTML/CSS/JS file bundle or a Git repository.

---

## 🌐 Phase 3: Collaborative Ecosystem
*Focus: Bringing developers together in shared spatial environments.*

- [ ] **Real-time Collaboration**: Multi-user spatial canvases with presence indicators and shared agentic history.
- [ ] **Cloud Sync**: iCloud-backed project persistence across devices.
- [ ] **Plugin System**: Allow third-party developers to create custom node types and agent behaviors.
- [ ] **Share Sheet**: Export a project as a shareable, self-contained `.ficruty` bundle.

---

> [!NOTE]
> This roadmap is a living document. As we "vibe code" and discover new possibilities, these milestones will evolve. The phases are ordered by user impact, not technical complexity.
