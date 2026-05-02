# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project Snapshot

Ficruty, also called CAOCAP in the iOS target, is a spatial, agentic code editor for iOS and iPadOS. The main product is the native SwiftUI app in `ios-app/caocap`. The website in `website` supports the public product, support, privacy, and terms pages. `android-app` is reserved for future work.

The current product priority is launch readiness: onboarding polish, App Store compliance, TestFlight readiness, and careful improvements to the CoCaptain agent flow.

## Start Here

Before changing code, read these files:

- `README.md` for product vision and current status.
- `ROADMAP.md` for priority and milestone context.
- `STRUCTURE.md` for the architecture map.
- `CONTRIBUTING.md` for SwiftUI and workflow standards.
- This file for agent-specific working rules.
- The nearest feature `README.md` before editing high-complexity feature code.

If architecture changes, update `STRUCTURE.md` in the same change. If product status changes, update `README.md` or `ROADMAP.md` where appropriate.

## Repository Map

- `ios-app/caocap/caocap/App`: app entry point, root view, configuration, app metadata.
- `ios-app/caocap/caocap/Navigation`: type-safe workspace routing through `AppRouter`.
- `ios-app/caocap/caocap/Models`: pure domain data such as `SpatialNode` and `NodeTheme`.
- `ios-app/caocap/caocap/Services`: persistence, authentication, LLM, subscriptions, app actions, context building, patching.
- `ios-app/caocap/caocap/Features`: user-facing SwiftUI features.
- `ios-app/caocap/caocap/Resources`: assets, privacy manifest, StoreKit config, local resources.
- `ios-app/caocap/caocapTests`: unit tests.
- `ios-app/caocap/caocapUITests`: UI tests.
- `website/src/app`: Next.js App Router pages and components.

## Architecture Rules

- `AppRouter` owns workspace navigation and active `ProjectStore` selection.
- `ProjectStore` owns node state, viewport state, persistence, and live preview compilation.
- Views should observe state and call store/router methods; avoid burying business logic in SwiftUI views.
- `Models` should stay pure: no UI, persistence, networking, or Firebase dependencies.
- `Services` are the right home for long-lived infrastructure and non-view business logic.
- `Features/*/Providers` are the right home for static node graph factories and templates.
- Keep navigation and actions type-safe. Prefer enums and structs over stringly typed checks.
- Keep the app local-first. Do not add network behavior unless it belongs to Firebase Auth, Firebase AI Logic, or an explicitly requested integration.

## Swift Standards

- Use `@Observable` for app/view state on iOS 17+.
- Use Swift structured concurrency (`async`/`await`, `Task`) for asynchronous work.
- Do not block the main actor with disk I/O, network calls, parsing, or heavy computation.
- Prefer small SwiftUI views, focused modifiers, and feature-local components over large `body` implementations.
- Use `Logger` from `OSLog` for diagnostics. Avoid new `print(...)` calls in production paths.
- Keep comments sparse and useful. Explain surprising behavior, not obvious assignments.
- Avoid new third-party dependencies for core editing, canvas, compilation, syntax highlighting, or routing logic.

## CoCaptain And LLM Flow

The agentic path is intentionally human-in-the-loop:

- `ProjectContextBuilder` serializes the current canvas into prompt context.
- `LLMService` talks to Firebase AI Logic and streams model responses.
- `CoCaptainAgentParser` extracts structured action payloads from LLM text.
- `CoCaptainAgentCoordinator` separates safe actions from review-required changes.
- `NodePatchEngine` previews and applies exact node edits.
- `AppActionDispatcher` exposes high-level app actions to the assistant.

When editing this flow, preserve review bundles and avoid auto-applying code edits without user confirmation. Add tests for parser, patching, action dispatch, and conflict behavior when changing contracts.

## Build And Test

iOS project:

```bash
xcodebuild -project ios-app/caocap/caocap.xcodeproj -scheme caocap -destination 'platform=iOS Simulator,name=iPhone 16' build
xcodebuild -project ios-app/caocap/caocap.xcodeproj -scheme caocap -destination 'platform=iOS Simulator,name=iPhone 16' test
```

If the named simulator is unavailable, run `xcrun simctl list devices available` and use an installed iOS simulator destination.

Website:

```bash
cd website
npm install
npm run lint
npm run build
```

Prefer running the narrowest useful verification for the change. If verification cannot be run, state why in the final response.

## Editing Guidance

- Keep changes tightly scoped to the requested behavior.
- Do not rewrite unrelated files or reformat broad areas opportunistically.
- Do not revert user changes. Treat unexpected modified files as user-owned unless explicitly told otherwise.
- Avoid editing `ios-app/caocap/caocap.xcodeproj/project.pbxproj` unless adding/removing Xcode-tracked files or changing build settings is genuinely required.
- When adding Swift source files, make sure they are included in the Xcode project target.
- Prefer extracting large hardcoded graph/template literals into providers instead of expanding routers or views.
- When adding user-facing text, keep localization in mind and avoid scattering duplicate strings.
- For UI changes, check compact and regular device sizes when possible.

## Good First Debt Payments

- Move onboarding toward a manifest-backed flow when implementing roadmap onboarding work.
- Add fixtures for project JSON save/load, corrupted project recovery, onboarding, and multi-node linked graphs.
- Expand tests around live preview compilation, project persistence, routing, node role matching, and patch conflicts.

## Product Guardrails

- Ficruty is a spatial IDE, not a conventional file-tree editor. Preserve the canvas-first mental model.
- The product philosophy prioritizes developer experience, direct manipulation, and agentic assistance.
- The app should remain polished enough for App Store/TestFlight work. Compliance, privacy links, account deletion, and subscription wording matter.
- New features should improve the spatial workflow or launch readiness, not just add surface area.
