import SwiftUI

/// Routes a selected node to the correct full-screen inspector/editor. Adding a
/// node type should usually update this router and the matching store/context
/// behavior together.
struct NodeDetailView: View {
    let node: SpatialNode
    let store: ProjectStore
    @Environment(\.dismiss) var dismiss
    @State private var isEditingTitle = false
    @State private var showingFilePicker = false
    @FocusState private var isTitleFocused: Bool
    
    private var currentNode: SpatialNode {
        store.nodes.first(where: { $0.id == node.id }) ?? node
    }
    
    private var eligibleInputNodes: [SpatialNode] {
        store.nodes.filter { other in
            other.id != currentNode.id && 
            [.text, .calculation, .number, .table, .aiAgent].contains(other.type)
        }
    }
    
    var body: some View {
        if currentNode.type == .webView {
            NavigationView {
                ZStack {
                    Color(uiColor: .systemBackground).ignoresSafeArea()
                    
                    if let html = currentNode.htmlContent {
                        HTMLWebView(htmlContent: html)
                            .ignoresSafeArea()
                    } else {
                        Text("No content to display.")
                            .foregroundColor(.gray)
                    }
                }
                .navigationTitle(currentNode.displayTitle)
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button("Done") {
                            dismiss()
                        }
                        .fontWeight(.semibold)
                    }
                }
            }
        } else if currentNode.type == .code {
            CodeEditorView(node: currentNode, store: store)
        } else if currentNode.type == .srs {
            SRSEditorView(node: currentNode, store: store)
        } else if currentNode.type == .art {
            ArtEditorView(node: currentNode, store: store)
        } else {
            NavigationView {
                ZStack {
                    // Background
                    themeColor.opacity(0.05).ignoresSafeArea()
                    
                    ScrollView {
                        VStack(alignment: .leading, spacing: 24) {
                            // Header Section
                            HStack(spacing: 20) {
                                if let icon = currentNode.icon {
                                    ZStack {
                                        Circle()
                                            .fill(themeColor.opacity(0.15))
                                            .frame(width: 80, height: 80)
                                        
                                        Image(systemName: icon)
                                            .font(.system(size: 32, weight: .bold))
                                            .foregroundColor(themeColor)
                                    }
                                }
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    HStack(spacing: 8) {
                                        if isEditingTitle {
                                            TextField("Node Name", text: Binding(
                                                get: { currentNode.title },
                                                set: { store.updateNodeTitle(id: node.id, title: $0) }
                                            ))
                                            .font(.system(size: 28, weight: .bold, design: .rounded))
                                            .focused($isTitleFocused)
                                            .submitLabel(.done)
                                            .onSubmit { isEditingTitle = false }
                                        } else {
                                            Text(currentNode.title)
                                                .font(.system(size: 28, weight: .bold, design: .rounded))
                                        }
                                        
                                        Button {
                                            isEditingTitle.toggle()
                                            isTitleFocused = isEditingTitle
                                        } label: {
                                            Image(systemName: isEditingTitle ? "checkmark.circle.fill" : "pencil.line")
                                                .font(.system(size: 18))
                                                .foregroundColor(isEditingTitle ? .green : .secondary)
                                                .opacity(0.8)
                                        }
                                    }
                                    
                                    Text(currentNode.type.displayName)
                                        .font(.system(size: 16, weight: .medium))
                                        .foregroundColor(.secondary)
                                }
                            }
                            .padding(.top, 20)
                            
                            Divider()
                            
                            // Content Editors
                            if currentNode.type == .number {
                                VStack(alignment: .leading, spacing: 12) {
                                    Label("Enter Value", systemImage: "number")
                                        .font(.headline)
                                        .foregroundColor(themeColor)
                                    
                                    TextField("0", text: Binding(
                                        get: { currentNode.textContent ?? "" },
                                        set: { store.updateNodeTextContent(id: node.id, text: $0) }
                                    ))
                                    .keyboardType(.decimalPad)
                                    .font(.system(size: 44, weight: .bold, design: .monospaced))
                                    .padding()
                                    .background(.ultraThinMaterial)
                                    .cornerRadius(16)
                                }
                            } else if currentNode.type == .text {
                                VStack(alignment: .leading, spacing: 12) {
                                    Label("Notes", systemImage: "text.alignleft")
                                        .font(.headline)
                                        .foregroundColor(themeColor)
                                    
                                    TextEditor(text: Binding(
                                        get: { currentNode.textContent ?? "" },
                                        set: { store.updateNodeTextContent(id: node.id, text: $0) }
                                    ))
                                    .frame(minHeight: 200)
                                    .padding(12)
                                    .background(.ultraThinMaterial)
                                    .cornerRadius(16)
                                }
                            } else if currentNode.type == .table {
                                VStack(alignment: .leading, spacing: 12) {
                                    HStack {
                                        Label("Table Data (CSV)", systemImage: "tablecells")
                                            .font(.headline)
                                            .foregroundColor(themeColor)
                                        Spacer()
                                        
                                        Button {
                                            showingFilePicker = true
                                        } label: {
                                            Label("Import CSV", systemImage: "square.and.arrow.down")
                                                .font(.caption.bold())
                                                .padding(.horizontal, 12)
                                                .padding(.vertical, 6)
                                                .background(themeColor.opacity(0.1))
                                                .cornerRadius(20)
                                        }
                                    }
                                    
                                    TextEditor(text: Binding(
                                        get: { currentNode.textContent ?? "" },
                                        set: { store.updateNodeTextContent(id: node.id, text: $0) }
                                    ))
                                    .font(.system(size: 12, design: .monospaced))
                                    .frame(minHeight: 150)
                                    .padding(8)
                                    .background(Color.black.opacity(0.05))
                                    .cornerRadius(12)
                                    
                                    let rows = (currentNode.textContent ?? "").components(separatedBy: "\n").filter { !$0.isEmpty }
                                    if !rows.isEmpty {
                                        Text("\(rows.count) rows found")
                                            .font(.caption2)
                                            .foregroundColor(.secondary)
                                    }
                                }
                                .fileImporter(
                                    isPresented: $showingFilePicker,
                                    allowedContentTypes: [.commaSeparatedText, .plainText],
                                    allowsMultipleSelection: false
                                ) { result in
                                    do {
                                        let urls = try result.get()
                                        if let url = urls.first {
                                            if url.startAccessingSecurityScopedResource() {
                                                let content = try String(contentsOf: url)
                                                store.updateNodeTextContent(id: node.id, text: content)
                                                url.stopAccessingSecurityScopedResource()
                                            }
                                        }
                                    } catch {
                                        print("File import failed: \(error)")
                                    }
                                }
                            } else if currentNode.type == .calculation {
                                VStack(alignment: .leading, spacing: 12) {
                                    Label("Operation", systemImage: "plus.forwardslash.minus")
                                        .font(.headline)
                                        .foregroundColor(themeColor)
                                    
                                    Picker("Operation", selection: Binding(
                                        get: { currentNode.operation ?? .add },
                                        set: { store.updateNodeOperation(id: node.id, operation: $0) }
                                    )) {
                                        ForEach(ArithmeticOperation.allCases, id: \.self) { op in
                                            Text(op.rawValue).tag(op)
                                        }
                                    }
                                    .pickerStyle(.segmented)
                                    .padding()
                                    .background(.ultraThinMaterial)
                                    .cornerRadius(16)
                                    
                                    Text("Computed Result: \(String(format: "%.1f", currentNode.outputValue ?? 0.0))")
                                        .font(.headline)
                                        .padding(.top, 8)
                                }
                            } else if currentNode.type == .display {
                                VStack(alignment: .leading, spacing: 16) {
                                    Label("Live Result", systemImage: "opticaldisc.fill")
                                        .font(.headline)
                                        .foregroundColor(themeColor)
                                    
                                    // Visual Display Picker
                                    Picker("Display Mode", selection: Binding(
                                        get: { currentNode.displayStyle ?? .number },
                                        set: { store.updateNodeDisplayStyle(id: node.id, style: $0) }
                                    )) {
                                        ForEach(DisplayStyle.allCases, id: \.self) { style in
                                            Text(style.displayName).tag(style)
                                        }
                                    }
                                    .pickerStyle(.segmented)
                                    .padding(.bottom, 8)

                                    Text(String(format: "%.1f", currentNode.outputValue ?? 0.0))
                                        .font(.system(size: 54, weight: .black, design: .monospaced))
                                        .foregroundColor(themeColor)
                                        .padding()
                                        .frame(maxWidth: .infinity, alignment: .center)
                                        .background(.ultraThinMaterial)
                                        .cornerRadius(16)
                                }
                            } else if currentNode.type == .aiAgent {
                                VStack(alignment: .leading, spacing: 12) {
                                    Label("AI Agent Prompt", systemImage: "brain.head.profile.fill")
                                        .font(.headline)
                                        .foregroundColor(themeColor)
                                    
                                    Text("Define how this agent processes its inputs. Use {{input1}} or {{Title}} to inject data.")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                    
                                    // The New Inline Token Editor
                                    TokenPromptEditor(
                                        text: Binding(
                                            get: { currentNode.promptTemplate ?? "" },
                                            set: { store.updateNodePrompt(id: node.id, prompt: $0) }
                                        ),
                                        nodes: store.nodes,
                                        themeColor: themeColor
                                    )
                                    .frame(minHeight: 120)
                                    .padding(12)
                                    .background(.ultraThinMaterial)
                                    .cornerRadius(16)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 16)
                                            .stroke(themeColor.opacity(0.3), lineWidth: 1)
                                    )
                                    
                                    // Quick Insert Tags
                                    let availableNodes = store.nodes.filter { $0.id != currentNode.id }
                                    if !availableNodes.isEmpty {
                                        VStack(alignment: .leading, spacing: 6) {
                                            Text("Quick Insert:")
                                                .font(.caption2.bold())
                                                .foregroundColor(.secondary)
                                            
                                            ScrollView(.horizontal, showsIndicators: false) {
                                                HStack(spacing: 8) {
                                                    ForEach(availableNodes) { otherNode in
                                                        Button {
                                                            let tag = "{{\(otherNode.title)}}"
                                                            let current = currentNode.promptTemplate ?? ""
                                                            store.updateNodePrompt(id: node.id, prompt: current + (current.isEmpty ? "" : " ") + tag)
                                                        } label: {
                                                            HStack(spacing: 4) {
                                                                Image(systemName: otherNode.icon ?? "square")
                                                                    .font(.system(size: 10))
                                                                Text(otherNode.title)
                                                            }
                                                            .font(.caption2.bold())
                                                            .padding(.horizontal, 10)
                                                            .padding(.vertical, 6)
                                                            .background(themeColor.opacity(0.2))
                                                            .foregroundColor(themeColor)
                                                            .clipShape(Capsule())
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    
                                    if let response = currentNode.aiResponse {
                                        VStack(alignment: .leading, spacing: 8) {
                                            Label("Agent Result", systemImage: "sparkles")
                                                .font(.subheadline.bold())
                                                .foregroundColor(themeColor)
                                            
                                            Text(response)
                                                .font(.system(size: 14, weight: .medium, design: .serif))
                                                .padding()
                                                .frame(maxWidth: .infinity, alignment: .leading)
                                                .background(themeColor.opacity(0.1))
                                                .cornerRadius(12)
                                        }
                                        .padding(.top, 8)
                                    }
                                    
                                    Button {
                                        store.evaluateAINode(id: node.id)
                                    } label: {
                                        Label("Run Agent", systemImage: "play.fill")
                                            .fontWeight(.bold)
                                            .frame(maxWidth: .infinity)
                                            .padding()
                                            .background(themeColor.opacity(0.2))
                                            .cornerRadius(12)
                                    }
                                }
                            } else if currentNode.type == .chart {
                                VStack(alignment: .leading, spacing: 20) {
                                    // Live Preview Header
                                    VStack(alignment: .leading, spacing: 8) {
                                        Label("LIVE PREVIEW", systemImage: "eye.fill")
                                            .font(.system(size: 10, weight: .black))
                                            .opacity(0.4)
                                        
                                        ChartNodeView(node: currentNode, allNodes: store.nodes, isScrollable: true)
                                            .frame(height: 240)
                                            .padding()
                                            .background(.ultraThinMaterial)
                                            .cornerRadius(20)
                                            .overlay(
                                                RoundedRectangle(cornerRadius: 20)
                                                    .stroke(themeColor.opacity(0.1), lineWidth: 1)
                                            )
                                    }
                                    .padding(.bottom, 8)

                                    Label("Chart Style", systemImage: "chart.line.uptrend.xyaxis")
                                        .font(.headline)
                                        .foregroundColor(themeColor)
                                    
                                    Picker("Style", selection: Binding(
                                        get: { currentNode.chartStyle ?? .bar },
                                        set: { store.updateNodeChartStyle(id: node.id, style: $0) }
                                    )) {
                                        ForEach(ChartStyle.allCases, id: \.self) { style in
                                            Label(style.displayName, systemImage: style.icon).tag(style)
                                        }
                                    }
                                    .pickerStyle(.segmented)
                                    .padding()
                                    .background(.ultraThinMaterial)
                                    .cornerRadius(16)
                                    
                                    // Visual Preview of data points
                                    let inputCount = currentNode.inputNodeIds?.count ?? 0
                                    Text("\(inputCount) active data streams")
                                        .font(.subheadline.bold())
                                        .foregroundColor(.secondary)
                                    
                                    Divider().padding(.vertical, 8)
                                    
                                    // Advanced Table Mapping (If a table is connected)
                                    if let tableNode = store.nodes.first(where: { 
                                        let sourceIds = Set(currentNode.inputNodeIds ?? [])
                                        let incomingStructuralIds = store.nodes.filter { other in
                                            other.nextNodeId == currentNode.id || (other.connectedNodeIds ?? []).contains(currentNode.id)
                                        }.map { $0.id }
                                        let allSourceIds = sourceIds.union(incomingStructuralIds)
                                        return allSourceIds.contains($0.id) && $0.type == .table 
                                    }) {
                                        VStack(alignment: .leading, spacing: 20) {
                                            Label("Data Source Configuration", systemImage: "tablecells.badge.ellipsis")
                                                .font(.headline)
                                                .foregroundColor(themeColor)
                                            
                                            // Header Toggle
                                            Toggle(isOn: Binding(
                                                get: { currentNode.chartHasHeaderRow ?? false },
                                                set: { store.updateNodeChartHasHeaderRow(id: currentNode.id, hasHeader: $0) }
                                            )) {
                                                VStack(alignment: .leading) {
                                                    Text("First row is header")
                                                        .font(.system(size: 16, weight: .medium))
                                                    Text("Skip the first row and use it for labels")
                                                        .font(.caption)
                                                        .foregroundColor(.secondary)
                                                }
                                            }
                                            .tint(themeColor)
                                            
                                            let headers = (tableNode.textContent ?? "").components(separatedBy: "\n").first?.components(separatedBy: ",") ?? []
                                            
                                            if !headers.isEmpty {
                                                VStack(alignment: .leading, spacing: 12) {
                                                    // X-Axis Picker
                                                    VStack(alignment: .leading, spacing: 8) {
                                                        Text("X-AXIS (LABELS)")
                                                            .font(.system(size: 10, weight: .black))
                                                            .opacity(0.4)
                                                        
                                                        Picker("X-Axis", selection: Binding(
                                                            get: { currentNode.chartXColumnIndex ?? 0 },
                                                            set: { store.updateNodeChartXColumn(id: currentNode.id, index: $0) }
                                                        )) {
                                                            ForEach(0..<headers.count, id: \.self) { index in
                                                                Text(headers[index].trimmingCharacters(in: .whitespaces)).tag(index)
                                                            }
                                                        }
                                                        .pickerStyle(.menu)
                                                        .padding(4)
                                                        .frame(maxWidth: .infinity, alignment: .leading)
                                                        .background(.ultraThinMaterial)
                                                        .cornerRadius(8)
                                                    }
                                                    
                                                    // Y-Axis Picker
                                                    VStack(alignment: .leading, spacing: 8) {
                                                        Text("Y-AXIS (VALUES)")
                                                            .font(.system(size: 10, weight: .black))
                                                            .opacity(0.4)
                                                        
                                                        Picker("Y-Axis", selection: Binding(
                                                            get: { currentNode.chartYColumnIndex ?? 1 },
                                                            set: { store.updateNodeChartYColumn(id: currentNode.id, index: $0) }
                                                        )) {
                                                            ForEach(0..<headers.count, id: \.self) { index in
                                                                Text(headers[index].trimmingCharacters(in: .whitespaces)).tag(index)
                                                            }
                                                        }
                                                        .pickerStyle(.menu)
                                                        .padding(4)
                                                        .frame(maxWidth: .infinity, alignment: .leading)
                                                        .background(.ultraThinMaterial)
                                                        .cornerRadius(8)
                                                    }
                                                }
                                            }
                                        }
                                        .padding()
                                        .background(themeColor.opacity(0.05))
                                        .cornerRadius(16)
                                    }
                                }
                            }

                            // Input Connections Section
                            if currentNode.type == .calculation || currentNode.type == .display || currentNode.type == .chart {
                                VStack(alignment: .leading, spacing: 12) {
                                    Label("Connect Inputs", systemImage: "link")
                                        .font(.headline)
                                        .foregroundColor(themeColor)
                                    
                                    if eligibleInputNodes.isEmpty {
                                        Text("No text or calculation nodes available to connect.")
                                            .font(.caption)
                                            .foregroundColor(.secondary)
                                    } else {
                                        ForEach(eligibleInputNodes) { otherNode in
                                            Button {
                                                var currentInputs = currentNode.inputNodeIds ?? []
                                                if currentInputs.contains(otherNode.id) {
                                                    currentInputs.removeAll(where: { $0 == otherNode.id })
                                                } else {
                                                    // Display nodes only support one input for simplicity
                                                    if currentNode.type == .display {
                                                        currentInputs = [otherNode.id]
                                                    } else {
                                                        currentInputs.append(otherNode.id)
                                                    }
                                                }
                                                store.updateNodeInputs(id: currentNode.id, inputNodeIds: currentInputs)
                                            } label: {
                                                HStack {
                                                    Image(systemName: otherNode.icon ?? "square")
                                                        .foregroundColor(otherNode.theme.color)
                                                    Text(otherNode.displayTitle)
                                                        .foregroundColor(.primary)
                                                    Spacer()
                                                    if (currentNode.inputNodeIds ?? []).contains(otherNode.id) {
                                                        Image(systemName: "checkmark.circle.fill")
                                                            .foregroundColor(themeColor)
                                                    }
                                                }
                                                .padding()
                                                .background(.ultraThinMaterial)
                                                .cornerRadius(12)
                                            }
                                        }
                                    }
                                }
                            }

                            // Aesthetics & Role Section (Only for protected/navigation nodes)
                            if currentNode.isProtected {
                                Section(header: Text("Configuration").font(.caption).foregroundStyle(.secondary)) {
                                    HStack(spacing: 12) {
                                        Picker("Theme", selection: Binding(
                                            get: { currentNode.theme },
                                            set: { store.updateNodeTheme(id: node.id, theme: $0) }
                                        )) {
                                            ForEach(NodeTheme.allCases, id: \.self) { theme in
                                                Circle().fill(theme.color).frame(width: 20, height: 20).tag(theme)
                                            }
                                        }
                                        .pickerStyle(.menu)
                                        .buttonStyle(.bordered)
                                        
                                        Picker("Role", selection: Binding(
                                            get: { currentNode.type },
                                            set: { store.updateNodeType(id: node.id, type: $0) }
                                        )) {
                                            ForEach(NodeType.allCases, id: \.self) { type in
                                                Text(type.displayName).tag(type)
                                            }
                                        }
                                        .pickerStyle(.menu)
                                        .buttonStyle(.bordered)
                                    }
                                }
                            }
                            
                            if !currentNode.isProtected {
                                Divider()
                                
                                Button(role: .destructive) {
                                    HapticsManager.shared.notification(.warning)
                                    store.deleteNode(id: node.id)
                                    dismiss()
                                } label: {
                                    Label("Delete Node", systemImage: "trash")
                                        .frame(maxWidth: .infinity)
                                        .padding()
                                        .background(Color.red.opacity(0.1))
                                        .cornerRadius(12)
                                }
                                .padding(.vertical)
                            }
                            
                            Spacer()
                        }
                        .padding(24)
                    }
                }
                .navigationTitle("Node Inspector")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button("Done") { dismiss() }.fontWeight(.semibold)
                    }
                }
            }
        }
    }
    
    private var themeColor: Color {
        currentNode.theme.color
    }
}

/// A high-performance editor that overlays an invisible TextEditor on a rich visual layer.
struct TokenPromptEditor: View {
    @Binding var text: String
    let nodes: [SpatialNode]
    let themeColor: Color
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            // Layer 1: Visual Rendering (Pills and Icons)
            // We use a fixed-width container to ensure wrapping matches the editor
            renderVisualLayer()
                .padding(.top, 8)
                .padding(.horizontal, 4)
            
            // Layer 2: The Actual Functional Editor (Transparent)
            TextEditor(text: $text)
                .font(.system(size: 16))
                .foregroundColor(.clear) // Text is handled by visual layer
                .scrollContentBackground(.hidden)
                .accentColor(themeColor) // Keep the cursor visible
        }
    }
    
    @ViewBuilder
    private func renderVisualLayer() -> some View {
        let segments = parseText(text)
        
        // We use wrapping Text for the visual layer to maintain perfect alignment
        segments.reduce(Text("")) { (result, segment) in
            switch segment {
            case .text(let str):
                return result + Text(str).font(.system(size: 16)).foregroundColor(.primary)
            case .tag(let tagName):
                if let matchedNode = findNode(by: tagName) {
                    // Styled Pill with Icon
                    return result + Text(" ") + Text(" \(matchedNode.icon ?? "tag") \(matchedNode.title) ")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(matchedNode.theme.color)
                        .baselineOffset(2)
                } else {
                    return result + Text(" ") + Text(" \(tagName) ")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(.secondary)
                        .baselineOffset(2)
                }
            }
        }
    }
    
    private enum Segment {
        case text(String)
        case tag(String)
    }
    
    private func parseText(_ input: String) -> [Segment] {
        var result: [Segment] = []
        let pattern = "\\{\\{(.*?)\\}\\}"
        let regex = try? NSRegularExpression(pattern: pattern, options: [])
        let nsString = input as NSString
        let matches = regex?.matches(in: input, options: [], range: NSRange(location: 0, length: nsString.length)) ?? []
        
        var lastIndex = 0
        for match in matches {
            let range = match.range
            if range.location > lastIndex {
                let text = nsString.substring(with: NSRange(location: lastIndex, length: range.location - lastIndex))
                result.append(.text(text))
            }
            
            let tagContent = nsString.substring(with: match.range(at: 1))
            result.append(.tag(tagContent))
            lastIndex = range.location + range.length
        }
        
        if lastIndex < nsString.length {
            let remaining = nsString.substring(from: lastIndex)
            result.append(.text(remaining))
        }
        
        return result
    }
    
    private func findNode(by title: String) -> SpatialNode? {
        let normalizedSearch = title.lowercased().replacingOccurrences(of: " ", with: "")
        return nodes.first { node in
            node.title.lowercased() == title.lowercased() ||
            node.title.lowercased().replacingOccurrences(of: " ", with: "") == normalizedSearch
        }
    }
}

struct DetailTag: View {
    let label: String
    let value: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(LocalizationManager.shared.localizedString(label).uppercased())
                .font(.system(size: 10, weight: .bold))
                .foregroundColor(.secondary)
            
            Text(value)
                .font(.system(size: 14, weight: .semibold))
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(Color.secondary.opacity(0.1))
                .cornerRadius(8)
        }
    }
}
