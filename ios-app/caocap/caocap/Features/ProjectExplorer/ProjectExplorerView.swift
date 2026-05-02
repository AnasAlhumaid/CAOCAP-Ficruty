import SwiftUI

struct ProjectExplorerView: View {
    @Environment(\.dismiss) private var dismiss
    var onSelect: (String) -> Void
    
    @State private var projects: [ProjectMetadata] = []
    @State private var isLoading = true
    
    var body: some View {
        NavigationStack {
            ZStack {
                Color(uiColor: .systemBackground).ignoresSafeArea()
                
                if isLoading {
                    ProgressView()
                } else if projects.isEmpty {
                    EmptyStateView()
                } else {
                    ProjectListView(projects: $projects, onSelect: { id in
                        onSelect(id)
                        dismiss()
                    }, onDelete: deleteProjects)
                }
            }
            .navigationTitle("Your Projects")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Done") { dismiss() }
                        .fontWeight(.bold)
                }
                
                ToolbarItem(placement: .topBarLeading) {
                    EditButton()
                }
            }
            .onAppear {
                loadProjects()
            }
        }
    }
    
    private func loadProjects() {
        projects = ProjectManager.shared.listProjects()
        isLoading = false
    }
    
    private func deleteProjects(at offsets: IndexSet) {
        for index in offsets {
            let project = projects[index]
            ProjectManager.shared.deleteProject(fileName: project.id)
        }
        projects.remove(atOffsets: offsets)
    }
}

// MARK: - Subviews

private struct EmptyStateView: View {
    var body: some View {
        VStack(spacing: 20) {
            Image(systemName: "folder.badge.questionmark")
                .font(.system(size: 64))
                .foregroundStyle(.secondary.opacity(0.3))
            
            Text("No Projects Found")
                .font(.headline)
            
            Text("Start a new project from the Home workspace to see it here.")
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 40)
        }
    }
}

private struct ProjectListView: View {
    @Binding var projects: [ProjectMetadata]
    let onSelect: (String) -> Void
    let onDelete: (IndexSet) -> Void
    
    var body: some View {
        List {
            ForEach(projects) { project in
                ProjectRow(project: project, action: { onSelect(project.id) })
                    .listRowBackground(Color.clear)
                    .listRowSeparatorTint(.primary.opacity(0.05))
            }
            .onDelete(perform: onDelete)
        }
        .listStyle(.plain)
    }
}

private struct ProjectRow: View {
    let project: ProjectMetadata
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                ZStack {
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(.purple.opacity(0.1))
                        .frame(width: 48, height: 48)
                    
                    Image(systemName: "parallax")
                        .foregroundStyle(.purple)
                        .font(.system(size: 20))
                }
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(project.name)
                        .font(.system(size: 17, weight: .bold))
                        .foregroundStyle(.primary)
                    
                    Text(
                        LocalizationManager.shared.localizedString(
                            "project.lastEditedDate",
                            arguments: [LocalizationManager.shared.relativeDateString(for: project.lastModified)]
                        )
                    )
                        .font(.system(size: 13))
                        .foregroundStyle(.secondary)
                }
                
                Spacer()
                
                Image(systemName: "chevron.forward")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundStyle(.tertiary)
            }
            .padding(.vertical, 8)
        }
    }
}

#Preview {
    ProjectExplorerView(onSelect: { _ in })
}
