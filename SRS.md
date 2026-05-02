# Software Requirements Specification: CAOCAP

Version: 0.6
Status: Operational movement-first product specification for shipped app and next-cycle planning
Date: 2026-05-01
Public product name: CAOCAP
Primary artifact: Native iOS and iPadOS app
Current release posture: App Store released, post-launch hardening active

## Change History

| Version | Date | Summary |
|---|---|---|
| 0.6 | 2026-05-01 | Resolved all 15 open questions. Added measurable thresholds to NFR-PRIVACY, NFR-SECURITY, and NFR-ACCESSIBILITY. Added Section 14 (Stakeholder Approval), Appendix A (P0 Requirement Attributes), and Appendix B (Internal Interface Contracts). |
| 0.5 | 2026-05-01 | Added change history table. Added measurable A-suffixed thresholds to NFR-USABILITY, NFR-RELIABILITY, and NFR-AISAFETY. Resolved OQ-001, OQ-013, and OQ-015. |
| 0.4 | 2026-05-01 | Promoted to operational specification. Added SRS readiness states, CoCaptain payload contracts, P0 launch checklist, scenario acceptance criteria, traceability matrix, risk register, and open questions. Aligned product name to CAOCAP throughout. |
| 0.3 | Prior | Requirements coverage for functional areas, non-functional requirements, and external interfaces. |

## 1. Introduction

### 1.0 Executive Summary

CAOCAP is a shipped iOS and iPadOS product and a long-running product thesis: software development can be made more immediate, inspectable, and humane. The current product expression combines a spatial workspace, SRS node, editable web code nodes, live preview, and CoCaptain, an AI collaborator that can understand project context and propose reviewable changes.

For the current release cycle, this SRS prioritizes trust over breadth. P0 work must preserve user projects, keep first project creation reliable, maintain App Store compliance, make subscription and account behavior clear, and ensure CoCaptain never silently mutates meaningful project content. P1-P3 requirements describe the direction for stronger SRS guidance, deeper agentic workflows, runtime surfaces, export, collaboration, and ecosystem growth.

The most important product invariant is this: CAOCAP may change its interface, platform, or implementation, but it shall not abandon the relationship between intent, implementation, feedback, and user-controlled AI assistance.

### 1.1 Purpose

This Software Requirements Specification defines the vision, product scope, requirements, quality attributes, and validation approach for CAOCAP.

CAOCAP is not defined by a single interface, feature, platform, or implementation. CAOCAP is the refusal to accept today's software development process as the final form. It is a relentless belief that the software development process can be improved, and a commitment to keep pushing until building software feels closer to thinking.

The current iOS and iPadOS app is the first shipped product expression of that belief. Its spatial workspace, requirements node, code nodes, live preview, and CoCaptain assistant are not the doctrine. They are current answers to a larger question: how can the act of building software become more immediate, understandable, inspectable, collaborative, and humane?

This SRS exists to keep that larger question explicit while still giving engineering, design, QA, and product work concrete requirements to build against.

### 1.2 Product Identity

The public product name is CAOCAP.

All public-facing product requirements, user journeys, legal copy, support pages, App Store copy, in-app language, and roadmap language should use CAOCAP as the product name. Historical or repository-specific aliases should not define the product identity.

Repository, target, bundle, or legacy names may appear in internal build configuration, historical documentation, or migration notes when changing them would create unnecessary operational risk. They should not leak into user-facing product copy unless required for continuity or legal clarity.

### 1.3 Product Thesis

CAOCAP is the refusal to accept today's software development process as the final form.

CAOCAP is not defined by any single interface, feature, or implementation. It is a relentless belief that the software development process can be improved, and a commitment to keep pushing until building software feels closer to thinking.

This thesis leads to several product consequences:

- The SRS is not paperwork. It is the first visible form of intent.
- Code is not isolated text. It is one expression of an idea under constraints.
- Preview is not an afterthought. It is feedback that keeps thought connected to result.
- AI is not a hidden author. It is a collaborator whose work must remain inspectable.
- The workspace is not sacred because it is spatial. It is valuable only if it improves how users think, build, test, and revise.

### 1.4 Intended Audience

This document is intended for:

- Product leadership defining CAOCAP's long-term direction.
- Designers shaping the requirements, workspace, and AI collaboration experience.
- Engineers implementing CAOCAP across app, agent, runtime, persistence, and service layers.
- QA reviewers validating behavior against product requirements.
- App Store, TestFlight, privacy, subscription, and compliance reviewers.
- Future contributors who need to understand the mission before changing implementation details.

### 1.5 Document Scope

This SRS covers both:

- The movement-level product promise: relentlessly improving the software development process.
- The current product artifact: a native iOS and iPadOS development environment with first-class requirements, a connected workspace, live preview, and CoCaptain.

This SRS includes requirements for:

- Intent capture and SRS authoring.
- Project artifacts, nodes, workspace navigation, and relationships.
- Code editing and live preview.
- CoCaptain agentic assistance and human-in-the-loop review.
- Project creation, persistence, templates, export, sharing, and future sync.
- Authentication, subscriptions, privacy, compliance, and public support surfaces.
- Accessibility, localization, maintainability, quality, and launch readiness.

This SRS does not prescribe every internal implementation detail. Architecture belongs in `STRUCTURE.md` and feature-level design documents.

### 1.6 Definitions

| Term | Definition |
|---|---|
| CAOCAP | The public product and movement: a commitment to improving the software development process, currently expressed as an iOS and iPadOS software creation environment. |
| Current product artifact | The shipped iOS and iPadOS implementation of CAOCAP. |
| Software development process | The full path from idea, intent, requirements, design, code, feedback, testing, revision, collaboration, release, and learning. |
| SRS | Software Requirements Specification. In CAOCAP, the SRS is a first-class expression of intent, not a static document separate from the build process. |
| SRS node | A project artifact that captures product intent, users, goals, requirements, acceptance checks, constraints, and open questions. |
| Workspace | The current project environment where artifacts can be created, inspected, connected, edited, previewed, and assisted. |
| Spatial workspace | The current canvas-based workspace model used by the iOS and iPadOS implementation. |
| Node | A discrete project artifact, tool, document, preview, runtime, command, or assistant surface inside the workspace. |
| Code node | A node containing editable implementation content such as HTML, CSS, JavaScript, or future supported languages. |
| Live preview node | A node that renders the current runnable state of the project. |
| CoCaptain | CAOCAP's AI collaborator, designed to understand project context and propose or execute actions with appropriate user control. |
| Review bundle | A grouped set of AI-proposed changes or actions that users can inspect before applying. |
| Human-in-the-loop | A safety model where the user remains responsible for approving meaningful AI-generated changes. |
| Local-first | A product principle where user work remains usable and durable on-device, with network services used only where they add clear value. |
| Product expression | A concrete implementation of CAOCAP's larger mission, such as the current iOS app or a future platform/version. |

### 1.7 References

- `README.md`: mission, public product framing, status, and devlog.
- `ROADMAP.md`: milestones, launch priorities, and future direction.
- `STRUCTURE.md`: current codebase architecture map.
- `CONTRIBUTING.md`: engineering standards and workflow.
- Feature README files for Canvas, CoCaptain, Auth, Omnibox, and Subscription.

### 1.8 Requirement Language

The requirement terms in this document follow these meanings:

- **Shall** means required for the named scope.
- **Should** means expected unless a documented product, technical, or compliance tradeoff overrides it.
- **May** means optional or future-facing.
- **Current product** means the shipped iOS and iPadOS implementation.
- **Future product expression** means a later version, platform, workflow, or interface that still serves the CAOCAP mission.

### 1.9 Requirement Attributes

Each requirement should be understood through four attributes, even when the short requirement ID does not restate them:

| Attribute | Meaning |
|---|---|
| Phase | The release horizon where the requirement matters most: P0, P1, P2, or P3. |
| Verification | The expected evidence type: manual QA, unit test, UI test, fixture, design review, legal review, privacy review, or product review. |
| Source | The reason the requirement exists: user workflow, launch readiness, platform policy, architecture constraint, safety model, or roadmap direction. |
| Owner | The functional owner expected to keep the requirement current: product, design, iOS, website, QA, compliance, or future platform team. |

When a requirement materially changes product scope, its phase, verification approach, and owner should be updated in the traceability matrix or linked implementation issue.

### 1.10 Release Phase Model

CAOCAP requirements are organized across release horizons so the movement remains ambitious without making every idea a launch blocker.

| Phase | Product Horizon | Primary Goal | Requirement Emphasis |
|---|---|---|---|
| P0 | Current shipped app and post-launch hardening | Preserve trust with real users and make the first project loop reliable. | Launch readiness, SRS clarity, persistence, preview reliability, CoCaptain review safety, auth, subscriptions, privacy, support. |
| P1 | Next SRS and CoCaptain loop | Make intent capture and AI collaboration feel meaningfully better than the old development process. | SRS states, clarifying questions, project analysis, requirement-to-code grounding, review bundles, conflict handling. |
| P2 | Runtime and interoperability | Make CAOCAP a stronger execution environment. | console output, debugging surfaces, export, templates, search, project portability. |
| P3 | Ecosystem and collaboration | Let people build together and extend CAOCAP. | sync, real-time collaboration, plugins, shared agentic history, cross-platform continuity. |

P0 requirements are launch-critical unless explicitly marked otherwise. P1-P3 requirements should guide design and architecture without blocking P0 stability.

### 1.11 Product State Vocabulary

CAOCAP shall use the following vocabulary when discussing product status:

| State | Meaning |
|---|---|
| Shipped | Available to real users through the App Store. |
| Post-launch hardening | Work that improves reliability, compliance, onboarding, support, or trust after release. |
| TestFlight readiness | Work required before beta distribution of a new build or feature path. |
| App Store update readiness | Work required before submitting an update to App Review. |
| Roadmap candidate | A scoped capability that has product value but is not yet committed to a release. |
| Experiment | A capability being explored without being treated as product doctrine. |

## 2. Overall Description

### 2.1 Product Perspective

CAOCAP is a software creation environment and product philosophy built around one belief: the way software is built can be made better.

The current product expression uses a spatial, node-based workspace because that model can make relationships between intent, implementation, and result more visible than a conventional file tree. The current product expression uses CoCaptain because AI can help compress the distance between idea and implementation when its work is grounded, reviewable, and under user control.

These implementation choices are important, but they are not the final definition of CAOCAP. Future product expressions may change the surface while preserving the mission: improve the act of building software.

### 2.2 Product Vision

CAOCAP shall become an environment where a person can move from an idea to working software with less fragmentation, less ceremony, and more direct feedback.

The product should help users:

- Capture intent before losing it.
- Turn intent into structured requirements.
- Connect requirements to implementation.
- See results quickly.
- Ask for help from an AI collaborator that understands the project.
- Review and control meaningful changes.
- Learn from the relationship between what they meant, what they built, and what actually runs.

The long-term product vision is not simply "a mobile code editor" or "a spatial IDE." It is a better software development process.

### 2.3 Problem Statement

Modern software development often splits intent, implementation, feedback, testing, collaboration, and release into disconnected tools and rituals. This fragmentation makes it harder for builders to maintain a clear mental model of:

- What they are building.
- Why it matters.
- Who it serves.
- Which requirements are satisfied.
- What changed.
- Whether the result actually matches the intent.

AI can accelerate this fragmentation if it hides reasoning, invents changes, or mutates projects without clear review. CAOCAP must solve the deeper problem: help users improve the software development process without losing authorship, control, or understanding.

### 2.4 Target Users

CAOCAP is intended for:

- Independent builders turning ideas into working prototypes.
- Students learning how requirements map to implementation and output.
- Designers and product thinkers who want a direct path from concept to interactive artifact.
- Developers who want faster feedback and clearer project context.
- Mobile-first creators building from iPhone or iPad.
- Small teams that need a shared understanding of intent, code, preview, and AI-assisted changes.
- Future contributors who see software tooling itself as an open design space.

### 2.5 User Goals

Users should be able to:

- Start with an idea and turn it into a structured SRS.
- Understand what is missing, vague, risky, or contradictory in their requirements.
- Generate or write code from requirements.
- Inspect and revise the code directly.
- Preview work immediately after meaningful changes.
- Ask CoCaptain for help grounded in the current project.
- Review AI-generated changes before they alter durable project state.
- Understand the connection between requirements, code, and output.
- Export or continue work outside CAOCAP when needed.
- Trust that privacy, ownership, billing, and account controls are clear.

### 2.6 Product Principles

CAOCAP shall follow these principles:

- No final form: today's development process is not the endpoint.
- Developer experience first: speed, clarity, agency, and creative flow are product requirements.
- Implementation is not identity: the current app is a vessel for the mission, not the limit of the mission.
- Intent before implementation: the product should help users clarify what they mean before and during building.
- Direct feedback: users should see the result of meaningful changes quickly.
- Human authority: AI may assist, but users remain in control of meaningful mutations.
- Inspectability: generated code, actions, assumptions, and changes should be visible where they matter.
- Local-first ownership: users should not lose work because of account, network, or AI availability.
- Progressive power: beginners should be able to start simply, while advanced users can go deeper.
- Launch quality: experimental ambition does not excuse weak privacy, compliance, reliability, or polish.

### 2.7 Product Scope

The CAOCAP product scope includes:

- A native iOS and iPadOS application.
- A first-class SRS experience for intent capture and refinement.
- A workspace for connected project artifacts.
- SRS, code, preview, runtime, assistant, and future debugging surfaces.
- CoCaptain as a context-aware AI collaborator.
- Human-in-the-loop review bundles for meaningful AI changes.
- Project persistence, templates, export, sharing, and future sync.
- Authentication and account management.
- Subscription-based Pro capabilities where appropriate.
- Public website pages for support, privacy, and terms.
- Future collaboration, plugins, templates, and platform expansion.

The product scope excludes unless explicitly added later:

- Treating any single interface pattern as the permanent definition of CAOCAP.
- Replacing professional desktop IDEs in every advanced workflow.
- Guaranteeing correctness or security of AI-generated code without user review.
- Making cloud storage mandatory for local project creation.
- Hiding implementation from users who want to inspect or edit code.

### 2.8 Core User Journeys

The following journeys define the product behaviors this SRS must support.

#### 2.8.1 Idea To Working Preview

1. User creates a new project.
2. CAOCAP opens a project with a structured SRS starting point.
3. User writes or structures intent.
4. User edits or asks CoCaptain to propose code.
5. CAOCAP stages meaningful AI changes as a review bundle.
6. User applies, rejects, or revises proposed changes.
7. Live Preview updates.
8. User compares the output against the SRS and iterates.

#### 2.8.2 Vague Idea To Clarified SRS

1. User writes an informal idea in the SRS.
2. CAOCAP identifies missing SRS sections or weak acceptance checks.
3. CoCaptain asks targeted clarifying questions before broad code generation.
4. User answers or edits directly.
5. CAOCAP updates readiness indicators without claiming the idea is finished.

#### 2.8.3 AI Patch Review

1. User asks CoCaptain to modify a project.
2. CoCaptain receives compact project context including the SRS.
3. CoCaptain returns visible explanation plus structured actions.
4. CAOCAP separates safe actions from review-required mutations.
5. CAOCAP presents review items with target node, summary, original text, proposed text, and conflict status.
6. User applies or rejects each review item.
7. CAOCAP prevents stale edits from overwriting newer user changes.

#### 2.8.4 First-Run Trust Loop

1. User installs and opens CAOCAP.
2. Onboarding explains that CAOCAP improves the development process through intent, implementation, feedback, and review.
3. User can create a first project without account friction.
4. User can find privacy, terms, support, restore purchases, account, and onboarding reset surfaces.
5. User can continue work after app relaunch.

#### 2.8.5 Failure Recovery

1. A save, preview, AI, auth, subscription, or project load operation fails.
2. CAOCAP keeps local project content safe.
3. CAOCAP shows a recovery path or non-destructive fallback.
4. User can continue editing where possible.

### 2.9 Product Decision Rubric

CAOCAP should prioritize work that satisfies at least one of these criteria:

- It shortens the path from intent to working software.
- It makes requirements, code, output, or AI changes more inspectable.
- It preserves user trust, privacy, ownership, or launch readiness.
- It makes a confusing development workflow more understandable.
- It improves feedback speed or quality.
- It reduces the chance of AI-generated changes surprising the user.

CAOCAP should reject or defer work when:

- It adds surface area without improving the development process.
- It hides important project state from the user.
- It weakens human review of meaningful AI mutations.
- It makes the current app less reliable for real users.
- It treats a novel interface as valuable without proving workflow value.

## 3. Product Context And Constraints

### 3.1 Platform Context

CAOCAP shall prioritize iOS and iPadOS as first-class platforms. The current product should feel native to touch, keyboard, trackpad, Apple Pencil where appropriate, and modern iPad multitasking patterns.

Future platform expansion may include Android, web, desktop, or shared project viewers. Future platforms may express CAOCAP differently, but they shall preserve the mission of improving the software development process.

### 3.2 Product Constraints

CAOCAP shall operate within these constraints:

- The current launch channel is the App Store and TestFlight ecosystem.
- User privacy, subscription disclosures, and account deletion flows must satisfy platform expectations.
- AI-assisted actions must preserve user control and avoid silent destructive edits.
- Core project work should remain durable on device.
- Public product language must consistently use CAOCAP.
- The product should remain understandable to users who have never seen the repository.

### 3.3 Assumptions

- Users may begin projects before creating or linking a permanent account.
- Users may work on prototypes, lessons, product concepts, tools, games, or larger multi-node projects.
- Users may not be fluent in traditional software requirements, so CAOCAP must help them structure intent.
- Users may not understand traditional file organization, so the workspace must teach itself through use.
- AI services may be unavailable, slow, or wrong.
- Subscription status may change outside the app and must be revalidated.
- Future collaboration and sync will require stronger identity, authorship, and conflict models.

### 3.4 Dependencies

CAOCAP may depend on:

- Apple platform capabilities for iOS, iPadOS, StoreKit, WebKit, accessibility, and system authentication.
- Firebase or equivalent services for authentication and AI infrastructure.
- AI model providers for CoCaptain responses.
- App Store Connect and Apple review requirements for distribution.
- Public website hosting for support, privacy, and terms pages.

## 4. Functional Requirements

### 4.1 Movement And Product Integrity

FR-MISSION-001: CAOCAP shall be specified as a product and movement dedicated to improving the software development process.

FR-MISSION-002: CAOCAP shall not define its long-term identity by any single feature, interface, platform, or implementation detail.

FR-MISSION-003: CAOCAP shall allow the current product expression to evolve when a different workflow better serves the mission.

FR-MISSION-004: CAOCAP shall keep public product language aligned around the CAOCAP name.

FR-MISSION-005: CAOCAP should evaluate new features by whether they improve the path from intent to working software.

FR-MISSION-006: CAOCAP should preserve the ability to experiment without making unstable experiments feel like final doctrine.

FR-MISSION-007: CAOCAP shall classify requirements and roadmap items by P0, P1, P2, or P3 release horizon when prioritization affects implementation order.

FR-MISSION-008: CAOCAP should document the user or workflow improvement expected from major new capabilities before implementation.

FR-MISSION-009: CAOCAP shall distinguish shipped behavior, post-launch hardening, roadmap candidates, and experiments in product planning and release notes where that distinction affects user expectations.

FR-MISSION-010: CAOCAP should avoid turning exploratory language into public promises until the related workflow is designed, feasible, and assigned to a release horizon.

### 4.2 SRS And Intent Capture

FR-SRS-001: CAOCAP shall treat requirements as first-class project content.

FR-SRS-002: CAOCAP shall provide an SRS experience for capturing product intent, target users, motivation, flow, functional requirements, acceptance checks, constraints, and open questions.

FR-SRS-003: CAOCAP shall support clear writing, editing, revision, and preservation of SRS content.

FR-SRS-004: CAOCAP shall make SRS content available to CoCaptain as project context when the user asks for project-specific assistance.

FR-SRS-005: CAOCAP should help users convert informal ideas into structured requirements without replacing the user's authorship.

FR-SRS-006: CAOCAP should identify missing sections, unclear assumptions, contradictions, scope gaps, and acceptance criteria gaps in SRS content.

FR-SRS-007: CAOCAP should make it possible to trace implementation artifacts back to the requirements they satisfy.

FR-SRS-008: CAOCAP should support one-tap or assisted SRS structuring that preserves existing drafts.

FR-SRS-009: CAOCAP should distinguish between requirement text, assumptions, decisions, acceptance checks, and future open questions.

FR-SRS-010: CAOCAP should support future SRS versioning, change history, and comparison when requirements evolve.

FR-SRS-011: CAOCAP shall avoid making the SRS a static document disconnected from code, preview, or agentic assistance.

FR-SRS-012: CAOCAP should allow CoCaptain to ask clarifying questions before generating code when the SRS is too vague.

FR-SRS-013: CAOCAP shall represent SRS readiness using explicit states: Empty, Draft, Structured, Needs Clarification, Implementation-Ready, and Stale Against Implementation.

FR-SRS-014: Empty state shall apply when the SRS contains no meaningful user-authored or template content.

FR-SRS-015: Draft state shall apply when the SRS contains intent but lacks one or more required structural sections.

FR-SRS-016: Structured state shall apply when the SRS contains intent, target user, flow, requirements, acceptance checks, and constraints sections.

FR-SRS-017: Needs Clarification state shall apply when required sections exist but contain unresolved placeholders, contradictions, or missing acceptance checks.

FR-SRS-018: Implementation-Ready state shall apply when the SRS has enough intent, flow, requirements, constraints, and acceptance checks for CoCaptain to propose targeted code changes.

FR-SRS-019: Stale Against Implementation state should apply when code or preview behavior appears to diverge from SRS acceptance checks or when significant implementation changes occur after the SRS was last updated.

FR-SRS-020: CAOCAP should expose the next useful SRS action for the current readiness state.

FR-SRS-021: CAOCAP shall never mark AI-generated or template SRS content as user-approved without user review or editing.

FR-SRS-022: CAOCAP should support SRS review prompts such as "What is missing?", "What changed?", and "What should be tested?".

FR-SRS-023: P0 SRS behavior shall include a structured default template, readiness indicators, draft preservation, persistence, and CoCaptain context inclusion.

FR-SRS-024: P1 SRS behavior should include clarifying-question loops, missing-context detection, and basic requirement-to-code grounding.

FR-SRS-025: SRS templates shall avoid implying that generated requirements are complete, correct, or user-approved before the user reviews them.

FR-SRS-026: CAOCAP should preserve an original draft or recoverable draft history when assisted structuring rewrites or reorganizes SRS content.

### 4.3 Workspace And Project Artifacts

FR-WORKSPACE-001: CAOCAP shall provide a workspace as the primary project interface.

FR-WORKSPACE-002: The current workspace shall allow users to view multiple project artifacts as nodes on a canvas.

FR-WORKSPACE-003: The current workspace shall allow users to pan, zoom, inspect, and navigate across project artifacts.

FR-WORKSPACE-004: The workspace shall preserve meaningful organization so users can return to prior project context.

FR-WORKSPACE-005: The workspace shall visually represent relationships between project artifacts where relationships improve understanding.

FR-WORKSPACE-006: The workspace shall support direct manipulation of artifacts, including selecting, moving, opening, and editing them.

FR-WORKSPACE-007: The workspace shall make the current project state legible through titles, icons, colors, previews, and connections.

FR-WORKSPACE-008: The workspace shall support both focused editing and broad project exploration.

FR-WORKSPACE-009: Future product expressions may use non-spatial or hybrid workspace models if they better serve the CAOCAP mission.

### 4.4 Node System

FR-NODE-001: CAOCAP shall model current project artifacts as nodes.

FR-NODE-002: Each node shall have stable identity, type, title, content, visual presentation, and optional relationships.

FR-NODE-003: CAOCAP shall support a requirements-oriented node type for capturing intent.

FR-NODE-004: CAOCAP shall support code-oriented node types for editable implementation content.

FR-NODE-005: CAOCAP shall support preview-oriented nodes for runnable output.

FR-NODE-006: CAOCAP shall support action-oriented nodes or controls for common project and navigation actions.

FR-NODE-007: CAOCAP should support future node types for console output, debugging, diagrams, assets, tests, documentation, API descriptions, deployments, and feedback.

FR-NODE-008: Nodes shall be able to express directed relationships when those relationships help explain flow, dependency, generation, execution, or review.

FR-NODE-009: Users shall be able to create new nodes through direct interaction, command surfaces, templates, or AI-assisted flows.

FR-NODE-010: CAOCAP shall use stable typed identifiers for node behavior where practical instead of fragile user-visible strings.

### 4.5 Code Editing

FR-CODE-001: CAOCAP shall allow users to edit source code inside code artifacts.

FR-CODE-002: CAOCAP shall support web prototype creation through HTML, CSS, and JavaScript editing in the current product expression.

FR-CODE-003: Code editing shall provide enough structure and visual feedback to remain usable on iOS and iPadOS.

FR-CODE-004: CAOCAP should support syntax-aware editing features appropriate to each supported language.

FR-CODE-005: CAOCAP should allow future expansion to additional languages, frameworks, file types, and runtime targets.

FR-CODE-006: Code edits shall be connected to project persistence and preview generation.

FR-CODE-007: CAOCAP shall not hide code from users who want to inspect or modify it directly.

FR-CODE-008: CAOCAP should make generated code explainable in relation to SRS intent.

### 4.6 Live Preview And Runtime

FR-RUNTIME-001: CAOCAP shall provide a live preview surface for runnable project output.

FR-RUNTIME-002: The preview shall update after relevant source changes according to the active phase's preview latency target.

FR-RUNTIME-003: The preview shall allow users to inspect output in embedded and focused modes.

FR-RUNTIME-004: CAOCAP shall combine relevant project artifacts into runnable output according to project type.

FR-RUNTIME-005: CAOCAP should support future runtime surfaces for console output, state inspection, variable flow, error messages, tests, and debugging overlays.

FR-RUNTIME-006: Runtime failures shall be surfaced in a way that helps users recover.

FR-RUNTIME-007: Preview execution shall not compromise app stability or user privacy.

FR-RUNTIME-008: Preview feedback should help users compare intended behavior with actual behavior.

FR-RUNTIME-009: P0 preview compilation should begin within 500ms after editing stops when the app is foregrounded and the project is within typical size limits.

FR-RUNTIME-010: Preview failures shall preserve the last editable source content and surface a recoverable error state.

### 4.7 CoCaptain Agentic Assistant

FR-AGENT-001: CAOCAP shall provide CoCaptain as an assistant that helps users understand, modify, and navigate projects.

FR-AGENT-002: CoCaptain shall use relevant workspace and SRS context when responding to project-specific requests.

FR-AGENT-003: CoCaptain shall be able to explain project structure, requirements, code, output, and likely next steps.

FR-AGENT-004: CoCaptain shall be able to propose changes to project artifacts.

FR-AGENT-005: CoCaptain shall group meaningful proposed changes into reviewable units before applying them.

FR-AGENT-006: CoCaptain shall not silently apply code edits or destructive project mutations without appropriate user approval.

FR-AGENT-007: CoCaptain may execute explicitly safe navigation or interface actions without review when the action is designed for autonomous execution.

FR-AGENT-008: CoCaptain shall distinguish between visible assistant text and hidden structured action data.

FR-AGENT-009: CoCaptain shall tolerate malformed or incomplete AI responses without corrupting project state.

FR-AGENT-010: CoCaptain shall detect or prevent stale AI edits from overwriting user changes made after the suggestion was generated.

FR-AGENT-011: CoCaptain should support multi-turn collaboration that preserves useful context.

FR-AGENT-012: CoCaptain should help transform a natural language prompt into a coherent project graph.

FR-AGENT-013: CoCaptain should support direct command interpretation for common app actions where local intent matching is safer or faster than model inference.

FR-AGENT-014: CoCaptain should challenge vague SRS input with clarifying questions before generating broad implementation changes.

FR-AGENT-015: CoCaptain should explain which SRS sections or requirements motivated a proposed change when practical.

FR-AGENT-016: CoCaptain structured payloads shall be parsed separately from visible assistant text.

FR-AGENT-017: A CoCaptain payload shall support safe actions, pending actions, and node edits as distinct categories.

FR-AGENT-018: Node edits shall identify target role, summary, ordered operations, and base text or equivalent conflict-detection data.

FR-AGENT-019: Review bundles shall include item title, target artifact, summary, original text or state, proposed text or state, source action, and conflict status where applicable.

FR-AGENT-020: CAOCAP shall block automatic execution of mutating actions unless the action is explicitly classified as safe for autonomous execution.

FR-AGENT-021: CAOCAP shall preserve a visible explanation when structured action parsing fails.

FR-AGENT-022: CoCaptain shall not receive secrets, tokens, credentials, billing identifiers, or unrelated personal data in prompt context.

FR-AGENT-023: CoCaptain should include SRS-derived reasoning in review summaries when a proposed change is based on requirements.

FR-AGENT-024: P0 CoCaptain behavior shall include context harvesting, structured action parsing, review bundles, safe-action separation, malformed payload fallback, and stale edit protection.

FR-AGENT-025: P1 CoCaptain behavior should include project analysis, clarifying questions, SRS quality review, and intent-to-node generation.

FR-AGENT-026: CoCaptain shall make it clear when it is explaining, proposing, executing a safe action, or waiting for user approval.

FR-AGENT-027: CoCaptain shall not present an unavailable or failed AI action as completed.

### 4.8 Project Creation And Templates

FR-PROJECT-001: CAOCAP shall allow users to create new projects.

FR-PROJECT-002: A new P0 project shall begin with a structured SRS artifact, editable implementation artifacts, a preview artifact, and visible relationships between them.

FR-PROJECT-003: New project templates shall include a useful SRS starting point when requirements improve the workflow.

FR-PROJECT-004: CAOCAP shall support named projects.

FR-PROJECT-005: CAOCAP shall persist project content and workspace state.

FR-PROJECT-006: CAOCAP shall allow users to reopen and continue prior projects.

FR-PROJECT-007: CAOCAP shall allow users to delete projects with appropriate safeguards.

FR-PROJECT-008: CAOCAP should provide a template library for common project types such as games, landing pages, tools, lessons, and experiments.

FR-PROJECT-009: Templates should include requirements, code, preview, and explanatory artifacts when those artifacts improve learning or speed.

FR-PROJECT-010: CAOCAP should allow CoCaptain to generate a project graph from user intent.

FR-PROJECT-011: P0 project creation shall create at least one SRS artifact, one editable implementation artifact, and one preview artifact.

FR-PROJECT-012: CAOCAP shall preserve project schema version information when project data is saved.

FR-PROJECT-013: CAOCAP shall provide a recovery path when persisted project data cannot be decoded.

### 4.9 Onboarding And Learning

FR-ONBOARD-001: CAOCAP shall provide onboarding that teaches the current workflow.

FR-ONBOARD-002: Onboarding shall introduce the relationship between intent, requirements, code, preview, workspace navigation, and CoCaptain.

FR-ONBOARD-003: Onboarding shall avoid forcing users through unnecessary friction once core concepts are understood.

FR-ONBOARD-004: Onboarding should use guided markers, action steps, and interaction gates where they improve comprehension.

FR-ONBOARD-005: Onboarding should be restartable from settings or help.

FR-ONBOARD-006: Onboarding shall not corrupt or unexpectedly persist tutorial-only workspace state into user projects.

FR-ONBOARD-007: Onboarding should teach that CAOCAP is about improving the development process, not merely using a canvas.

FR-ONBOARD-008: P0 onboarding shall allow users to reach first project creation without requiring sign-in.

FR-ONBOARD-009: P0 onboarding shall provide or link to a reset path for users who want to replay the tutorial.

### 4.10 Command And Navigation Surfaces

FR-COMMAND-001: CAOCAP shall provide fast access to common actions through command-oriented surfaces.

FR-COMMAND-002: Commands shall support project navigation, project creation, assistant access, settings, profile, subscription, help, and project exploration.

FR-COMMAND-003: Command surfaces shall route actions through a safety-aware execution boundary.

FR-COMMAND-004: Commands triggered by a user may perform configured actions immediately when user intent is clear.

FR-COMMAND-005: Commands triggered by an agent shall respect mutability and autonomous execution rules.

FR-COMMAND-006: CAOCAP should support natural language command aliases in supported languages when the alias is unlikely to conflict with ordinary chat.

### 4.11 Authentication And Account Management

FR-AUTH-001: CAOCAP shall allow users to begin using the app without losing work due to account friction.

FR-AUTH-002: CAOCAP shall support upgrading or linking a temporary session to a durable account where platform and provider rules allow.

FR-AUTH-003: CAOCAP shall support sign-in providers appropriate for the platform and product audience.

FR-AUTH-004: Account linking shall preserve user work whenever technically possible.

FR-AUTH-005: CAOCAP shall provide sign-out behavior that returns users to a valid state.

FR-AUTH-006: CAOCAP shall provide account deletion or deletion request flows that meet platform and privacy requirements.

FR-AUTH-007: CAOCAP shall surface account deletion failures, reauthentication requirements, or provider errors clearly.

FR-AUTH-008: CAOCAP shall keep privacy policy and terms links reachable from authentication surfaces.

### 4.12 Subscriptions And Entitlements

FR-SUB-001: CAOCAP may offer Pro capabilities through subscriptions.

FR-SUB-002: Subscription purchase flows shall use platform-approved payment systems where required.

FR-SUB-003: CAOCAP shall grant paid capabilities only after verified entitlement state.

FR-SUB-004: CAOCAP shall support restore purchases where platform rules require it.

FR-SUB-005: CAOCAP shall handle cancelled, pending, revoked, expired, and unverified transactions correctly.

FR-SUB-006: Subscription surfaces shall show accurate legal disclosures and links to terms and privacy pages.

FR-SUB-007: CAOCAP shall avoid presenting placeholder pricing as final truth when localized platform pricing is available.

FR-SUB-008: CAOCAP should design Pro capabilities so free users can understand the product's core value before subscribing.

FR-SUB-009: P0 subscription surfaces shall include restore purchase access, auto-renewal disclosure, price period, terms link, and privacy link where platform rules require them.

### 4.13 Export, Sharing, And Interoperability

FR-EXPORT-001: CAOCAP shall allow users to export project work in useful formats.

FR-EXPORT-002: CAOCAP should support exporting web projects as standard HTML, CSS, and JavaScript bundles.

FR-EXPORT-003: CAOCAP should support exporting or sharing a self-contained CAOCAP project bundle.

FR-EXPORT-004: CAOCAP should support future export to Git repositories or file-system structures.

FR-EXPORT-005: Exported projects shall preserve runnable source files and available SRS context so users can continue work outside CAOCAP.

FR-EXPORT-006: Sharing shall respect user intent, privacy, and platform permissions.

FR-EXPORT-007: Export should preserve SRS content where practical so intent can travel with implementation.

FR-EXPORT-008: Exported web bundles should include SRS content as documentation or metadata when the user chooses to include project context.

### 4.14 Collaboration And Sync

FR-COLLAB-001: CAOCAP should support future real-time collaboration in shared workspaces.

FR-COLLAB-002: Collaborative workspaces should show participant presence and activity where helpful.

FR-COLLAB-003: CAOCAP should support shared agentic history where it improves team understanding.

FR-COLLAB-004: CAOCAP should support cloud sync for users who choose cross-device continuity.

FR-COLLAB-005: Sync and collaboration shall include conflict handling appropriate to project artifacts and text content.

FR-COLLAB-006: Collaboration shall not make local project creation dependent on network availability.

FR-COLLAB-007: Collaboration should preserve the relationship between intent, decisions, code changes, and review history.

### 4.15 Plugin And Ecosystem Capabilities

FR-PLUGIN-001: CAOCAP should support a future plugin system for custom artifact types, runtime behaviors, templates, integrations, or agent capabilities.

FR-PLUGIN-002: Plugins shall operate within clear security and privacy boundaries.

FR-PLUGIN-003: Plugin capabilities shall be discoverable and understandable to users.

FR-PLUGIN-004: Plugins should not silently exfiltrate private project data.

FR-PLUGIN-005: CAOCAP should provide developer documentation for supported plugin APIs when the plugin system becomes public.

FR-PLUGIN-006: Plugins should be evaluated by whether they improve the software development process, not by novelty alone.

### 4.16 Website, Support, And Legal Surfaces

FR-WEB-001: CAOCAP shall provide public support information.

FR-WEB-002: CAOCAP shall provide a privacy policy page.

FR-WEB-003: CAOCAP shall provide a terms of service or terms of use page.

FR-WEB-004: App surfaces that reference support, privacy, or terms shall route users to valid public destinations.

FR-WEB-005: Legal and support pages shall use CAOCAP as the product name.

FR-WEB-006: Legal pages shall describe AI processing, subscriptions, account deletion, and project data handling accurately.

FR-WEB-007: Public support, privacy, and terms pages should be checked before each App Store update that changes account, AI, subscription, or project data behavior.

### 4.17 Future Platform Expansion

FR-PLATFORM-001: CAOCAP may expand beyond iOS and iPadOS when the product experience can remain coherent.

FR-PLATFORM-002: Future Android, web, or desktop versions shall preserve the core CAOCAP mission of improving the software development process.

FR-PLATFORM-003: Future platforms should preserve connected intent, implementation, feedback, and review even when interface details differ.

FR-PLATFORM-004: Cross-platform expansion shall not require renaming public product language away from CAOCAP.

FR-PLATFORM-005: Platform-specific implementations may differ when necessary, but user-owned project content should remain portable where practical.

## 5. Non-Functional Requirements

### 5.1 Product Integrity

NFR-INTEGRITY-001: CAOCAP shall keep the movement-level mission visible in product, documentation, onboarding, and roadmap language.

NFR-INTEGRITY-002: CAOCAP shall not let current implementation details narrow future product imagination unnecessarily.

NFR-INTEGRITY-003: CAOCAP shall make product changes traceable to user value, development-process improvement, launch readiness, or platform responsibility.

### 5.2 Usability

NFR-USABILITY-001: CAOCAP shall make the current workflow understandable without requiring users to read technical documentation first.

NFR-USABILITY-002: Common workflows shall be achievable with a small number of clear interactions.

NFR-USABILITY-003: The app shall support touch-first usage while remaining effective with keyboard and pointer input on iPadOS.

NFR-USABILITY-004: Writing requirements, editing code, previewing output, and asking CoCaptain for help shall feel like connected parts of one workflow.

NFR-USABILITY-005: Visual design shall prioritize clarity, polish, and direct manipulation over decorative complexity.

NFR-USABILITY-006: SRS guidance shall help users think more clearly without making them feel trapped in a rigid form.

NFR-USABILITY-006A: A first-time user shall be able to create a project and reach live preview within 10 interactions from app launch under normal onboarding conditions.

NFR-USABILITY-006B: Common single-intent actions such as opening the SRS, triggering CoCaptain, and creating a node shall be reachable within 3 interactions from the workspace.

### 5.3 Performance

NFR-PERFORMANCE-001: Workspace navigation shall remain responsive during typical project sizes.

NFR-PERFORMANCE-001A: For P0 projects up to 25 nodes and 100KB combined editable text, canvas pan and zoom should remain visually responsive during direct manipulation on currently supported devices.

NFR-PERFORMANCE-002: Text editing shall not be blocked by disk I/O, network requests, AI calls, or heavy preview work.

NFR-PERFORMANCE-003: Live preview updates shall occur quickly enough to preserve a sense of immediacy.

NFR-PERFORMANCE-003A: P0 live preview recompilation should be debounced at approximately 500ms after edit completion or pause.

NFR-PERFORMANCE-004: AI streaming shall provide visible progress when responses take noticeable time.

NFR-PERFORMANCE-004A: CoCaptain shall show a visible pending, streaming, or failure state when an AI response takes longer than 1 second.

NFR-PERFORMANCE-005: Background persistence and future sync shall avoid degrading foreground interaction.

### 5.4 Reliability

NFR-RELIABILITY-001: CAOCAP shall preserve user work across normal app lifecycle events.

NFR-RELIABILITY-002: CAOCAP shall recover gracefully from malformed project data where reasonable.

NFR-RELIABILITY-003: Failed AI responses shall not corrupt project content.

NFR-RELIABILITY-004: Failed purchases, auth errors, and network failures shall leave the app in a valid state.

NFR-RELIABILITY-005: CAOCAP shall make destructive actions explicit and recoverable where practical.

NFR-RELIABILITY-006: SRS content shall be persisted with the same care as code content.

NFR-RELIABILITY-007: CAOCAP shall not discard unsaved text editor changes when a preview, AI, auth, or subscription operation fails.

NFR-RELIABILITY-008: CAOCAP should keep enough project recovery information to distinguish between an empty project and a failed project decode.

NFR-RELIABILITY-008A: Project save shall complete within 2 seconds under normal device conditions for projects within the P0 size limit of 25 nodes and 100KB combined editable text.

NFR-RELIABILITY-008B: Project load shall complete and render the workspace within 3 seconds under normal device conditions for P0-size projects.

NFR-RELIABILITY-008C: CAOCAP shall attempt project recovery and present a user-visible result within 5 seconds of detecting a decode failure.

### 5.5 Privacy

NFR-PRIVACY-001: CAOCAP shall minimize collection of user project data.

NFR-PRIVACY-002: CAOCAP shall clearly disclose when project context is sent to AI services.

NFR-PRIVACY-003: CAOCAP shall not sell user project data.

NFR-PRIVACY-004: CAOCAP shall avoid logging sensitive authentication credentials, tokens, private project content, or personal data.

NFR-PRIVACY-005: Users shall have access to account deletion or deletion request mechanisms consistent with platform and legal requirements.

NFR-PRIVACY-006: Public privacy copy shall remain aligned with actual app behavior.

NFR-PRIVACY-006A: Privacy policy and terms links shall resolve to valid public URLs within 3 seconds on a standard network connection before each App Store update that modifies account, AI, or subscription behavior.

NFR-PRIVACY-006B: CAOCAP shall not transmit project content to AI services until the user explicitly initiates a CoCaptain action in the current session.

### 5.6 Security

NFR-SECURITY-001: CAOCAP shall use platform-standard authentication and authorization mechanisms.

NFR-SECURITY-002: Account linking shall protect against replay and credential misuse according to provider requirements.

NFR-SECURITY-003: AI-generated code shall be treated as untrusted until reviewed by the user.

NFR-SECURITY-004: Runtime preview execution shall be isolated as appropriate for the platform.

NFR-SECURITY-005: Subscription entitlements shall be based on verified platform transaction state.

NFR-SECURITY-006: Future plugin capabilities shall be sandboxed or permissioned to protect user projects.

NFR-SECURITY-006A: Authentication tokens shall not be logged, included in AI context, or stored in plaintext outside platform-secure storage.

NFR-SECURITY-006B: Subscription entitlement validation shall occur within 5 seconds of app foregrounding when entitlement state may have changed outside the app.

### 5.7 AI Safety And User Control

NFR-AISAFETY-001: CAOCAP shall keep users in control of meaningful project mutations proposed by AI.

NFR-AISAFETY-002: AI-generated suggestions shall be reviewable before application when they change code, project structure, identity state, billing state, or other durable content.

NFR-AISAFETY-003: CAOCAP shall detect stale AI proposals where practical before applying them.

NFR-AISAFETY-004: CAOCAP shall degrade gracefully when AI output is malformed, unsafe, incomplete, or unavailable.

NFR-AISAFETY-005: CAOCAP shall avoid presenting AI output as guaranteed correct, secure, or production-ready.

NFR-AISAFETY-006: CAOCAP should make AI assumptions visible when those assumptions materially affect generated requirements or code.

NFR-AISAFETY-006A: A review bundle shall be presented to the user within 2 seconds of a CoCaptain response completing when proposed node edits are present.

NFR-AISAFETY-006B: Stale edit detection shall execute synchronously before any review bundle item is applied and shall block application if the target node content has changed since the base text was captured.

### 5.8 Accessibility

NFR-ACCESSIBILITY-001: CAOCAP shall support platform accessibility features where practical, including VoiceOver, Dynamic Type, sufficient contrast, and reduced motion.

NFR-ACCESSIBILITY-002: Spatial interactions shall have accessible alternatives where possible.

NFR-ACCESSIBILITY-003: Important controls shall have meaningful labels.

NFR-ACCESSIBILITY-004: Color shall not be the only means of conveying critical information.

NFR-ACCESSIBILITY-005: Onboarding shall not rely solely on gestures that some users cannot perform.

NFR-ACCESSIBILITY-006: SRS readiness and section completion indicators shall not rely only on color.

NFR-ACCESSIBILITY-006A: All interactive controls shall have VoiceOver accessibility labels that identify the control's purpose without requiring visual context.

NFR-ACCESSIBILITY-006B: The app shall remain functionally usable at Dynamic Type size XXXL without content truncation or layout breakage in primary editing surfaces.

### 5.9 Localization And Internationalization

NFR-LOCALIZATION-001: CAOCAP should support English and Arabic as priority languages.

NFR-LOCALIZATION-002: Localized text shall preserve product meaning and safety-critical instructions.

NFR-LOCALIZATION-003: Command aliases and intent matching shall remain conservative across languages.

NFR-LOCALIZATION-004: UI layout shall account for language expansion and right-to-left needs where applicable.

NFR-LOCALIZATION-005: SRS templates and guidance should be localizable without changing requirement meaning.

### 5.10 Maintainability

NFR-MAINTAINABILITY-001: CAOCAP shall keep product requirements, roadmap, architecture, and implementation documentation aligned.

NFR-MAINTAINABILITY-002: Public-facing product language shall consistently use CAOCAP.

NFR-MAINTAINABILITY-003: Historical aliases shall not appear in public-facing requirements unless explicitly required for migration or legal context.

NFR-MAINTAINABILITY-004: Requirements shall be written so they can be traced to tests, design decisions, or roadmap items.

NFR-MAINTAINABILITY-005: New major features shall update the SRS or linked product requirements when they change product scope.

NFR-MAINTAINABILITY-006: SRS scaffold changes shall be tested because they influence new project context and AI behavior.

### 5.11 Scalability

NFR-SCALABILITY-001: CAOCAP shall support increasing project complexity without abandoning the mission of clarity and directness.

NFR-SCALABILITY-002: Project organization features shall help users manage many artifacts, relationships, templates, or collaborators.

NFR-SCALABILITY-003: Future sync and collaboration systems shall be designed for conflict handling and network variability.

NFR-SCALABILITY-004: Future plugin and ecosystem capabilities shall use stable interfaces rather than ad hoc integration points.

### 5.12 Compliance And App Store Readiness

NFR-COMPLIANCE-001: CAOCAP shall provide reachable privacy policy, terms, support, subscription, and account deletion information where required.

NFR-COMPLIANCE-002: Subscription copy shall satisfy platform requirements for auto-renewal disclosure, billing management, and restore behavior.

NFR-COMPLIANCE-003: App privacy disclosures shall match actual data collection and processing.

NFR-COMPLIANCE-004: AI processing disclosures shall explain what project context may be sent and why.

NFR-COMPLIANCE-005: CAOCAP shall pass the applicable readiness checklist before external TestFlight distribution of new builds or feature paths.

NFR-COMPLIANCE-006: P0 launch readiness shall include verification of account deletion, privacy links, terms links, support links, restore purchases, subscription disclosure, onboarding reset, first project creation, anonymous-to-linked account preservation, and AI context disclosure.

NFR-COMPLIANCE-007: CAOCAP shall pass the applicable launch readiness checklist before App Store update submission when the update changes onboarding, account, subscription, AI, persistence, privacy, or support behavior.

## 6. External Interface Requirements

### 6.1 User Interfaces

UI-001: CAOCAP shall provide a workspace-centered project interface.

UI-002: CAOCAP shall provide focused editors for requirements and code.

UI-003: CAOCAP shall provide preview surfaces for runnable output.

UI-004: CAOCAP shall provide CoCaptain chat and review surfaces.

UI-005: CAOCAP shall provide settings, profile, authentication, subscription, support, and legal surfaces.

UI-006: CAOCAP should provide future collaboration, template, plugin, export, and debugging surfaces.

UI-007: CAOCAP shall provide SRS guidance and readiness indicators where they improve intent capture.

UI-008: SRS readiness indicators shall show both overall state and missing or completed sections.

UI-009: Review bundle UI shall let users inspect proposed code changes before applying them.

### 6.2 Hardware Interfaces

HW-001: CAOCAP shall support touch input on iPhone and iPad.

HW-002: CAOCAP should support keyboard and pointer workflows on iPadOS.

HW-003: CAOCAP may support Apple Pencil interactions when they improve organization, annotation, or direct manipulation.

### 6.3 Software Interfaces

SW-001: CAOCAP shall use platform-supported web rendering for web preview experiences.

SW-002: CAOCAP shall use platform-supported subscription APIs for paid entitlements.

SW-003: CAOCAP shall use authentication providers through secure platform or provider SDKs.

SW-004: CAOCAP may use AI service providers to power CoCaptain.

SW-005: CAOCAP shall provide public website routes for support, terms, and privacy.

SW-006: CAOCAP should provide future export interfaces to standard project formats.

### 6.4 Communications Interfaces

COM-001: CAOCAP shall communicate with AI services only when user actions or enabled features require it.

COM-002: CAOCAP shall communicate with authentication services for account state and provider linking.

COM-003: CAOCAP shall communicate with subscription services for entitlement validation.

COM-004: CAOCAP should communicate with sync and collaboration services only when those features are enabled.

COM-005: Communication failures shall be surfaced or handled without corrupting local work.

## 7. Data Requirements

### 7.1 Project Data

DATA-PROJECT-001: CAOCAP project data shall include project identity, artifact content, relationships, workspace state, and metadata required to reopen the project.

DATA-PROJECT-002: Project data shall remain understandable enough to support export, migration, or recovery where practical.

DATA-PROJECT-003: Local project data shall not depend on an active AI session.

DATA-PROJECT-004: Future sync data shall preserve authorship, conflict, and update information where collaboration requires it.

DATA-PROJECT-005: Project data shall preserve SRS content as durable project context.

DATA-PROJECT-006: Project data shall include a schema version or migration marker.

DATA-PROJECT-007: Project data should preserve node identity, node type, node content, node relationships, viewport state, project name, and last modified metadata.

DATA-PROJECT-008: Project deletion shall distinguish between local deletion, account deletion, and future cloud deletion semantics.

### 7.2 User Data

DATA-USER-001: CAOCAP user data may include account identity, provider links, subscription entitlement state, preferences, and project ownership metadata.

DATA-USER-002: User data shall be handled according to published privacy terms.

DATA-USER-003: Sensitive user data shall not be exposed in diagnostics or AI prompts unless required for the requested feature and properly disclosed.

### 7.3 AI Context Data

DATA-AI-001: AI context shall include only the project information needed to answer or act on the user's request.

DATA-AI-002: CAOCAP shall avoid sending hidden credentials, secrets, tokens, or unrelated personal data to AI services.

DATA-AI-003: AI context construction should remain compact enough to support reliable model behavior.

DATA-AI-004: AI action outputs shall be validated before they can change durable project state.

DATA-AI-005: SRS content should be treated as high-value AI context because it explains user intent.

DATA-AI-006: AI context shall include project name, node inventory, canonical SRS/code content, and relevant relationship metadata when needed for the user request.

DATA-AI-007: AI context should be trimmed or summarized before provider limits are exceeded.

DATA-AI-008: AI outputs that propose durable project changes shall be stored or represented in a reviewable form before application.

## 8. Acceptance Criteria

### 8.1 Vision Acceptance

AC-VISION-001: A reviewer can read the SRS and understand CAOCAP as a movement and product dedicated to improving the software development process.

AC-VISION-002: A reviewer can understand that the current spatial and agentic implementation is important but not the final definition of CAOCAP.

AC-VISION-003: The SRS explains how intent, requirements, code, preview, and AI assistance fit into one product vision.

AC-VISION-004: The SRS uses CAOCAP consistently as the public product name.

### 8.2 SRS Acceptance

AC-SRS-001: Requirements identify SRS content as first-class project content.

AC-SRS-002: Requirements cover structured SRS sections, assisted structuring, missing-context detection, CoCaptain context, and traceability.

AC-SRS-003: Requirements preserve the user's authorship over generated or assisted SRS content.

### 8.3 Functional Acceptance

AC-FUNCTIONAL-001: Requirements cover product integrity, SRS editing, workspace, node system, code editing, live preview, CoCaptain, project lifecycle, onboarding, commands, auth, subscriptions, export, collaboration, plugins, website, and future platforms.

AC-FUNCTIONAL-002: Requirements distinguish user-approved mutations from safe autonomous actions.

AC-FUNCTIONAL-003: Requirements preserve local-first user ownership and future cloud capability.

### 8.4 Quality Acceptance

AC-QUALITY-001: Non-functional requirements cover product integrity, usability, performance, reliability, privacy, security, AI safety, accessibility, localization, maintainability, scalability, and compliance.

AC-QUALITY-002: Requirements are written in testable language where possible.

AC-QUALITY-003: Strategic or future-facing requirements are identifiable through terms such as "should", "may", or "future".

### 8.5 Documentation Acceptance

AC-DOC-001: The SRS is a Markdown document suitable for the repository.

AC-DOC-002: The SRS uses formal requirement IDs.

AC-DOC-003: The SRS can be reviewed in drafts without requiring code changes.

AC-DOC-004: The SRS remains aligned with `README.md`, `ROADMAP.md`, and `STRUCTURE.md` when those documents change product scope or architecture.

### 8.6 Scenario Acceptance

AC-SCENARIO-001: Given a fresh install, when a user completes or skips onboarding, then the user can create a first project without signing in and without losing the project after relaunch.

AC-SCENARIO-002: Given a new project, when the user opens the SRS artifact, then CAOCAP shows structured sections, readiness indicators, and a way to preserve or structure a draft.

AC-SCENARIO-003: Given an informal SRS draft, when the user triggers assisted structuring, then CAOCAP preserves the user's draft and adds missing sections without deleting user-authored text.

AC-SCENARIO-004: Given a project with SRS, HTML, CSS, JavaScript, and preview artifacts, when the user edits code and finishes or pauses editing, then the live preview recompiles within the P0 preview threshold where device conditions are normal.

AC-SCENARIO-005: Given a vague SRS, when the user asks CoCaptain to build a broad feature, then CoCaptain should ask clarifying questions or identify missing context before proposing broad code changes.

AC-SCENARIO-006: Given a CoCaptain response with node edits, when the structured payload is valid, then CAOCAP presents review items before mutating code.

AC-SCENARIO-007: Given a CoCaptain response with malformed structured data, when parsing fails, then CAOCAP preserves visible assistant text and does not corrupt project state.

AC-SCENARIO-008: Given a review bundle generated from older code, when the user edits the target node before applying it, then CAOCAP detects the stale edit or blocks unsafe application.

AC-SCENARIO-009: Given an anonymous user with local work, when the user links or upgrades an account, then CAOCAP preserves existing projects whenever provider rules allow.

AC-SCENARIO-010: Given a Pro subscription screen, when the user reviews purchase options, then CAOCAP shows restore purchase access, pricing period, terms, privacy link, and platform-required renewal disclosure.

AC-SCENARIO-011: Given AI services are unavailable, when the user asks CoCaptain for help, then CAOCAP surfaces a recoverable failure and preserves local project editing.

AC-SCENARIO-012: Given corrupted project data, when CAOCAP attempts to open the project, then CAOCAP avoids crashing and presents a recovery or fallback path.

### 8.7 P0 Launch Readiness Checklist

AC-LAUNCH-001: Privacy Policy link opens from app surfaces that reference privacy.

AC-LAUNCH-002: Terms link opens from app surfaces that reference terms.

AC-LAUNCH-003: Support link or support instructions are reachable.

AC-LAUNCH-004: Account deletion or deletion request flow is reachable and handles failure states.

AC-LAUNCH-005: Restore purchases is reachable from subscription surfaces.

AC-LAUNCH-006: Subscription copy includes platform-required auto-renewal, period, price, terms, and privacy information.

AC-LAUNCH-007: First project creation works after fresh install.

AC-LAUNCH-008: Onboarding can be completed and reset or replayed.

AC-LAUNCH-009: Anonymous work is preserved through supported account-linking flows.

AC-LAUNCH-010: AI context disclosure explains that project context may be sent to AI services when the user uses CoCaptain.

AC-LAUNCH-011: CoCaptain code edits require user review before application.

AC-LAUNCH-012: Project persistence survives app relaunch for the default SRS/code/preview project.

### 8.8 Verification Evidence

AC-EVIDENCE-001: Each P0 acceptance criterion should have an explicit verification record before a public release or App Store update that touches the related behavior.

AC-EVIDENCE-002: Manual QA evidence should identify device or simulator, OS version, app build, account state, network state, and whether the test used fresh install or upgraded app data.

AC-EVIDENCE-003: Automated tests should be preferred for parser behavior, patch conflict behavior, project persistence, schema migration, entitlement state, and deterministic SRS scaffold behavior.

AC-EVIDENCE-004: Legal, privacy, subscription, and AI disclosure changes should be reviewed against the shipped app behavior, not only against intended behavior.

AC-EVIDENCE-005: When a P0 criterion cannot be verified before release, the exception should identify the risk, owner, mitigation, and follow-up trigger.

## 9. Traceability Matrix

| Product Area | Requirement IDs | Validation Approach |
|---|---|---|
| Mission and identity | FR-MISSION-001 through FR-MISSION-010, AC-VISION-001 through AC-VISION-004, NFR-INTEGRITY-001 through NFR-INTEGRITY-003 | Docs review, product review, roadmap review |
| SRS and intent | FR-SRS-001 through FR-SRS-026, AC-SRS-001 through AC-SRS-003, AC-SCENARIO-002 through AC-SCENARIO-005 | Editor tests, scaffold tests, CoCaptain context tests, product review |
| Workspace | FR-WORKSPACE-001 through FR-WORKSPACE-009 | UI tests, gesture tests, design review |
| Node model | FR-NODE-001 through FR-NODE-010 | Unit tests, persistence tests, UX review |
| Code editing | FR-CODE-001 through FR-CODE-008 | Editor tests, syntax behavior review, device QA |
| Live preview and runtime | FR-RUNTIME-001 through FR-RUNTIME-010, AC-SCENARIO-004 | Compilation tests, WebView tests, runtime QA |
| CoCaptain | FR-AGENT-001 through FR-AGENT-027, NFR-AISAFETY-001 through NFR-AISAFETY-006, AC-SCENARIO-005 through AC-SCENARIO-008 | Parser tests, review bundle tests, conflict tests, safety review |
| Projects and templates | FR-PROJECT-001 through FR-PROJECT-013, AC-SCENARIO-001, AC-SCENARIO-012 | Persistence tests, template review, workflow QA, recovery tests |
| Onboarding | FR-ONBOARD-001 through FR-ONBOARD-009, AC-LAUNCH-007, AC-LAUNCH-008 | First-run QA, usability testing |
| Commands | FR-COMMAND-001 through FR-COMMAND-006 | Intent resolver tests, dispatcher tests |
| Auth | FR-AUTH-001 through FR-AUTH-008, AC-SCENARIO-009, AC-LAUNCH-004, AC-LAUNCH-009 | Auth flow tests, provider QA, compliance review |
| Subscriptions | FR-SUB-001 through FR-SUB-009, AC-SCENARIO-010, AC-LAUNCH-005, AC-LAUNCH-006 | StoreKit tests, entitlement tests, legal review |
| Export and sharing | FR-EXPORT-001 through FR-EXPORT-008 | Export fixtures, share workflow QA |
| Collaboration and sync | FR-COLLAB-001 through FR-COLLAB-007 | Future sync tests, conflict tests, multi-user QA |
| Plugins | FR-PLUGIN-001 through FR-PLUGIN-006 | Future sandbox tests, API review |
| Website and legal | FR-WEB-001 through FR-WEB-007, AC-LAUNCH-001 through AC-LAUNCH-003, AC-LAUNCH-010 | Link checks, legal copy review, privacy review |
| Platform expansion | FR-PLATFORM-001 through FR-PLATFORM-005 | Future platform review |
| Data and privacy | DATA-PROJECT-001 through DATA-AI-008, NFR-PRIVACY-001 through NFR-PRIVACY-006 | Privacy review, data flow review, logging audit |
| Release evidence | AC-EVIDENCE-001 through AC-EVIDENCE-005, NFR-COMPLIANCE-001 through NFR-COMPLIANCE-007 | Release checklist, QA notes, compliance review |

## 10. Review Process

### 10.1 Draft 1: Movement Foundation

Draft 1 should validate:

- CAOCAP product identity.
- Movement-level thesis.
- Problem statement.
- Target users.
- Product principles.
- Scope and success definition.
- Product decision rubric.
- Release phase model.

### 10.2 Draft 2: SRS And Functional Requirements

Draft 2 should validate:

- P0/P1/P2/P3 requirement classification.
- SRS and intent capture requirements.
- SRS readiness states.
- Major product capabilities.
- User journeys.
- Agentic safety model.
- CoCaptain payload and review bundle contracts.
- Future roadmap coverage.
- Requirement IDs and wording.

### 10.3 Draft 3: Non-Functional Requirements

Draft 3 should validate:

- Performance and reliability expectations.
- Measurable thresholds for P0 behavior.
- Privacy, security, and compliance requirements.
- Accessibility and localization requirements.
- Maintainability and scalability expectations.
- Product integrity expectations.
- Data recovery and schema expectations.

### 10.4 Draft 4: Final Specification Polish

Draft 4 should validate:

- Naming consistency.
- Traceability.
- Acceptance criteria.
- Scenario acceptance.
- Launch readiness checklist.
- Requirement testability.
- Glossary completeness.
- Alignment with roadmap and product vision.

## 11. Risk Register

| Risk ID | Risk | Impact | Mitigation |
|---|---|---|---|
| RISK-001 | CAOCAP's movement language becomes too broad to guide implementation. | Features may drift toward novelty instead of workflow value. | Require major work to map to a release phase, user journey, or decision-rubric criterion. |
| RISK-002 | SRS guidance becomes rigid paperwork rather than a thinking aid. | Users may avoid the SRS node or treat it as busywork. | Keep SRS states lightweight, preserve drafts, and make next actions practical rather than prescriptive. |
| RISK-003 | CoCaptain applies or appears to apply changes without enough user control. | Loss of trust, data loss, or App Store/privacy risk. | Preserve review bundles, safe-action classification, stale edit detection, and visible action state. |
| RISK-004 | AI context includes more project or personal data than needed. | Privacy risk and reduced user confidence. | Trim context, exclude secrets, disclose AI processing, and review prompt construction. |
| RISK-005 | Local project persistence fails or corrupted data is unrecoverable. | Users lose work, especially before account linking. | Use schema markers, atomic saves, decode recovery paths, and persistence fixtures. |
| RISK-006 | Subscription, account deletion, or legal links drift from platform requirements. | Review rejection, user confusion, or compliance exposure. | Re-run launch readiness checks before relevant TestFlight builds and App Store updates. |
| RISK-007 | Future sync, collaboration, export, or plugins are designed without enough ownership and conflict semantics. | Later architecture becomes fragile or unsafe. | Treat those areas as P2/P3 until data, permission, and conflict models are explicit. |

## 12. Open Questions

OQ-001 [RESOLVED]: Free capabilities at launch include the full spatial workspace, SRS authoring, HTML/CSS/JavaScript code editing, live preview, and basic project management. Pro capabilities include CoCaptain AI assistance, advanced project templates, and future export, collaboration, and sync features. The free/Pro boundary should be reviewed before each major roadmap milestone to ensure core workflow value is accessible without a subscription.

OQ-002 [RESOLVED]: Navigation to nodes and workspaces, opening and closing panels, reading project state, setting project name, creating new empty nodes with default content, and repositioning nodes on the canvas are safe for autonomous CoCaptain execution. Actions that mutate code content, SRS text, project structure, auth state, or subscription state shall always require user review.

OQ-003 [RESOLVED]: CAOCAP projects shall use a JSON envelope with a top-level schema version, project metadata, node array, relationship array, and viewport state. Binary assets shall be base64-encoded inline for P0 and migrated to external references at P2. The file extension shall be .caocap. The format shall be documented in STRUCTURE.md when stabilized.

OQ-004 [RESOLVED]: Text content conflicts in SRS and code nodes shall use last-write-wins with a visible conflict marker for P3 initial collaboration. Structural conflicts such as node creation, deletion, and relationship changes shall use server-authoritative ordering. Operational transforms or CRDTs shall be evaluated before general availability and this model revisited before P3 design begins.

OQ-005 [RESOLVED]: The first plugin capability class shall be custom node templates that supply initial node content as read-only factories. Plugins shall declare required capabilities at install time. Plugins shall not access other nodes' content, auth state, subscription state, or AI context without explicit user-granted permissions. Runtime sandboxing details belong in the plugin system design document.

OQ-006 [RESOLVED]: iPadOS multitasking and large-screen optimization is the immediate priority. macOS via Mac Catalyst is the next candidate for native desktop reach. A web-based read-only project viewer is a P2 candidate to support sharing without a native install. Android and a full-feature web editor are P3 candidates.

OQ-007 [RESOLVED]: When AI services are unavailable, CAOCAP shall allow full offline editing of SRS content, code nodes, and workspace organization. CoCaptain entry points shall indicate unavailability clearly. Previously received review bundles that have not been applied shall remain accessible offline. New AI requests shall fail gracefully with a retry option when connectivity returns.

OQ-008 [RESOLVED]: Traceability shall use lightweight inline tags. SRS requirements may reference node IDs or node roles. CoCaptain shall surface requirement IDs in review bundle summaries when a proposed change is motivated by a specific requirement. Full bidirectional traceability tooling is a P2 candidate and shall not block P0 work.

OQ-009 [RESOLVED]: CoCaptain may generate a full project graph when the SRS contains at minimum a product intent statement, at least one identified target user, at least three functional requirements, and at least one acceptance check, and the SRS readiness state is Structured or higher. CoCaptain shall ask clarifying questions rather than generating a graph when these minimums are not met.

OQ-010 [RESOLVED]: Initial proxy metrics include time-to-first-live-preview from project creation, percentage of new projects reaching Structured SRS state, review bundle acceptance rate, and CoCaptain session continuation rate. These are leading indicators. Qualitative feedback and usability testing remain primary evidence sources alongside these proxies.

OQ-011 [RESOLVED]: Automatic transitions: Empty to Draft when the user writes meaningful content; Draft to Needs Clarification when CAOCAP detects a missing required section or unresolved placeholder. User-confirmed transitions: Structured to Implementation-Ready requires user affirmation. Stale Against Implementation requires user acknowledgment to clear. CAOCAP shall never automatically transition to Implementation-Ready or clear Stale status without user action.

OQ-012 [RESOLVED]: See OQ-009. The minimums for a CoCaptain-generated multi-node graph proposal are an intent statement, an identified target user, three or more functional requirements, and one or more acceptance checks, with SRS readiness state at Structured or higher.

OQ-013 [RESOLVED]: Review bundles shall capture a base text snapshot of the target node content at the time the CoCaptain response is received. Before applying any node edit, CAOCAP shall perform a byte-exact comparison between the captured base text and the current node content. For nodes exceeding 50KB, a SHA-256 hash may be used as a fast pre-check before the full comparison. If content diverges, the edit shall be blocked and the user notified. This mechanism should be revisited if operational transforms or CRDT-based sync are introduced at P3.

OQ-014 [RESOLVED]: The first export format shall be a self-contained HTML/CSS/JavaScript bundle that reproduces the runnable project output. When the user opts to include project context, the bundle shall include the SRS content as a README.md file in the export root. This format preserves runnable code and intent without requiring CAOCAP to be installed.

OQ-015 [RESOLVED]: The following P0 criteria shall have automated test coverage before each App Store update that touches the related behavior: CoCaptain structured payload parsing and malformed payload fallback (FR-AGENT-016, FR-AGENT-021), node patch conflict detection (FR-AGENT-010), project save and load round-trip for the default SRS/code/preview project (FR-PROJECT-005, FR-PROJECT-006), project schema decode failure recovery (FR-PROJECT-013), and StoreKit entitlement state transitions (FR-SUB-003, FR-SUB-005). Manual QA evidence is required for onboarding reset, account deletion, restore purchases, and subscription disclosure before updates touching those surfaces.

## 13. Success Criteria

SC-001: Users can understand CAOCAP's central promise: today's software development process is not the final form.

SC-002: Users can begin with an idea, express requirements, edit code, preview output, and ask CoCaptain for help without leaving the core workflow.

SC-003: Users can see how intent, implementation, output, and AI-proposed changes relate to one another.

SC-004: Users remain in control of meaningful AI-generated project changes.

SC-005: CAOCAP reaches launch readiness with clear privacy, terms, support, subscription, onboarding, and account management behavior.

SC-006: The product can grow toward collaboration, plugins, export, sync, and platform expansion without defining itself by one interface or implementation.

SC-007: The SRS becomes a living product artifact that improves the quality of code generation, review, onboarding, and project understanding.

SC-008: P0 scenarios can be manually verified from a fresh install without hidden developer setup.

SC-009: CoCaptain can improve a project while preserving visible review, conflict detection, and user authorship.

SC-010: New major product work can be placed into a release phase and traced to a requirement, scenario, or launch checklist item.

## 14. Stakeholder Approval

This section records acknowledgment that the SRS reflects current product intent and is suitable for guiding implementation, QA, and compliance work for the named release phase.

| Role | Status | Date |
|---|---|---|
| Product | Pending | — |
| iOS Engineering | Pending | — |
| Design | Pending | — |
| QA | Pending | — |
| Compliance / Legal | Pending | — |

Approval indicates agreement that requirements are correct, complete enough to build against for the named phase, and consistent with the product vision. It does not imply P1–P3 requirements are finalized.

## 15. Appendix A: P0 Requirement Attributes

This table records Phase, Verification, Source, and Owner for all P0 requirements. P1–P3 requirements will be attributed when promoted to an active release phase.

| Requirement ID | Phase | Verification | Source | Owner |
|---|---|---|---|---|
| FR-SRS-001 through FR-SRS-004 | P0 | Product review, editor tests, CoCaptain context tests | User workflow | Product, iOS |
| FR-SRS-013 through FR-SRS-023 | P0 | Unit tests, scaffold tests, product review | User workflow, launch readiness | iOS, Product |
| FR-WORKSPACE-001 through FR-WORKSPACE-008 | P0 | UI tests, design review | User workflow | iOS, Design |
| FR-NODE-001 through FR-NODE-010 | P0 | Unit tests, persistence tests | Architecture constraint | iOS |
| FR-CODE-001 through FR-CODE-007 | P0 | Editor tests, device QA | User workflow | iOS |
| FR-RUNTIME-001 through FR-RUNTIME-004 | P0 | Compilation tests, WebView tests | User workflow | iOS |
| FR-RUNTIME-009 through FR-RUNTIME-010 | P0 | Performance tests, reliability tests | Launch readiness | iOS |
| FR-AGENT-001 through FR-AGENT-010 | P0 | Parser tests, review bundle tests, safety review | Safety model, launch readiness | iOS, Product |
| FR-AGENT-016 through FR-AGENT-024 | P0 | Parser tests, conflict tests, integration tests | Safety model | iOS |
| FR-PROJECT-001 through FR-PROJECT-007 | P0 | Persistence tests, workflow QA | User workflow, launch readiness | iOS |
| FR-PROJECT-011 through FR-PROJECT-013 | P0 | Persistence tests, recovery tests | Launch readiness | iOS |
| FR-ONBOARD-001 through FR-ONBOARD-009 | P0 | First-run QA, usability testing | Launch readiness | iOS, Design |
| FR-COMMAND-001 through FR-COMMAND-005 | P0 | Intent resolver tests, dispatcher tests | Architecture constraint | iOS |
| FR-AUTH-001 through FR-AUTH-008 | P0 | Auth flow tests, compliance review | Platform policy, launch readiness | iOS, Compliance |
| FR-SUB-001 through FR-SUB-009 | P0 | StoreKit tests, legal review | Platform policy, launch readiness | iOS, Compliance |
| FR-WEB-001 through FR-WEB-007 | P0 | Link checks, legal copy review | Platform policy, launch readiness | Website, Compliance |
| NFR-PERFORMANCE-001 through NFR-PERFORMANCE-005 | P0 | Performance tests, device QA | Launch readiness | iOS |
| NFR-RELIABILITY-001 through NFR-RELIABILITY-008C | P0 | Persistence tests, recovery tests | Launch readiness | iOS |
| NFR-PRIVACY-001 through NFR-PRIVACY-006B | P0 | Privacy review, logging audit | Platform policy, launch readiness | Compliance, iOS |
| NFR-SECURITY-001 through NFR-SECURITY-006B | P0 | Security review, auth QA | Platform policy | iOS, Compliance |
| NFR-USABILITY-001 through NFR-USABILITY-006B | P0 | Design review, usability testing | User workflow | Design |
| NFR-ACCESSIBILITY-001 through NFR-ACCESSIBILITY-006B | P0 | Accessibility audit, VoiceOver QA | Platform policy | iOS, Design |
| NFR-AISAFETY-001 through NFR-AISAFETY-006B | P0 | Safety review, parser tests | Safety model, launch readiness | iOS, Product |
| NFR-COMPLIANCE-001 through NFR-COMPLIANCE-007 | P0 | Release checklist, compliance review | Platform policy | Compliance, iOS |

## 16. Appendix B: Internal Interface Contracts

This appendix records behavioral contracts between major internal service components. Detailed method signatures belong in `STRUCTURE.md`.

### B.1 ProjectContextBuilder → LLMService

ProjectContextBuilder shall produce a compact context payload containing project name, SRS content, node inventory, active node content, and relationship metadata. The payload shall not include auth tokens, billing identifiers, or content from nodes unrelated to the current request. LLMService shall treat the payload as read-only input.

### B.2 LLMService → CoCaptainAgentParser

LLMService shall deliver a streaming text response and a completion signal. CoCaptainAgentParser shall parse structured action blocks from the response as they arrive. The parser shall tolerate partial or malformed blocks without throwing exceptions that corrupt caller state.

### B.3 CoCaptainAgentParser → CoCaptainAgentCoordinator

CoCaptainAgentParser shall deliver a typed payload containing visible assistant text, safe actions, pending review actions, and node edit operations as distinct collections. CoCaptainAgentCoordinator shall classify and route each category without merging safe and review-required actions.

### B.4 CoCaptainAgentCoordinator → NodePatchEngine

CoCaptainAgentCoordinator shall pass only review-approved node edits to NodePatchEngine. NodePatchEngine shall perform stale edit detection before applying each operation and shall return a conflict error rather than silently overwriting diverged content.

### B.5 AppActionDispatcher → ProjectStore and AppRouter

AppActionDispatcher shall route safe actions to ProjectStore or AppRouter through their published method interfaces. AppActionDispatcher shall not access internal store state directly. Mutating actions shall not be dispatched without prior CoCaptainAgentCoordinator approval.
