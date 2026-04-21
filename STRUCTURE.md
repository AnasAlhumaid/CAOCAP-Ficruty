# Ficruty Folder Structure

This document outlines the architectural hierarchy of the Ficruty (caocap) codebase. We aim for a feature-based organization to ensure scalability and isolation.

## Root Directory
- `caocap/`: The main Xcode project and source files.
- `README.md`: Project overview and mission.
- `STRUCTURE.md`: This document.
- `LICENSE`: GNU GPL v3.0.

---

## Source Structure (`caocap/caocap/`)

### 1. `App/`
Entry points and system-level configurations.
- `caocapApp.swift`: The main application entry point.
- `Info.plist`: App configuration.
- `caocap.entitlements`: App capabilities.

### 2. `Features/`
The functional modules of the application. Each feature should contain its own Views, ViewModels, and Models.

- **`Canvas/`**: The spatial runtime where nodes live.
  - `InfiniteCanvasView.swift`: The grid rendering and gesture logic.
- **`Omnibox/`**: Navigation and intent-driven command palette.
  - `CommandPaletteView.swift`: The UI for the palette.
  - `CommandPaletteViewModel.swift`: Search logic and command execution.
- **`Overlays/`**: Heads-up display and floating interface elements.
  - `FloatingCommandButton.swift`: The AI-sparkle button.

### 3. `Core/`
Shared primitives and infrastructure.
- `DesignSystem/`: UI constants, glassmorphism tokens, and custom modifiers.
- `Extensions/`: Utility extensions for SwiftUI and Foundation.
- `Navigation/`: Global routing or coordination logic.

### 4. `Resources/`
Non-code assets.
- `Assets.xcassets`: Images, colors, and icons.

### 5. `Preview Content/`
Mock data and assets specifically for Xcode Previews.

---

## Migration Plan
1. [x] Create the folder hierarchy in the filesystem.
2. [x] Move `.swift` files to their respective feature folders.
3. [x] Update the `.xcodeproj` file to reflect the new file paths.
4. [x] Verify that the project builds and previews still work.
