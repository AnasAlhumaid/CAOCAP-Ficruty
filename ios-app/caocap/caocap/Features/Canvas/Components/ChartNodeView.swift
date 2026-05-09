import SwiftUI
import Charts

struct ChartDataPoint: Identifiable {
    let id = UUID()
    let label: String
    let value: Double
    let color: Color
}

struct ChartNodeView: View {
    let node: SpatialNode
    let allNodes: [SpatialNode]
    var isScrollable: Bool = false
    var onUpdateX: ((Int?) -> Void)? = nil
    var onUpdateY: ((Int?) -> Void)? = nil
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Column Selectors (Only shown if a table is connected)
            if let tableNode = connectedTableNode {
                HStack(spacing: 8) {
                    let headers = getHeaders(for: tableNode)
                    
                    // X-Axis Selector
                    Menu {
                        ForEach(0..<headers.count, id: \.self) { index in
                            Button {
                                onUpdateX?(index)
                            } label: {
                                HStack {
                                    Text(headers[index])
                                    if node.chartXColumnIndex == index {
                                        Image(systemName: "checkmark")
                                    }
                                }
                            }
                        }
                        Divider()
                        Button("Default") { onUpdateX?(nil) }
                    } label: {
                        HStack(spacing: 4) {
                            Text("X: \(node.chartXColumnIndex != nil ? headers[node.chartXColumnIndex!] : "Auto")")
                            Image(systemName: "chevron.down")
                        }
                        .font(.system(size: 10, weight: .bold))
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(Color.primary.opacity(0.05))
                        .cornerRadius(6)
                    }
                    
                    // Y-Axis Selector
                    Menu {
                        ForEach(0..<headers.count, id: \.self) { index in
                            Button {
                                onUpdateY?(index)
                            } label: {
                                HStack {
                                    Text(headers[index])
                                    if node.chartYColumnIndex == index {
                                        Image(systemName: "checkmark")
                                    }
                                }
                            }
                        }
                        Divider()
                        Button("Auto") { onUpdateY?(nil) }
                    } label: {
                        HStack(spacing: 4) {
                            Text("Y: \(node.chartYColumnIndex != nil ? headers[node.chartYColumnIndex!] : "Auto")")
                            Image(systemName: "chevron.down")
                        }
                        .font(.system(size: 10, weight: .bold))
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(Color.primary.opacity(0.05))
                        .cornerRadius(6)
                    }
                    
                    Spacer()
                }
                .transition(.opacity.combined(with: .move(edge: .top)))
            }
            
            let data = chartData
            
            if data.isEmpty {
                VStack(spacing: 8) {
                    Image(systemName: "chart.line.uptrend.xyaxis")
                        .font(.system(size: 32))
                        .foregroundColor(node.theme.color.opacity(0.3))
                    
                    Text("No numeric data found")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity)
                .frame(height: 140)
                .background(Color.black.opacity(0.05))
                .cornerRadius(12)
            } else {
                let minWidth: CGFloat = 280
                let dynamicWidth = max(minWidth, CGFloat(data.count) * 60)
                
                Group {
                    if isScrollable {
                        ScrollView(.horizontal, showsIndicators: false) {
                            chartContent(width: dynamicWidth)
                        }
                    } else {
                        chartContent(width: dynamicWidth)
                    }
                }
            }
            
            HStack {
                Label(node.chartStyle?.displayName.uppercased() ?? "CHART", systemImage: node.chartStyle?.icon ?? "chart.xyaxis.line")
                    .font(.system(size: 10, weight: .black))
                    .opacity(0.4)
                
                Spacer()
                
                Text("\(data.count) DATA SOURCES")
                    .font(.system(size: 9, weight: .bold))
                    .opacity(0.3)
            }
        }
        .padding(.top, 12)
    }
    
    @ViewBuilder
    private func chartContent(width: CGFloat) -> some View {
        let data = chartData
        Chart {
            ForEach(data) { point in
                switch node.chartStyle ?? .bar {
                case .bar:
                    BarMark(
                        x: .value("Category", point.label),
                        y: .value("Value", point.value)
                    )
                    .foregroundStyle(point.color.gradient)
                    .cornerRadius(4)
                    
                case .line:
                    LineMark(
                        x: .value("Category", point.label),
                        y: .value("Value", point.value)
                    )
                    .foregroundStyle(point.color.gradient)
                    .interpolationMethod(.catmullRom)
                    .lineStyle(StrokeStyle(lineWidth: 3))
                    
                    PointMark(
                        x: .value("Category", point.label),
                        y: .value("Value", point.value)
                    )
                    .foregroundStyle(point.color)
                    
                case .area:
                    AreaMark(
                        x: .value("Category", point.label),
                        y: .value("Value", point.value)
                    )
                    .foregroundStyle(point.color.opacity(0.3).gradient)
                    .interpolationMethod(.catmullRom)
                    
                    LineMark(
                        x: .value("Category", point.label),
                        y: .value("Value", point.value)
                    )
                    .foregroundStyle(point.color)
                    .interpolationMethod(.catmullRom)
                    .lineStyle(StrokeStyle(lineWidth: 2))
                }
            }
        }
        .frame(width: width)
        .frame(height: 160)
        .chartXAxis {
            AxisMarks(values: .automatic) { _ in
                AxisValueLabel()
                    .font(.system(size: 10, weight: .bold))
            }
        }
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine()
                AxisValueLabel()
                    .font(.system(size: 10, design: .monospaced))
            }
        }
        .padding(.trailing, 20)
    }
    
    private var connectedTableNode: SpatialNode? {
        let sourceIds = Set(node.inputNodeIds ?? [])
        let incomingStructuralIds = allNodes.filter { other in
            other.nextNodeId == node.id || (other.connectedNodeIds ?? []).contains(node.id)
        }.map { $0.id }
        
        let allSourceIds = sourceIds.union(incomingStructuralIds)
        return allNodes.first { allSourceIds.contains($0.id) && $0.type == .table }
    }
    
    private func getHeaders(for tableNode: SpatialNode) -> [String] {
        let rows = (tableNode.textContent ?? "").components(separatedBy: "\n").filter { !$0.isEmpty }
        guard let firstRow = rows.first else { return [] }
        return firstRow.components(separatedBy: ",").map { $0.trimmingCharacters(in: .whitespaces) }
    }
    
    private var chartData: [ChartDataPoint] {
        // Collect all potential source nodes:
        var sourceIds = Set(node.inputNodeIds ?? [])
        let incomingStructuralIds = allNodes.filter { other in
            other.nextNodeId == node.id || (other.connectedNodeIds ?? []).contains(node.id)
        }.map { $0.id }
        sourceIds.formUnion(incomingStructuralIds)
        
        guard !sourceIds.isEmpty else { return [] }
        var result: [ChartDataPoint] = []
        let sortedSourceIds = Array(sourceIds).sorted { $0.uuidString < $1.uuidString }
        
        for id in sortedSourceIds {
            guard let sourceNode = allNodes.first(where: { $0.id == id }) else { continue }
            
            if sourceNode.type == .table {
                let allRows = (sourceNode.textContent ?? "").components(separatedBy: "\n").filter { !$0.isEmpty }
                
                // If chartHasHeaderRow is true, skip the first row
                let rows = (node.chartHasHeaderRow ?? false) ? Array(allRows.dropFirst()) : allRows
                
                for (index, row) in rows.enumerated() {
                    let columns = row.components(separatedBy: ",")
                    
                    // X-Axis Label
                    let label: String
                    if let xIdx = node.chartXColumnIndex, xIdx < columns.count {
                        label = columns[xIdx].trimmingCharacters(in: .whitespaces)
                    } else if columns.count >= 2 {
                        label = columns[0].trimmingCharacters(in: .whitespaces)
                    } else {
                        label = "Row \(index + 1)"
                    }
                    
                    // Y-Axis Value
                    let value: Double?
                    if let yIdx = node.chartYColumnIndex, yIdx < columns.count {
                        let valStr = columns[yIdx].filter { "0123456789.-".contains($0) }
                        value = Double(valStr)
                    } else {
                        // Auto-scan logic
                        var found: Double? = nil
                        let startSearch = (node.chartXColumnIndex == 0 || columns.count < 2) ? 0 : 1
                        for colIndex in startSearch..<columns.count {
                            let valStr = columns[colIndex].filter { "0123456789.-".contains($0) }
                            if let v = Double(valStr), !valStr.isEmpty {
                                found = v
                                break
                            }
                        }
                        value = found
                    }
                    
                    if let v = value {
                        result.append(ChartDataPoint(label: label, value: v, color: sourceNode.theme.color))
                    }
                }
            } else {
                let value: Double
                if let outVal = sourceNode.outputValue, outVal != 0 {
                    value = outVal
                } else {
                    let text = sourceNode.textContent ?? "0"
                    let cleaned = text.filter { "0123456789.-".contains($0) }
                    value = Double(cleaned) ?? 0
                }
                
                result.append(ChartDataPoint(label: sourceNode.title, value: value, color: sourceNode.theme.color))
            }
        }
        
        return result
    }
}
