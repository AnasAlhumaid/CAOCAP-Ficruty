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
                    
                    // The view model owns the selected index so keyboard,
                    // submit, and pointer/touch selection all share one state.
                    ScrollViewReader { proxy in
                        ScrollView {
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
                        }
                        .frame(maxHeight: 400)
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

#Preview {
    ContentView()
}
