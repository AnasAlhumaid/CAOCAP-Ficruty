import Foundation
import CoreGraphics

public enum NodeAction: String, Codable, Equatable {
    case navigateHome
    case retryOnboarding
    case createNewProject
    case openSettings
    case openProfile
    case openProjectExplorer
    case resumeLastProject
    case summonCoCaptain
}

public enum NodeType: String, Codable, Equatable, CaseIterable {
    case standard
    case webView
    case srs
    case code
    case art
    case text
    case number
    case table
    case calculation
    case display
    case aiAgent
    case chart
    
    public var displayName: String {
        switch self {
        case .standard: return "Standard"
        case .webView: return "Web View"
        case .srs: return "SRS"
        case .code: return "Code"
        case .art: return "Art"
        case .text: return "Text"
        case .number: return "Number"
        case .table: return "Table"
        case .calculation: return "Calculation"
        case .display: return "Display"
        case .aiAgent: return "AI Agent"
        case .chart: return "Chart"
        }
    }
}

public enum ChartStyle: String, Codable, CaseIterable {
    case bar
    case line
    case area
    
    public var displayName: String {
        switch self {
        case .bar: return "Bar Chart"
        case .line: return "Line Trend"
        case .area: return "Area Graph"
        }
    }
    
    public var icon: String {
        switch self {
        case .bar: return "chart.bar.fill"
        case .line: return "chart.line.uptrend.xyaxis"
        case .area: return "chart.xyaxis.line"
        }
    }
}

public enum ArithmeticOperation: String, Codable, Equatable, CaseIterable {
    case add = "+"
    case subtract = "-"
    case multiply = "×"
    case divide = "÷"
    
    public var icon: String {
        switch self {
        case .add: return "plus"
        case .subtract: return "minus"
        case .multiply: return "multiply"
        case .divide: return "divide"
        }
    }
}

public enum DisplayStyle: String, Codable, CaseIterable {
    case number
    case percentage
    case progress
    case gauge
    
    public var displayName: String {
        switch self {
        case .number: return "Big Number"
        case .percentage: return "Percentage"
        case .progress: return "Progress Bar"
        case .gauge: return "Gauge"
        }
    }
}

public struct SpatialNode: Identifiable, Codable, Equatable {
    public let id: UUID
    public var type: NodeType
    public var position: CGPoint
    public var title: String
    public var subtitle: String?
    public var icon: String?
    public var theme: NodeTheme
    public var nextNodeId: UUID?
    public var connectedNodeIds: [UUID]?
    public var action: NodeAction?
    public var htmlContent: String?
    public var textContent: String?
    public var srsReadinessState: SRSReadinessState?
    public var drawingData: Data?
    
    /// The arithmetic operation to perform for calculation nodes.
    public var operation: ArithmeticOperation?
    
    /// The display style for display nodes.
    public var displayStyle: DisplayStyle?
    
    /// The computed result for calculation/display nodes.
    public var outputValue: Double?
    
    /// The computed text result for AI/Text nodes.
    public var aiResponse: String?
    
    /// The AI prompt template for AI-processing nodes.
    public var promptTemplate: String?
    
    /// The chart style for chart nodes.
    public var chartStyle: ChartStyle?
    
    /// The selected column index for X-axis labels (for table inputs).
    public var chartXColumnIndex: Int?
    
    /// The selected column index for Y-axis values (for table inputs).
    public var chartYColumnIndex: Int?
    
    /// Whether the input table has a header row to be skipped.
    public var chartHasHeaderRow: Bool?
    
    /// IDs of nodes providing input data to this node.
    public var inputNodeIds: [UUID]?

    enum CodingKeys: String, CodingKey {
        case id, type, position, title, subtitle, icon, theme, nextNodeId, connectedNodeIds, action, htmlContent, textContent, srsReadinessState, drawingData, operation, outputValue, aiResponse, promptTemplate, inputNodeIds, displayStyle, chartStyle, chartXColumnIndex, chartYColumnIndex, chartHasHeaderRow
    }
    
    public init(id: UUID = UUID(), type: NodeType = .standard, position: CGPoint, title: String, subtitle: String? = nil, icon: String? = nil, theme: NodeTheme = .blue, nextNodeId: UUID? = nil, connectedNodeIds: [UUID]? = nil, action: NodeAction? = nil, htmlContent: String? = nil, textContent: String? = nil, srsReadinessState: SRSReadinessState? = nil, drawingData: Data? = nil, operation: ArithmeticOperation? = nil, outputValue: Double? = nil, aiResponse: String? = nil, promptTemplate: String? = nil, inputNodeIds: [UUID]? = nil, displayStyle: DisplayStyle? = nil, chartStyle: ChartStyle? = nil, chartXColumnIndex: Int? = nil, chartYColumnIndex: Int? = nil, chartHasHeaderRow: Bool? = nil) {
        self.id = id
        self.type = type
        self.position = position
        self.title = title
        self.subtitle = subtitle
        self.icon = icon
        self.theme = theme
        self.nextNodeId = nextNodeId
        self.connectedNodeIds = connectedNodeIds
        self.action = action
        self.htmlContent = htmlContent
        self.textContent = textContent
        self.srsReadinessState = srsReadinessState
        self.drawingData = drawingData
        self.operation = operation
        self.outputValue = outputValue
        self.aiResponse = aiResponse
        self.promptTemplate = promptTemplate
        self.inputNodeIds = inputNodeIds
        self.displayStyle = displayStyle
        self.chartStyle = chartStyle
        self.chartXColumnIndex = chartXColumnIndex
        self.chartYColumnIndex = chartYColumnIndex
        self.chartHasHeaderRow = chartHasHeaderRow
    }

    public var displayTitle: String {
        LocalizationManager.shared.localizedNodeTitle(title)
    }

    public var displaySubtitle: String? {
        subtitle.map { LocalizationManager.shared.localizedNodeSubtitle($0) }
    }
}
