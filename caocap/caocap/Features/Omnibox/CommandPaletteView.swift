import SwiftUI

struct CommandPaletteView: View {
    @ObservedObject var viewModel: CommandPaletteViewModel
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
                    
                    // Results List
                    ScrollViewReader { proxy in
                        ScrollView {
                            LazyVStack(spacing: 0) {
                                ForEach(Array(viewModel.filteredCommands.enumerated()), id: \.element.id) { index, command in
                                    CommandRow(
                                        command: command,
                                        isSelected: index == viewModel.selectedIndex
                                    ) {
                                        viewModel.executeCommand(command)
                                    }
                                    .id(index)
                                }
                            }
                        }
                        .frame(maxHeight: 400)
                        .onChange(of: viewModel.selectedIndex) { oldIndex, newIndex in
                            withAnimation {
                                proxy.scrollTo(newIndex, anchor: .center)
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

struct CommandRow: View {
    let command: Command
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Image(systemName: command.icon)
                    .font(.system(size: 16))
                    .frame(width: 24)
                
                Text(command.title)
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
