# EVE — Native Android Personal AI Voice Assistant

[![Android](https://img.shields.io/badge/Platform-Android%207.0%2B%20(API%2024%2B)-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.20-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(BOM%202026.03.01)-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean%20Architecture-00E5FF)](https://developer.android.com/topic/architecture)

**EVE** is a native, futuristic personal AI voice assistant for Android, engineered with a premium cyberpunk visual identity, fluid reactive animations, and a decoupled Clean Architecture.

- **Wake Phrase**: *"Hey Eve"*
- **Package**: `com.eve.assistant`
- **Minimum SDK**: API 24 (Android 7.0 Nougat)
- **Target SDK**: API 36

---

## 🌌 Visual Design & UI Architecture

EVE is designed to feel like an ambient, highly responsive personal AI rather than a standard chatbot:

- **Deep Space Palette**: Deep black (`#030712`) and midnight navy (`#070C1B`) canvases paired with electric cyan (`#00E5FF`) and cyber violet (`#7C4DFF`) accents.
- **Dynamic Glowing AI Orb**: Custom multi-layered Compose `Canvas` drawing concentric breathing pulses, orbital particle rings, and audio shockwaves driven by a strict 5-state finite state machine (`IDLE`, `LISTENING`, `THINKING`, `SPEAKING`, `ERROR`).
- **Glassmorphism Design System**: Reusable translucent glass cards (`GlassCard`) with subtle hairline gradient borders and backdrop blur accents.
- **Zero-Dependency Vector System**: 20+ handcrafted native Compose `ImageVector` definitions (`EveIcons`) ensuring clean builds without bloated external icon bundles.

---

## 📱 Application Screens (Phase 1)

1. **Home Screen (`HomeScreen.kt`)**:
   - Central reactive glowing AI orb with dynamic breathing, listening ripples, thinking orbit rings, and speaking harmonics.
   - Real-time connection status pill with pulsing LED indicator and latency readout.
   - Interactive 9-bar sound-wave frequency equalizer (`WaveformVisualizer`).
   - Tactile floating microphone interaction button (`MicInteractionButton`) with ambient glow aura.
   - Conversation preview card and top navigation shortcuts.

2. **Conversation History (`HistoryScreen.kt`)**:
   - Search bar with instant query filtering.
   - Category filter chips (`ALL`, `TODAY`, `FAVORITES`).
   - Clean empty state and structured message cards.

3. **Assistant Settings (`SettingsScreen.kt`)**:
   - Navigation hub for voice and privacy configurations.
   - Interaction toggles (Wake-word detection, haptic feedback, continuous listening).
   - System diagnostics and latency telemetry panel.

4. **Voice Settings (`VoiceSettingsScreen.kt`)**:
   - Persona selection cards (`Nova`, `Echo`, `Sol`, `Aura`).
   - Real-time speech rate slider (0.5x – 2.0x).
   - Pitch adjustment slider (0.75x – 1.5x).
   - Ambient noise isolation toggle.

5. **Privacy & Permissions (`PrivacyPermissionsScreen.kt`)**:
   - Granular Android runtime permission status (`RECORD_AUDIO`, `POST_NOTIFICATIONS`).
   - Cloud sync and anonymous telemetry switches.
   - Local conversation cache purge and factory reset dialog.

---

## 🏗️ Architecture & Package Structure

```text
com.eve.assistant/
├── MainActivity.kt                  # Edge-to-edge entry point
├── navigation/
│   ├── NavigationKeys.kt            # Type-safe Navigation3 screen keys
│   └── Navigation.kt                # NavDisplay backstack & animated transitions
└── ui/
    ├── components/                  # Reusable atomic design components
    │   ├── EveIcons.kt              # Native ImageVectors
    │   ├── EveTopAppBar.kt          # Status bar insets & actions
    │   ├── GlassCard.kt             # Translucent glassmorphism containers
    │   ├── GlowingOrb.kt            # High-performance Canvas AI orb
    │   ├── MicInteractionButton.kt  # Glowing tactile action button
    │   ├── StatusPill.kt            # Live telemetry indicator
    │   └── WaveformVisualizer.kt    # Dynamic audio equalizer
    ├── history/                     # History screen, ViewModel, and UiState
    ├── home/                        # Home screen, ViewModel, and UiState
    ├── settings/                    # Settings, Voice, and Privacy screens
    └── theme/                       # Color, Type, and EVETheme tokens
```

---

## 🛠️ Building & Verification

### Prerequisites
- JDK 17+ (configured via `JAVA_HOME` or toolchain)
- Android SDK (API 24 to 36)

### Build Commands
```powershell
# Run all unit tests
.\gradlew.bat testDebugUnitTest

# Assemble debug APK
.\gradlew.bat assembleDebug

# Install on connected device/emulator
.\gradlew.bat installDebug
```

### Verification Status
- **Unit Tests**: 12/12 passing (`HomeViewModelTest`, `HistoryViewModelTest`, `SettingsViewModelTest`).
- **Build Output**: `app/build/outputs/apk/debug/app-debug.apk` (12.1 MB).
- **Device Verification**: Validated on physical device `moto g64 5G` running Android 15.

---

## 🗺️ Project Roadmap

- [x] **Phase 1: UI & Visual Identity** — Complete design system, 5 core screens, animations, and MVVM architecture.
- [ ] **Phase 2: Speech & Audio Pipeline** — Wake-word detection engine, audio recording pipeline, and speech-to-text.
- [ ] **Phase 3: Backend & AI Engine** — Node.js + Express + TypeScript server integration, LLM streaming, and TTS.
- [ ] **Phase 4: Production Hardening & Release** — End-to-end tests, performance profiling, and release build.
