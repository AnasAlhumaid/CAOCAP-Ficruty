import SwiftUI

/// Spotlight-style command surface. Rendering stays here while filtering,
/// selection, and execution callbacks live in `CommandPaletteViewModel`.
struct CommandPaletteView: View {
    @Bindable var viewModel: CommandPaletteViewModel
    @FocusState private var isFocused: Bool
    
    var body: some View {
        ZStack {
            if viewModel.isPresented {
                // Backdrop
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        viewModel.setPresented(false)
                    }
                    .transition(.opacity)
                
                // Palette
                VStack(spacing: 0) {
                    // Search Bar
                    HStack {
                        Image(systemName: "sparkles")
                            .font(.system(size: 20))
                            .foregroundColor(.blue)
                        
                        TextField("Search commands...", text: $viewModel.query)
                            .textFieldStyle(.plain)
                            .focused($isFocused)
                            .font(.system(size: 18, weight: .medium))
                            .submitLabel(.done)
                            .onSubmit {
                                viewModel.confirmSelection()
                            }
                    }
                    .padding(16)
                    
                    Divider()
                        .background(Color.white.opacity(0.1))
                    
                    ScrollViewReader { proxy in
                        ScrollView {
                            VStack(alignment: .leading, spacing: 0) {
                                if viewModel.currentPage == .createNode && viewModel.query.isEmpty {
                                    // NEW: Premium 2x2 Node Creation Grid
                                    VStack(alignment: .leading, spacing: 12) {
                                        Button {
                                            withAnimation { viewModel.currentPage = .main }
                                        } label: {
                                            HStack {
                                                Image(systemName: "chevron.left")
                                                Text("Select Node Type")
                                                    .font(.system(size: 20, weight: .bold))
                                            }
                                            .foregroundColor(.primary)
                                            .padding(.bottom, 4)
                                        }
                                        
                                        Text("Choose a specialized node for your spatial canvas")
                                            .font(.system(size: 13))
                                            .opacity(0.6)
                                            .padding(.bottom, 12)
                                        
                                        let columns = [
                                            GridItem(.flexible(), spacing: 16),
                                            GridItem(.flexible(), spacing: 16)
                                        ]
                                        
                                        LazyVGrid(columns: columns, spacing: 16) {
                                            ForEach(Array(viewModel.filteredActions.enumerated()), id: \.element.id) { index, action in
                                                AppActionGridItem(
                                                    item: action,
                                                    isSelected: index == viewModel.selectedIndex
                                                ) {
                                                    viewModel.executeAction(action)
                                                }
                                                .id(action.id.rawValue)
                                            }
                                        }
                                    }
                                    .padding(20)
                                    .transition(.move(edge: .trailing).combined(with: .opacity))
                                } else {
                                    // List Layout (Main View & Search Results)
                                    LazyVStack(spacing: 0) {
                                        ForEach(Array(viewModel.filteredActions.enumerated()), id: \.element.id) { index, action in
                                            AppActionRow(
                                                item: action,
                                                isSelected: index == viewModel.selectedIndex
                                            ) {
                                                viewModel.executeAction(action)
                                            }
                                            .id(action.id.rawValue)
                                        }

                                        if !viewModel.nodeResults.isEmpty {
                                            HStack {
                                                Text("CANVAS NODES")
                                                    .font(.system(size: 10, weight: .bold))
                                                    .opacity(0.4)
                                                Spacer()
                                            }
                                            .padding(.horizontal, 16)
                                            .padding(.top, 12)
                                            .padding(.bottom, 4)

                                            let actionCount = viewModel.filteredActions.count
                                            ForEach(Array(viewModel.nodeResults.enumerated()), id: \.element.id) { index, nodeResult in
                                                NodeSearchResultRow(
                                                    result: nodeResult,
                                                    isSelected: (index + actionCount) == viewModel.selectedIndex
                                                ) {
                                                    viewModel.flyToNode(nodeResult)
                                                }
                                                .id(nodeResult.id.uuidString)
                                            }
                                        }

                                        if viewModel.canSubmitPrompt {
                                            CoCaptainPromptRow(prompt: viewModel.query) {
                                                viewModel.submitPromptIfNeeded()
                                            }
                                            .id("cocaptain-prompt")
                                        }
                                    }
                                    .transition(.move(edge: .leading).combined(with: .opacity))
                                }
                            }
                        }
                        .frame(maxHeight: 480)
                        .onChange(of: viewModel.selectedIndex) { oldIndex, newIndex in
                            let actions = viewModel.filteredActions
                            let nodeResults = viewModel.nodeResults
                            
                            if newIndex >= 0 && newIndex < actions.count {
                                withAnimation {
                                    proxy.scrollTo(actions[newIndex].id.rawValue, anchor: .center)
                                }
                            } else if newIndex >= actions.count && newIndex < (actions.count + nodeResults.count) {
                                withAnimation {
                                    proxy.scrollTo(nodeResults[newIndex - actions.count].id.uuidString, anchor: .center)
                                }
                            }
                        }
                    }
                    
                    // Footer hint
                    HStack {
                        Text("Use arrows to navigate, Enter to select")
                            .font(.system(size: 10, weight: .light))
                            .opacity(0.5)
                        Spacer()
                    }
                    .padding(8)
                    .background(Color.black.opacity(0.05))
                }
                .background(.ultraThinMaterial)
                .frame(width: min(500, UIScreen.main.bounds.width - 40))
                .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                .overlay(
                    RoundedRectangle(cornerRadius: 16, style: .continuous)
                        .stroke(Color.white.opacity(0.2), lineWidth: 0.5)
                )
                .shadow(color: .black.opacity(0.5), radius: 30, x: 0, y: 15)
                .transition(.asymmetric(
                    insertion: .scale(scale: 0.9).combined(with: .opacity),
                    removal: .scale(scale: 0.9).combined(with: .opacity)
                ))
                .onAppear {
                    isFocused = true
                }
            }
        }
        .animation(Animation.spring(response: 0.3, dampingFraction: 0.8), value: viewModel.isPresented)
        .onChange(of: viewModel.isPresented) { oldPresented, newPresented in
            if newPresented {
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                    isFocused = true
                }
            }
        }
    }
}

struct AppActionRow: View {
    let item: AppActionDefinition
    let isSelected: Bool
    let onSelect: () -> Void
    
    var body: some View {
        Button(action: onSelect) {
            HStack(spacing: 12) {
                Image(systemName: item.icon)
                    .font(.system(size: 16))
                    .frame(width: 24)
                
                Text(item.localizedTitle)
                    .font(.system(size: 16))
                
                Spacer()
                
                if isSelected {
                    Image(systemName: "return")
                        .font(.system(size: 12))
                        .opacity(0.5)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(isSelected ? Color.blue.opacity(0.15) : Color.clear)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

struct CoCaptainPromptRow: View {
    let prompt: String
    let onSelect: () -> Void

    private var trimmedPrompt: String {
        prompt.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    var body: some View {
        Button(action: onSelect) {
            HStack(spacing: 12) {
                Image(systemName: "sparkles")
                    .font(.system(size: 16))
                    .frame(width: 24)

                VStack(alignment: .leading, spacing: 2) {
                    Text("Ask CoCaptain")
                        .font(.system(size: 16, weight: .medium))

                    Text(trimmedPrompt)
                        .font(.system(size: 12))
                        .lineLimit(1)
                        .truncationMode(.tail)
                        .opacity(0.65)
                }

                Spacer()

                Image(systemName: "return")
                    .font(.system(size: 12))
                    .opacity(0.5)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(Color.blue.opacity(0.15))
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
struct NodeSearchResultRow: View {
    let result: NodeSearchResult
    let isSelected: Bool
    let onSelect: () -> Void
    
    var body: some View {
        Button {
            onSelect()
        } label: {
            HStack(spacing: 12) {
                Image(systemName: result.role.icon)
                    .font(.system(size: 16))
                    .frame(width: 24)
                    .foregroundColor(result.role.themeColor)
                
                VStack(alignment: .leading, spacing: 2) {
                    Text(result.title)
                        .font(.system(size: 16, weight: .medium))
                    
                    if !result.snippet.isEmpty {
                        Text(result.snippet)
                            .font(.system(size: 12))
                            .lineLimit(1)
                            .truncationMode(.tail)
                            .opacity(0.6)
                    }
                }
                
                Spacer()
                
                if isSelected {
                    Image(systemName: "location.north.fill")
                        .font(.system(size: 12))
                        .opacity(0.5)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(isSelected ? Color.blue.opacity(0.15) : Color.clear)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

struct AppActionGridItem: View {
    let item: AppActionDefinition
    let isSelected: Bool
    let onSelect: () -> Void
    
    var body: some View {
        Button(action: onSelect) {
            VStack(spacing: 16) {
                // Large Circular Icon Container
                ZStack {
                    Circle()
                        .fill(iconColor.opacity(0.15))
                        .frame(width: 72, height: 72)
                    
                    Circle()
                        .stroke(iconColor.opacity(0.3), lineWidth: 2)
                        .frame(width: 72, height: 72)
                    
                    Image(systemName: item.icon)
                        .font(.system(size: 28))
                        .foregroundColor(iconColor)
                }
                .padding(.top, 8)
                
                VStack(spacing: 4) {
                    // Clean up the title (Remove "Create ")
                    Text(item.localizedTitle.replacingOccurrences(of: "Create ", with: ""))
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.primary)
                    
                    Text(description)
                        .font(.system(size: 11))
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                        .lineLimit(2)
                        .padding(.horizontal, 8)
                }
                .padding(.bottom, 8)
            }
            .frame(maxWidth: .infinity, minHeight: 160)
            .background(isSelected ? Color.blue.opacity(0.08) : Color.black.opacity(0.03))
            .cornerRadius(24)
            .overlay(
                RoundedRectangle(cornerRadius: 24)
                    .stroke(Color.white.opacity(0.1), lineWidth: 1)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
    
    private var description: String {
        let title = item.title
        if title.contains("Text") { return "General notes and markdown" }
        if title.contains("Calculation") { return "Mathematical logic and formulas" }
        if title.contains("Display") { return "Visual output and monitoring" }
        if title.contains("Number") { return "Numeric variable inputs" }
        if title.contains("Table") { return "Structured data entry" }
        if title.contains("AI") { return "Smart data processing" }
        return "Choose a specialized node"
    }
    
    private var iconColor: Color {
        // Match specific node creation actions to their theme colors
        let title = item.title
        if title.contains("Text") { return .blue }
        if title.contains("Calculation") { return .orange }
        if title.contains("Display") { return .purple }
        if title.contains("Number") { return .green }
        if title.contains("Table") { return .indigo }
        if title.contains("AI") { return .cyan }
        return .primary
    }
}

#Preview {
    ContentView()
}
