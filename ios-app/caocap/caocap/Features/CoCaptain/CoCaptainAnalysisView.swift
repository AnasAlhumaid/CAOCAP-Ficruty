import SwiftUI

/// A horizontal scroll of dismissible suggestion chips.
struct CoCaptainAnalysisView: View {
    let suggestions: [ProjectSuggestion]
    let onApply: (ProjectSuggestion) -> Void
    let onDismiss: (ProjectSuggestion) -> Void
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(suggestions) { suggestion in
                    SuggestionChip(
                        suggestion: suggestion,
                        onTap: { onApply(suggestion) },
                        onDismiss: { onDismiss(suggestion) }
                    )
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
        }
    }
}

private struct SuggestionChip: View {
    let suggestion: ProjectSuggestion
    let onTap: () -> Void
    let onDismiss: () -> Void
    
    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: suggestion.severity == .warning ? "exclamationmark.triangle.fill" : "sparkles")
                .font(.system(size: 12))
                .foregroundColor(suggestion.severity == .warning ? .orange : .blue)
            
            VStack(alignment: .leading, spacing: 0) {
                Text(suggestion.title)
                    .font(.system(size: 13, weight: .bold))
                Text(suggestion.detail)
                    .font(.system(size: 11))
                    .opacity(0.7)
                    .lineLimit(1)
            }
            
            Button(action: onDismiss) {
                Image(systemName: "xmark")
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(.secondary)
                    .padding(4)
            }
            .buttonStyle(.plain)
        }
        .padding(.leading, 12)
        .padding(.trailing, 8)
        .padding(.vertical, 8)
        .background {
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .fill(.ultraThinMaterial)
                .shadow(color: .black.opacity(0.1), radius: 4, x: 0, y: 2)
        }
        .overlay {
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .stroke(Color.white.opacity(0.1), lineWidth: 0.5)
        }
        .onTapGesture(perform: onTap)
    }
}
