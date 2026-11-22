package com.eve.assistant.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing global assistant settings, voice tuning, and privacy controls.
 */
class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setWakeWordEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isWakeWordEnabled = enabled) }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isHapticFeedbackEnabled = enabled) }
    }

    fun setFloatingOverlayEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isFloatingOverlayEnabled = enabled) }
    }

    fun setContinuousListeningEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isContinuousListeningEnabled = enabled) }
    }

    fun selectVoice(voice: VoicePersonality) {
        _uiState.update { it.copy(selectedVoice = voice) }
    }

    fun setSpeechRate(rate: Float) {
        _uiState.update { it.copy(speechRate = rate) }
    }

    fun setPitchMultiplier(pitch: Float) {
        _uiState.update { it.copy(pitchMultiplier = pitch) }
    }

    fun setNoiseSuppressionEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isNoiseSuppressionEnabled = enabled) }
    }

    fun setCloudSyncEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isCloudSyncEnabled = enabled) }
    }

    fun setAnonymousTelemetryEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isAnonymousTelemetryEnabled = enabled) }
    }

    fun resetToDefaults() {
        _uiState.value = SettingsUiState()
    }
}
