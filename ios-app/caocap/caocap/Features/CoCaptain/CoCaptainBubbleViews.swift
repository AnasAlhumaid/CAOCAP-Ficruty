import SwiftUI

struct ThinkingIndicator: View {
    @State private var dotScale: CGFloat = 0.5

    var body: some View {
        HStack(spacing: 4) {
            ForEach(0..<3) { index in
                Circle()
                    .fill(Color.blue.opacity(0.5))
                    .frame(width: 6, height: 6)
                    .scaleEffect(dotScale)
                    .animation(
                        .easeInOut(duration: 0.6)
                            .repeatForever()
                            .delay(Double(index) * 0.2),
                        value: dotScale
                    )
            }
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(Color.primary.opacity(0.05))
        .clipShape(Capsule())
        .onAppear {
            dotScale = 1.0
        }
    }
}

struct ChatBubbleView: View {
    let message: ChatBubbleItem

    var body: some View {
        HStack(alignment: .bottom, spacing: 10) {
            if message.isUser {
                Spacer()
            } else {
                Image("cocaptain")
                    .resizable()
                    .scaledToFill()
                    .frame(width: 32, height: 32)
                    .clipShape(Circle())
                    .overlay(Circle().stroke(Color.blue.opacity(0.3), lineWidth: 1))
                    .shadow(color: .blue.opacity(0.4), radius: 6)
            }

            VStack(alignment: message.isUser ? .trailing : .leading, spacing: 4) {
                ChatBubbleText(message: message)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)
                    .background(messageBackground)
                    .foregroundColor(message.isUser ? .white : .primary)
            }

            if message.isUser {
                ZStack {
                    Circle()
                        .fill(.ultraThinMaterial)
                        .frame(width: 34, height: 34)
                        .overlay(Circle().stroke(Color.primary.opacity(0.1), lineWidth: 1))

                    Image(systemName: "person.fill")
                        .font(.system(size: 14))
                        .foregroundColor(.primary.opacity(0.7))
                }
            } else {
                Spacer()
            }
        }
        .transition(.asymmetric(insertion: .push(from: .bottom).combined(with: .opacity), removal: .opacity))
    }

    @ViewBuilder
    private var messageBackground: some View {
        if message.isUser {
            MessageBubbleShape(isUser: true)
                .fill(
                    LinearGradient(
                        colors: [Color(hex: "0066FF"), Color(hex: "00CCFF")],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .shadow(color: .blue.opacity(0.2), radius: 4, y: 2)
        } else {
            MessageBubbleShape(isUser: false)
                .fill(.ultraThinMaterial)
                .overlay(
                    MessageBubbleShape(isUser: false)
                        .stroke(
                            LinearGradient(
                                colors: [
                                    Color.blue.opacity(0.4),
                                    Color.cyan.opacity(0.2)
                                ],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            ),
                            lineWidth: 1
                        )
                )
        }
    }
}

struct ChatBubbleText: View {
    let message: ChatBubbleItem

    var body: some View {
        Text(styledMarkdown)
            .textSelection(.enabled)
            .fixedSize(horizontal: false, vertical: true)
    }

    private var styledMarkdown: AttributedString {
        if message.isUser {
            return AttributedString(message.text)
        }

        var attributed = message.markdownText
        attributed.mergeAttributes(
            AttributeContainer().font(.system(size: 15, weight: .medium)),
            mergePolicy: .keepCurrent
        )

        for run in attributed.runs {
            if let intent = run.inlinePresentationIntent, intent.contains(.code) {
                attributed[run.range].foregroundColor = .orange
                attributed[run.range].backgroundColor = Color.primary.opacity(0.05)
            }
        }

        return attributed
    }
}

struct MessageBubbleShape: Shape {
    var isUser: Bool
    var radius: CGFloat = 18

    func path(in rect: CGRect) -> Path {
        var path = Path()

        let tl = radius
        let tr = radius
        let bl = isUser ? radius : 4
        let br = isUser ? 4 : radius

        path.move(to: CGPoint(x: rect.minX + tl, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX - tr, y: rect.minY))
        path.addArc(center: CGPoint(x: rect.maxX - tr, y: rect.minY + tr), radius: tr, startAngle: Angle(degrees: -90), endAngle: Angle(degrees: 0), clockwise: false)

        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY - br))
        path.addArc(center: CGPoint(x: rect.maxX - br, y: rect.maxY - br), radius: br, startAngle: Angle(degrees: 0), endAngle: Angle(degrees: 90), clockwise: false)

        path.addLine(to: CGPoint(x: rect.minX + bl, y: rect.maxY))
        path.addArc(center: CGPoint(x: rect.minX + bl, y: rect.maxY - bl), radius: bl, startAngle: Angle(degrees: 90), endAngle: Angle(degrees: 180), clockwise: false)

        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY + tl))
        path.addArc(center: CGPoint(x: rect.minX + tl, y: rect.minY + tl), radius: tl, startAngle: Angle(degrees: 180), endAngle: Angle(degrees: 270), clockwise: false)

        return path
    }
}
