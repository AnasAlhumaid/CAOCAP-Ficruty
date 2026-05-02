import SwiftUI

struct SRSEditorView: View {
    let node: SpatialNode
    let store: ProjectStore
    @Environment(\.dismiss) private var dismiss
    @State private var text: String

    init(node: SpatialNode, store: ProjectStore) {
        self.node = node
        self.store = store
        self._text = State(initialValue: node.textContent ?? "")
    }

    var body: some View {
        VStack(spacing: 0) {
            topBar

            Divider()

            readinessPanel

            Divider()

            editor
        }
        .background(Color(uiColor: .systemBackground).ignoresSafeArea())
        .safeAreaInset(edge: .bottom) {
            footerBar
        }
    }

    private var analysis: SRSAnalysis {
        SRSAnalysis(text: text, currentState: node.srsReadinessState)
    }

    private var topBar: some View {
        HStack(spacing: 12) {
            HStack(spacing: 10) {
                ZStack {
                    Circle()
                        .fill(node.theme.color.opacity(0.14))
                        .frame(width: 32, height: 32)

                    Image(systemName: "doc.text.fill")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(node.theme.color)
                }

                VStack(alignment: .leading, spacing: 2) {
                    Text("SRS")
                        .font(.system(size: 15, weight: .semibold, design: .rounded))
                        .foregroundColor(.primary)
                        .contextMenu {
                            Section("Aesthetics") {
                                ForEach(NodeTheme.allCases, id: \.self) { theme in
                                    Button {
                                        store.updateNodeTheme(id: node.id, theme: theme)
                                    } label: {
                                        Label(theme.rawValue.capitalized, systemImage: "circle.fill")
                                            .foregroundColor(theme.color)
                                    }
                                }
                            }
                            
                            Section("Transform") {
                                ForEach(NodeType.allCases, id: \.self) { type in
                                    if type != node.type {
                                        Button {
                                            store.updateNodeType(id: node.id, type: type)
                                        } label: {
                                            Label(type.displayName, systemImage: "arrow.triangle.2.circlepath")
                                        }
                                    }
                                }
                            }
                        }

                    Text(analysis.readinessState.displayTitle)
                        .font(.system(size: 11, weight: .medium))
                        .foregroundColor(.secondary)
                        .lineLimit(1)
                }
            }
            .padding(.leading, 20)

            Spacer(minLength: 8)

            Button(action: applyStructure) {
                Image(systemName: "checklist")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(node.theme.color)
                    .frame(width: 34, height: 34)
                    .background(node.theme.color.opacity(0.12))
                    .clipShape(Capsule())
            }
            .accessibilityLabel("Structure requirements")
            .help("Structure requirements")

            Button(action: saveAndDismiss) {
                Text("Done")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(.white)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 7)
                    .background(node.theme.color)
                    .clipShape(Capsule())
            }
            .padding(.trailing, 20)
        }
        .frame(height: 64)
        .background(Color(uiColor: .systemBackground))
    }

    private var readinessPanel: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 12) {
                Label(analysis.readinessState.displayTitle, systemImage: analysis.readinessState.icon)
                    .font(.system(size: 14, weight: .semibold, design: .rounded))
                    .foregroundColor(analysis.readinessState == .stale ? .orange : .primary)

                Spacer()

                Text("\(analysis.completedSections)/\(analysis.totalSections)")
                    .font(.system(size: 12, weight: .bold, design: .monospaced))
                    .foregroundColor(node.theme.color)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 5)
                    .background(node.theme.color.opacity(0.12))
                    .clipShape(Capsule())
            }

            ProgressView(value: analysis.completionRatio)
                .tint(node.theme.color)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(SRSScaffoldSection.allCases, id: \.self) { section in
                        SRSSectionChip(
                            title: section.title,
                            icon: section.icon,
                            isComplete: analysis.completedSectionsSet.contains(section),
                            tint: node.theme.color
                        )
                    }
                }
                .padding(.vertical, 2)
            }
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 14)
        .background(Color(uiColor: .secondarySystemBackground).opacity(0.65))
    }

    private var editor: some View {
        ZStack(alignment: .topLeading) {
            TextEditor(text: $text)
                .font(.system(size: 17, weight: .regular, design: .serif))
                .lineSpacing(8)
                .padding(.horizontal, 20)
                .padding(.top, 20)
                .scrollContentBackground(.hidden)
                .background(Color(uiColor: .systemBackground))

            if text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                Text("Name the intent. Define who it serves. State how success will be judged.")
                    .font(.system(size: 17, weight: .regular, design: .serif))
                    .foregroundColor(.secondary.opacity(0.72))
                    .lineSpacing(8)
                    .padding(.horizontal, 28)
                    .padding(.top, 28)
                    .allowsHitTesting(false)
            }
        }
    }

    private var footerBar: some View {
        VStack(spacing: 0) {
            Divider()

            HStack(spacing: 10) {
                SRSMetricPill(
                    icon: "text.word.spacing",
                    value: "\(analysis.wordCount)",
                    label: "Words",
                    tint: node.theme.color
                )

                SRSMetricPill(
                    icon: "checkmark.seal.fill",
                    value: "\(analysis.completedSections)",
                    label: "Sections",
                    tint: node.theme.color
                )

                Spacer(minLength: 8)

                Text(analysis.readinessState.nextAction)
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(.secondary)
                    .lineLimit(1)
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 10)
            .background(Color(uiColor: .systemBackground))
        }
    }

    private func applyStructure() {
        text = SRSScaffold.structuredText(from: text)
    }

    private func saveAndDismiss() {
        store.updateNodeTextContent(id: node.id, text: text, persist: true)
        dismiss()
    }
}

private struct SRSAnalysis {
    let wordCount: Int
    let completedSectionsSet: Set<SRSScaffoldSection>
    let missingSections: [SRSScaffoldSection]
    let readinessState: SRSReadinessState

    init(text: String, currentState: SRSReadinessState? = nil) {
        self.wordCount = text.split(whereSeparator: \.isWhitespace).count
        self.missingSections = SRSScaffold.missingSections(in: text)
        self.completedSectionsSet = Set(SRSScaffoldSection.allCases).subtracting(missingSections)
        self.readinessState = SRSReadinessEvaluator().evaluate(text: text, currentState: currentState)
    }

    var completedSections: Int {
        completedSectionsSet.count
    }

    var totalSections: Int {
        SRSScaffoldSection.allCases.count
    }

    var completionRatio: Double {
        guard totalSections > 0 else { return 0 }
        return Double(completedSections) / Double(totalSections)
    }
}

private struct SRSSectionChip: View {
    let title: String
    let icon: String
    let isComplete: Bool
    let tint: Color

    var body: some View {
        HStack(spacing: 6) {
            Image(systemName: isComplete ? "checkmark.circle.fill" : icon)
                .font(.system(size: 12, weight: .semibold))
                .foregroundColor(isComplete ? tint : .secondary)

            Text(title)
                .font(.system(size: 12, weight: .semibold, design: .rounded))
                .foregroundColor(isComplete ? .primary : .secondary)
                .lineLimit(1)
        }
        .padding(.horizontal, 10)
        .padding(.vertical, 7)
        .background((isComplete ? tint.opacity(0.12) : Color.secondary.opacity(0.08)))
        .clipShape(Capsule())
    }
}

private struct SRSMetricPill: View {
    let icon: String
    let value: String
    let label: String
    let tint: Color

    var body: some View {
        HStack(spacing: 5) {
            Image(systemName: icon)
                .font(.system(size: 11, weight: .semibold))

            Text(value)
                .font(.system(size: 12, weight: .bold, design: .monospaced))
                .monospacedDigit()

            Text(label)
                .font(.system(size: 12, weight: .medium))
        }
        .foregroundColor(tint)
        .padding(.horizontal, 9)
        .padding(.vertical, 6)
        .background(tint.opacity(0.12))
        .clipShape(Capsule())
    }
}
