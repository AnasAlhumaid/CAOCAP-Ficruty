import SwiftUI

struct ReviewBundleView: View {
    let bundle: ReviewBundleItem
    let viewModel: CoCaptainViewModel
    let bundleID: UUID

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text(bundle.title)
                    .font(.system(size: 16, weight: .bold))
                Spacer()
            }

            ForEach(bundle.items) { item in
                ReviewCardView(item: item) {
                    viewModel.applyReviewItem(bundleID: bundleID, itemID: item.id)
                } onReject: {
                    viewModel.rejectReviewItem(bundleID: bundleID, itemID: item.id)
                }
            }

            HStack(spacing: 16) {
                Spacer()
                Button("Apply All") {
                    viewModel.applyAll(in: bundleID)
                }
                .font(.system(size: 12, weight: .semibold))
                .disabled(!hasPendingItems)

                Button("Reject All") {
                    viewModel.rejectAll(in: bundleID)
                }
                .font(.system(size: 12, weight: .semibold))
                .foregroundColor(.red)
                .disabled(!hasPendingItems)
            }
            .padding(.top, 2)
        }
        .padding(14)
        .background(Color.primary.opacity(0.05))
        .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
    }

    private var hasPendingItems: Bool {
        bundle.items.contains { $0.status == .pending }
    }
}

struct ReviewCardView: View {
    let item: PendingReviewItem
    let onApply: () -> Void
    let onReject: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                Text(item.targetLabel)
                    .font(.system(size: 14, weight: .bold))
                Spacer()
                Text(item.status.localizedTitle)
                    .font(.system(size: 11, weight: .bold))
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(statusColor.opacity(0.14))
                    .foregroundColor(statusColor)
                    .clipShape(Capsule())
            }

            Text(item.summary)
                .font(.system(size: 13, weight: .medium))
                .foregroundColor(.secondary)

            if item.status == .conflicted, let reason = item.conflictDescription {
                HStack(alignment: .top, spacing: 6) {
                    Image(systemName: "exclamationmark.triangle.fill")
                        .font(.system(size: 11))
                        .foregroundColor(.orange)
                    Text(reason)
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.orange)
                        .fixedSize(horizontal: false, vertical: true)
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 8)
                .background(Color.orange.opacity(0.08))
                .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
            }

            Text(item.preview.isEmpty ? LocalizationManager.shared.localizedString("No preview available.") : item.preview)
                .font(.system(size: 12, weight: .regular, design: .monospaced))
                .padding(10)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color.black.opacity(0.06))
                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))

            HStack {
                Button("Apply") {
                    onApply()
                }
                .buttonStyle(.borderedProminent)
                .disabled(item.status != .pending)

                Button("Reject") {
                    onReject()
                }
                .buttonStyle(.bordered)
                .disabled(item.status != .pending)
            }
        }
        .padding(12)
        .background(Color.white.opacity(0.04))
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
    }

    private var statusColor: Color {
        switch item.status {
        case .pending: return .orange
        case .applied: return .green
        case .conflicted: return .red
        case .rejected: return .secondary
        }
    }
}
