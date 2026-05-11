import SwiftUI

struct NodeView: View {
    let node: SpatialNode
    var isDragging: Bool = false
    @State private var isHovering = false
    let allNodes: [SpatialNode]
    var onUpdateChartX: ((Int?) -> Void)? = nil
    var onUpdateChartY: ((Int?) -> Void)? = nil
    @AppStorage(LocalizationManager.languageStorageKey) private var selectedLanguage = "English"
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .top, spacing: 16) {
                // Icon / Symbol
                if let icon = node.icon {
                    ZStack {
                        Circle()
                            .fill(themeColor.opacity(0.15))
                            .frame(width: 40, height: 40)
                        
                        Image(systemName: icon)
                            .font(.system(size: 20, weight: .semibold))
                            .foregroundColor(themeColor)
                    }
                }
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(node.displayTitle)
                        .font(.system(size: 18, weight: .bold, design: .rounded))
                        .foregroundColor(.primary)
                    
                    if let subtitle = node.displaySubtitle {
                        Text(subtitle)
                            .font(.system(size: 14, weight: .medium, design: .default))
                            .foregroundColor(.secondary)
                            .fixedSize(horizontal: false, vertical: true)
                            .lineLimit(3)
                    }

                    // Show SRS readiness badge for SRS nodes.
                    if node.type == .srs {
                        let state = node.srsReadinessState ?? .empty
                        HStack(spacing: 5) {
                            Image(systemName: state.icon)
                                .font(.system(size: 10, weight: .semibold))
                            Text(state.displayTitle)
                                .font(.system(size: 11, weight: .semibold, design: .rounded))
                        }
                        .foregroundColor(state == .stale ? .orange : themeColor)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background((state == .stale ? Color.orange : themeColor).opacity(0.12))
                        .clipShape(Capsule())
                        .padding(.top, 4)
                    }
                }
                .frame(maxWidth: 240, alignment: .leading)
            }
            .environment(\.layoutDirection, LocalizationManager.shared.layoutDirection(for: selectedLanguage))
            .padding(.bottom, node.type == .webView ? 16 : 0)
            
            if node.type == .webView, let html = node.htmlContent {
                HTMLWebView(htmlContent: html)
                    .frame(height: 340)
                    .background(Color.white.opacity(0.1))
                    .cornerRadius(12)
                    .padding(.top, 12)
            } else if node.type == .number, node.action == nil {
                VStack(alignment: .leading, spacing: 4) {
                    Text(node.textContent ?? "0")
                        .font(.system(size: 32, weight: .bold, design: .monospaced))
                        .foregroundColor(themeColor)
                    
                    Text("NUMBER VALUE")
                        .font(.system(size: 10, weight: .black))
                        .opacity(0.4)
                }
                .padding(.top, 12)
            } else if node.type == .text, node.action == nil {
                VStack(alignment: .leading, spacing: 6) {
                    Text(node.textContent ?? "Notes...")
                        .font(.system(size: 14, weight: .medium))
                        .lineLimit(4)
                        .foregroundColor(.primary)
                    
                    Text("NOTE PREVIEW")
                        .font(.system(size: 10, weight: .black))
                        .opacity(0.4)
                }
                .padding(.top, 12)
            } else if node.type == .table, node.action == nil {
                let rows = (node.textContent ?? "").components(separatedBy: "\n").filter { !$0.isEmpty }
                /// Wider canvas than 4 squeezed columns; horizontal scroll keeps cells readable.
                let maxPreviewColumns = 12
                let columnMinWidth: CGFloat = 76

                VStack(alignment: .leading, spacing: 0) {
                    ScrollView(.horizontal, showsIndicators: true) {
                        VStack(alignment: .leading, spacing: 0) {
                            ForEach(0..<min(rows.count, 5), id: \.self) { rowIndex in
                                let columns = rows[rowIndex].components(separatedBy: ",")
                                HStack(spacing: 0) {
                                    ForEach(0..<min(columns.count, maxPreviewColumns), id: \.self) { colIndex in
                                        Text(columns[colIndex].trimmingCharacters(in: .whitespaces))
                                            .font(.system(size: 10, weight: rowIndex == 0 ? .bold : .medium))
                                            .padding(6)
                                            .frame(minWidth: columnMinWidth, alignment: .leading)
                                            .background(rowIndex == 0 ? themeColor.opacity(0.15) : (rowIndex % 2 == 0 ? Color.black.opacity(0.05) : Color.clear))
                                            .border(Color.black.opacity(0.05), width: 0.5)
                                    }
                                }
                            }
                        }
                    }
                    .frame(maxWidth: 280)

                    if rows.count > 5 {
                        Text("+ \(rows.count - 5) more rows...")
                            .font(.system(size: 9, weight: .bold))
                            .foregroundColor(.secondary)
                            .padding(.top, 4)
                    }
                }
                .cornerRadius(8)
                .padding(.top, 12)
            } else if node.type == .calculation, node.action == nil {
                HStack(spacing: 12) {
                    Image(systemName: (node.operation ?? .add).icon)
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(themeColor)
                    
                    VStack(alignment: .leading, spacing: 2) {
                        Text(String(format: "%.1f", node.outputValue ?? 0.0))
                            .font(.system(size: 24, weight: .bold, design: .monospaced))
                            .foregroundColor(.primary)
                        
                        Text("COMPUTING \(node.operation?.rawValue ?? "+")")
                            .font(.system(size: 9, weight: .bold))
                            .opacity(0.5)
                    }
                }
                .padding(.top, 12)
            } else if node.type == .display, node.action == nil {
                VStack(spacing: 8) {
                    let value = node.outputValue ?? 0.0
                    let style = node.displayStyle ?? .number
                    
                    switch style {
                    case .number:
                        Text(String(format: "%.1f", value))
                            .font(.system(size: 48, weight: .black, design: .rounded))
                            .foregroundColor(themeColor)
                    
                    case .percentage:
                        Text("\(Int(value))%")
                            .font(.system(size: 48, weight: .black, design: .rounded))
                            .foregroundColor(themeColor)
                    
                    case .progress:
                        VStack(spacing: 12) {
                            ProgressView(value: min(max(value, 0), 100), total: 100)
                                .tint(themeColor)
                                .scaleEffect(x: 1, y: 4, anchor: .center)
                                .padding(.horizontal, 10)
                            
                            Text("\(Int(value))/100")
                                .font(.system(size: 14, weight: .bold, design: .monospaced))
                                .foregroundColor(themeColor)
                        }
                        .frame(height: 60)
                        
                    case .gauge:
                        ZStack {
                            Circle()
                                .stroke(themeColor.opacity(0.1), lineWidth: 12)
                            
                            Circle()
                                .trim(from: 0, to: CGFloat(min(max(value, 0), 100)) / 100.0)
                                .stroke(themeColor, style: StrokeStyle(lineWidth: 12, lineCap: .round))
                                .rotationEffect(.degrees(-90))
                            
                            Text("\(Int(value))")
                                .font(.system(size: 24, weight: .black, design: .rounded))
                                .foregroundColor(themeColor)
                        }
                        .frame(width: 80, height: 80)
                        .padding(.vertical, 8)
                    }
                    
                    Text(style.displayName.uppercased())
                        .font(.system(size: 10, weight: .black))
                        .opacity(0.4)
                }
                .padding(.top, 12)
            } else if node.type == .aiAgent, node.action == nil {
                VStack(alignment: .leading, spacing: 6) {
                    Label("AGENT OUTPUT", systemImage: "sparkles")
                        .font(.system(size: 10, weight: .black))
                        .opacity(0.4)
                    
                    Text(node.aiResponse ?? "Ready to process...")
                        .font(.system(size: 13, weight: .medium, design: .serif))
                        .foregroundColor(.primary)
                        .padding(10)
                        .background(themeColor.opacity(0.1))
                        .cornerRadius(8)
                }
                .padding(.top, 12)
            } else if node.type == .firebase, node.action == nil {
                VStack(alignment: .leading, spacing: 8) {
                    Label("FIREBASE (WEB)", systemImage: "flame.fill")
                        .font(.system(size: 10, weight: .black))
                        .opacity(0.4)

                    Text(FirebasePreviewBootstrap.canvasSummaryLine(for: node))
                        .font(.system(size: 13, weight: .medium))
                        .foregroundColor(.secondary)
                        .fixedSize(horizontal: false, vertical: true)
                }
                .padding(.top, 12)
            } else if node.type == .chart, node.action == nil {
                ChartNodeView(node: node, allNodes: allNodes, onUpdateX: onUpdateChartX, onUpdateY: onUpdateChartY)
            }

        }
        .fixedSize(horizontal: true, vertical: true)
        .padding(.horizontal, 20)
        .padding(.vertical, 20)
        .background(
            ZStack {
                RoundedRectangle(cornerRadius: 24, style: .continuous)
                    .fill(.ultraThinMaterial)
                
                RoundedRectangle(cornerRadius: 24, style: .continuous)
                    .fill(isDragging ? themeColor.opacity(0.08) : themeColor.opacity(0.03))
            }
            .shadow(
                color: Color.black.opacity(isDragging ? 0.25 : 0.15),
                radius: isDragging ? 30 : 20,
                x: 0,
                y: isDragging ? 20 : 10
            )
        )
        .overlay(
            RoundedRectangle(cornerRadius: 24, style: .continuous)
                .stroke(
                    LinearGradient(
                        colors: [
                            .white.opacity(isDragging ? 0.6 : 0.3),
                            .white.opacity(0.05),
                            themeColor.opacity(isDragging ? 0.6 : 0.3)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    ),
                    lineWidth: isDragging ? 2 : 1
                )
        )
        .scaleEffect(isDragging ? 1.05 : (isHovering ? 1.02 : 1.0))
        .background(
            GeometryReader { geo in
                Color.clear
                    .preference(
                        key: NodeFramePreferenceKey.self,
                        value: [node.id: NodeFrameData(nodeId: node.id, frame: geo.frame(in: .named("canvas")))]
                    )
            }
        )
        .animation(.spring(response: 0.3, dampingFraction: 0.7), value: isDragging)
        .animation(.spring(response: 0.3, dampingFraction: 0.7), value: isHovering)
    }
    
    private var themeColor: Color {
        node.theme.color
    }
}

#Preview {
    ZStack {
        Color(white: 0.05).ignoresSafeArea()
        NodeView(node: SpatialNode(
            position: .zero,
            title: "Hello, world!",
            subtitle: "Welcome to the future of agentic programming.",
            icon: "sparkles",
            theme: .purple
        ), allNodes: [])
    }
}
