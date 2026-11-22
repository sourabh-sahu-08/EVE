package com.eve.assistant.ui.settings

/**
 * Available synthetic voice personalities for EVE.
 */
enum class VoicePersonality(
    val displayName: String,
    val description: String
) {
    NOVA("Nova", "Warm, natural, expressive tone"),
    ECHO("Echo", "Resonant, authoritative, deep timbre"),
    SOL("Sol", "Crisp, precise, energetic delivery"),
    AURA("Aura", "Calm, ambient, soothing frequency")
}

/**
 * UI State container for Assistant Settings, Voice Tuning, and Privacy.
 */
data class SettingsUiState(
    // Assistant Settings
    val isWakeWordEnabled: Boolean = true,
    val isHapticFeedbackEnabled: Boolean = true,
    val isFloatingOverlayEnabled: Boolean = false,
    val isContinuousListeningEnabled: Boolean = false,

    // Voice Settings
    val selectedVoice: VoicePersonality = VoicePersonality.NOVA,
    val speechRate: Float = 1.0f,
    val pitchMultiplier: Float = 1.0f,
    val isNoiseSuppressionEnabled: Boolean = true,

    // Privacy & Permissions
    val isMicPermissionGranted: Boolean = true,
    val isNotificationPermissionGranted: Boolean = true,
    val isCloudSyncEnabled: Boolean = true,
    val isAnonymousTelemetryEnabled: Boolean = false,

    // System Telemetry
    val appVersion: String = "1.0.0-phase1",
    val engineStatus: String = "Cloud AI Ready",
    val architecture: String = "Clean Architecture + MVVM"
)
