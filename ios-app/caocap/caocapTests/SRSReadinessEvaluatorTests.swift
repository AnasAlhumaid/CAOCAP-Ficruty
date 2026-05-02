import XCTest
@testable import caocap

final class SRSReadinessEvaluatorTests: XCTestCase {
    private let evaluator = SRSReadinessEvaluator()

    // MARK: - Empty

    func testEmptyTextReturnsEmpty() {
        XCTAssertEqual(evaluator.evaluate(text: "", currentState: nil), .empty)
    }

    func testWhitespaceOnlyReturnsEmpty() {
        XCTAssertEqual(evaluator.evaluate(text: "   \n  ", currentState: nil), .empty)
    }

    // MARK: - Draft

    func testContentWithNoSectionsReturnsDraft() {
        let text = "I want to build a task manager app."
        XCTAssertEqual(evaluator.evaluate(text: text, currentState: nil), .draft)
    }

    func testContentWithSomeSectionsReturnsDraft() {
        let text = """
        # Intent
        Build a focused tool.

        ## People
        - Primary user: solo developer
        """
        XCTAssertEqual(evaluator.evaluate(text: text, currentState: nil), .draft)
    }

    // MARK: - Structured / NeedsClarification

    func testAllSectionsWithPlaceholdersReturnsNeedsClarification() {
        // Build a text that has all 7 sections but leaves a colon-terminated placeholder.
        let text = allSectionsText(withPlaceholder: true)
        let result = evaluator.evaluate(text: text, currentState: nil)
        XCTAssertEqual(result, .needsClarification)
    }

    func testAllSectionsNoPlaceholdersNoFilledChecklistReturnsStructured() {
        // All sections present, no placeholders, but acceptance check is just a comment.
        let text = """
        # Intent
        Build a focused web app.

        ## Why It Matters
        Developers need faster feedback loops.

        ## People
        Primary user: solo developer working on mobile.

        ## Core Flow
        1. User opens the app.
        2. User creates a project.
        3. Live preview renders.

        ## Requirements
        The interface must be clear and responsive.

        ## Acceptance Checks
        Section added.

        ## Constraints
        Keep it small.
        """
        let result = evaluator.evaluate(text: text, currentState: nil)
        XCTAssertEqual(result, .structured)
    }

    // MARK: - Implementation-Ready

    func testAllSectionsFilledChecklistReturnsImplementationReady() {
        let text = allSectionsText(withPlaceholder: false)
        let result = evaluator.evaluate(text: text, currentState: nil)
        XCTAssertEqual(result, .implementationReady)
    }

    // MARK: - Stale

    func testStaleIsPreservedRegardlessOfText() {
        // Even if text is fully filled, stale must not be auto-cleared.
        let text = allSectionsText(withPlaceholder: false)
        XCTAssertEqual(evaluator.evaluate(text: text, currentState: .stale), .stale)
    }

    func testStaleIsPreservedOnEmptyText() {
        XCTAssertEqual(evaluator.evaluate(text: "", currentState: .stale), .stale)
    }

    // MARK: - Helpers

    private func allSectionsText(withPlaceholder: Bool) -> String {
        let acceptanceBlock = withPlaceholder
            ? "## Acceptance Checks\n- [ ] Acceptance criteria:"
            : "## Acceptance Checks\n- [ ] A first-time user understands the purpose in under 5 seconds."

        return """
        # Intent
        Build a focused web app that turns one clear idea into a working preview.

        ## Why It Matters
        Developers need faster, more direct feedback from their code.

        ## People
        Primary user: solo developer building a prototype.

        ## Core Flow
        1. User opens the app.
        2. User writes intent.
        3. Live preview renders.

        ## Requirements
        The interface must make the main action obvious.

        \(acceptanceBlock)

        ## Constraints
        Keep the first version small enough to ship today.
        """
    }
}
