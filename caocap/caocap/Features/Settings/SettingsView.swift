import SwiftUI

struct SettingsView: View {
    @Environment(\.dismiss) private var dismiss
    
    @AppStorage("app_language") private var selectedLanguage = "English"
    @AppStorage("app_theme") private var selectedTheme = "System"
    @AppStorage("haptics_enabled") private var hapticsEnabled = true
    @AppStorage("haptics_intensity") private var hapticsIntensity = "Medium"
    @AppStorage("grid_opacity") private var gridOpacity: Double = 0.1
    @AppStorage("connection_style") private var connectionStyle = "Dashed"
    @AppStorage("spatial_glow_enabled") private var spatialGlowEnabled = true
    
    let languages = ["English", "Arabic", "French", "German", "Spanish"]
    let themes = ["System", "Light", "Dark"]
    let intensities = ["Subtle", "Medium", "Sharp"]
    let styles = ["Solid", "Dashed", "Neon"]
    
    var body: some View {
        NavigationStack {
            ZStack {
                // MARK: - Background
                Color(uiColor: .systemBackground).ignoresSafeArea()
                
                // Subtle Glow
                if spatialGlowEnabled {
                    Circle()
                        .fill(Color.orange.opacity(0.1))
                        .frame(width: 400, height: 400)
                        .blur(radius: 60)
                        .offset(x: 150, y: -200)
                }
                
                ScrollView {
                    VStack(spacing: 32) {
                        
                        VStack(spacing: 24) {
                            // MARK: - Interface
                            SettingsSection("Interface") {
                                SettingsPickerRow(icon: "paintbrush.fill", title: "Theme", selection: $selectedTheme, options: themes, color: .purple)
                                
                                Divider().padding(.leading, 56).opacity(0.3)
                                
                                SettingsPickerRow(icon: "globe", title: "Language", selection: $selectedLanguage, options: languages, color: .blue)
                                    .disabled(true)
                                    .opacity(0.5)
                                    .overlay(alignment: .trailing) {
                                        Text("Locked")
                                            .font(.caption2)
                                            .foregroundStyle(.secondary)
                                            .padding(.trailing, 40)
                                    }
                            }
                            
                            // MARK: - Canvas & Graphics
                            SettingsSection("Canvas & Graphics") {
                                VStack(alignment: .leading, spacing: 12) {
                                    HStack {
                                        Label("Grid Visibility", systemImage: "grid")
                                            .font(.system(size: 16, weight: .medium))
                                        Spacer()
                                        Text("\(Int(gridOpacity * 100))%")
                                            .font(.system(size: 14, design: .monospaced))
                                            .foregroundStyle(.secondary)
                                    }
                                    Slider(value: $gridOpacity, in: 0.05...0.4, step: 0.05)
                                        .tint(.orange)
                                }
                                .padding(.horizontal, 16)
                                .padding(.vertical, 14)
                                
                                Divider().padding(.leading, 56).opacity(0.3)
                                
                                SettingsPickerRow(icon: "waveform.path", title: "Connection Style", selection: $connectionStyle, options: styles, color: .orange)
                                
                                Divider().padding(.leading, 56).opacity(0.3)
                                
                                Toggle(isOn: $spatialGlowEnabled) {
                                    Label("Spatial Glow", systemImage: "sun.max.fill")
                                        .font(.system(size: 16, weight: .medium))
                                }
                                .padding(.horizontal, 16)
                                .padding(.vertical, 14)
                                .tint(.orange)
                            }
                            
                            // MARK: - Haptics
                            SettingsSection("Haptics") {
                                Toggle(isOn: $hapticsEnabled) {
                                    Label("Tactile Feedback", systemImage: "sensor.touch.fill")
                                        .font(.system(size: 16, weight: .medium))
                                }
                                .padding(.horizontal, 16)
                                .padding(.vertical, 14)
                                .tint(.green)
                                
                                if hapticsEnabled {
                                    Divider().padding(.leading, 56).opacity(0.3)
                                    
                                    SettingsPickerRow(icon: "shredder.fill", title: "Intensity", selection: $hapticsIntensity, options: intensities, color: .green)
                                }
                            }
                        }
                        .padding(.horizontal, 20)
                        .padding(.top, 20)
                        
                        // MARK: - Footer
                        VStack(spacing: 8) {
                            Text("ENGINE CONFIGURATION")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundStyle(.secondary)
                            Text("Real-time synchronization active.")
                                .font(.system(size: 10))
                                .foregroundStyle(.tertiary)
                        }
                        .padding(.vertical, 40)
                    }
                }
            }
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Text("Settings")
                        .font(.system(size: 24, weight: .black, design: .rounded))
                        .foregroundStyle(.primary)
                }
                
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "xmark")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundStyle(.primary.opacity(0.6))
                            .padding(8)
                            .background(.primary.opacity(0.1))
                            .clipShape(Circle())
                    }
                }
            }
            .preferredColorScheme(currentColorScheme)
            .onAppear {
                selectedLanguage = "English"
            }
        }
    }
    
    private var currentColorScheme: ColorScheme? {
        switch selectedTheme {
        case "Light": return .light
        case "Dark": return .dark
        default: return nil
        }
    }
}

// MARK: - Helper View
private struct SettingsPickerRow: View {
    let icon: String
    let title: LocalizedStringKey
    @Binding var selection: String
    let options: [String]
    let color: Color
    
    var body: some View {
        HStack {
            Label(title, systemImage: icon)
                .font(.system(size: 16, weight: .medium))
            Spacer()
            Picker(title, selection: $selection) {
                ForEach(options, id: \.self) { option in
                    Text(LocalizedStringKey(option)).tag(option)
                }
            }
            .pickerStyle(.menu)
            .tint(color)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 14)
    }
}

#Preview {
    SettingsView()
}
