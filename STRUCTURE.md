# Ficruty Folder Structure

This document outlines the architectural hierarchy of the Ficruty (caocap) codebase. We utilize a domain-driven, feature-based organization to ensure maximum scalability, isolation, and developer focus.

## Root Directory
- `caocap/`: The main Xcode project and source files.
- `README.md`: Project overview and mission.
- `STRUCTURE.md`: This document.
- `LICENSE`: GNU GPL v3.0.

---

## Source Structure (`caocap/caocap/`)

### 1. `App/`
The application shell and lifecycle management.
- `caocapApp.swift`: Application entry point.
- `ContentView.swift`: The root view that manages high-level workspace switching.
- `Info.plist` & `caocap.entitlements`: System-level configuration.

### 2. `Navigation/`
Centralized routing and coordination.
- `AppRouter.swift`: Manages the application's workspace state (.onboarding vs .home).

### 3. `Models/`
Pure domain data structures, independent of UI or persistence logic.
- `SpatialNode.swift`: The core model representing an object on the canvas.
- `NodeTheme.swift`: Theming tokens for spatial nodes.

### 4. `Services/`
Global singletons, infrastructure, and heavy-lifting logic.
- `ProjectStore.swift`: The persistence layer with asynchronous, debounced saving.
- `SubscriptionManager.swift`: StoreKit 2 integration for premium features.

### 5. `Extensions/`
Reusable language and framework extensions.
- `Color+Hex.swift`: Hex-to-SwiftUI color conversion.

### 6. `Features/`
Functional UI modules. Each feature contains its views and state.

- **`Canvas/`**: The spatial runtime engine.
  - `InfiniteCanvasView.swift`: The core spatial engine.
  - `ViewportState.swift`: State tracker for pan/zoom levels.
  - **`Components/`**: Reusable canvas UI elements (`NodeView`, `ConnectionLayer`, etc.).
  - **`Providers/`**: Static and dynamic data generators (`HomeProvider`, `OnboardingProvider`).
- **`Omnibox/`**: Intent-driven command palette.
- **`CoCaptain/`**: AI agentic interface.
- **`Overlays/`**: HUD and floating UI elements.
- **`Subscription/`**: Monetization and purchase UI.

### 7. `Resources/`
Asset catalogs, fonts, and localization files.

### 8. `Preview Content/`
Assets specifically for Xcode Previews.
