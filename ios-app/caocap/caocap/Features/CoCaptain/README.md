# CoCaptain Feature

CoCaptain is the agentic assistant for Ficruty. It reads the current spatial project, streams model responses, executes safe app actions, and stages code changes for human review.

## Ownership

- `CoCaptainView` renders the timeline, input, streaming state, and review controls.
- `CoCaptainViewModel` owns presentation state, timeline items, streaming task lifetime, direct command handling, and review item application.
- `CoCaptainAgentCoordinator` orchestrates the model run: build context, stream text, parse structured actions, execute safe actions, and build review bundles.
- `CoCaptainAgentOutputAdapter` converts raw model output into a source-agnostic directive for the coordinator.
- `CoCaptainAgentParser` extracts the trailing structured payload from a `cocaptain-actions` fenced block.
- `CoCaptainAgentValidator` validates parsed payloads before any app action can execute.
- `CoCaptainAgentModels` defines timeline, review, action, and node edit domain models.

Supporting services live outside this feature:

- `ProjectContextBuilder` serializes the canvas for the model.
- `LLMService` streams from Firebase AI Logic.
- `AppActionDispatcher` performs high-level app actions.
- `NodePatchEngine` previews and applies exact node edits.

## Agent Flow

1. The user sends a message through `CoCaptainViewModel`.
2. Direct commands are resolved locally with `CommandIntentResolver` when possible.
3. Otherwise, `CoCaptainAgentCoordinator` builds project context from the active `ProjectStore`.
4. `LLMService` streams text back into the current assistant bubble.
5. `CoCaptainAgentOutputAdapter` hides machine output while streaming and turns the final response into a directive.
6. `CoCaptainAgentValidator` checks action IDs, action safety, node edit shape, and required agentic work.
7. Safe actions are executed immediately through `AppActionDispatcher` only after validation passes.
8. Mutating app actions and node edits become `ReviewBundleItem` entries.
9. Applying a review item revalidates the base node text before writing changes to `ProjectStore`.

The core contract is human-in-the-loop code editing. Do not auto-apply node edits without explicit user approval.

## Structured Payload Contract

The model may include one trailing XML block:

```xml
<cocaptain_actions>
  <assistant_message>Visible fallback text.</assistant_message>
  <safe_actions>
    <action id="id" />
  </safe_actions>
  <pending_actions>
    <action id="id" />
  </pending_actions>
  <node_edits>
    <node_edit role="html" summary="Update headline">
      <operation type="replace_all">
        <content><![CDATA[<h1>New text</h1>]]></content>
      </operation>
    </node_edit>
  </node_edits>
</cocaptain_actions>
```

Rules:

- The parser uses the last `cocaptain_actions` tag in the response.
- Malformed XML falls back to visible text with no payload.
- `safeActions` may only contain available, non-mutating, autonomous actions.
- `pendingActions` are shown for review before execution and are required for mutating or non-autonomous app actions.
- `nodeEdits` target `NodeRole` values and `NodePatchOperation` arrays.
- Node edits require a non-empty summary and at least one operation.
- Exact operations require a non-empty target.

Invalid structured payloads are not partially executed. The coordinator retries once with parse or validation feedback. If the retry is still invalid, the user sees a conflicted review item rather than a silent no-op or unsafe action.

Firebase function calling is the preferred path for app actions through the `request_app_action` tool. The XML block remains the compatibility format for node edits until structured-output node edit payloads replace it.

If this payload changes, update parser/coordinator tests and the prompt contract in `LLMService`.

## Review Safety

Node edits store their original `baseText` when the review bundle is created. On apply, the view model checks that the current node text still matches that base text before applying operations. This prevents silently overwriting user edits made after the model response.

Preserve this conflict guard when refactoring review state.

## Editing Guidance

- Keep UI rendering in `CoCaptainView`; keep timeline and async state in `CoCaptainViewModel`.
- Assistant chat bubbles may render Markdown for readable explanations, but raw structured payloads must stay hidden.
- Keep model orchestration in `CoCaptainAgentCoordinator`.
- Keep payload parsing deterministic and tolerant of malformed model output.
- Prefer adding new app capabilities through `AppActionDispatcher` and `AppActionID`.
- Add tests when changing parser fences, action classification, review item states, patch behavior, or retry behavior.
- Do not leak raw structured payload text into the visible chat timeline.
- Be careful with cancellation: closing the sheet cancels streaming and removes empty assistant messages.
- Keep validation near the coordinator boundary. SwiftUI views should render review state, not decide whether model output is safe.
- Keep raw model wire formats behind output adapters. The coordinator should consume directives, not Firebase/Gemini-specific response parts.
- Keep app actions in `request_app_action`; keep code/content changes in `nodeEdits`.

## Verification Checklist

- Send a normal chat message and confirm streaming text appears.
- Confirm assistant Markdown renders cleanly and message text can be selected or copied.
- Open the input plus menu and confirm quick prompts send once.
- Send a direct navigation command and confirm safe actions execute or review appears as expected.
- Ask for a code change and confirm review items are created rather than auto-applied.
- Apply a node edit and confirm the target node updates plus Live Preview recompiles.
- Modify a node after a review bundle is created, then apply the stale review item and confirm it conflicts.
- Switch projects while streaming and confirm the task cancels and history resets.

## Test Targets

Useful test coverage for this feature:

- parser success, malformed JSON fallback, and trailing fence behavior.
- coordinator safe action execution and review bundle generation.
- validator rejection for unknown actions, unsafe safe actions, unavailable pending actions, and empty node edit operations.
- function-call adapter mapping for safe actions, pending actions, malformed arguments, and mixed function-call + fenced node edits.
- node edit conflict handling when base text changes.
- direct command handling for autonomous vs review-required actions.
- retry behavior when agentic work is requested but no structured payload is returned.
- retry behavior when the structured payload is present but invalid.
