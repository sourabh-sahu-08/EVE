package com.eve.assistant.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eve.assistant.audio.TextToSpeechEngine
import com.eve.assistant.audio.TextToSpeechManager
import com.eve.assistant.audio.TtsEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing global assistant settings, voice tuning, and privacy controls.
 * Interfaces with TextToSpeechEngine to tune synthesis rate, pitch, and voice profile in real-time.
 */
class SettingsViewModel(
    private var ttsManager: TextToSpeechEngine? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var ttsJob: Job? = null

    init {
        ttsManager?.let { bindTtsManager(it) }
    }

    fun attachContext(context: Context) {
        if (ttsManager == null) {
            val tm = TextToSpeechManager(context.applicationContext)
            ttsManager = tm
            bindTtsManager(tm)
        }
    }

    fun setTtsManager(manager: TextToSpeechEngine) {
        ttsJob?.cancel()
        ttsManager = manager
        bindTtsManager(manager)
    }

    private fun bindTtsManager(tm: TextToSpeechEngine) {
        ttsJob?.cancel()
        ttsJob = viewModelScope.launch {
            tm.events.collect { event ->
                when (event) {
                    is TtsEvent.Started -> {
                        _uiState.update { it.copy(isSpeakingTest = true) }
                    }
                    is TtsEvent.Done, is TtsEvent.Error -> {
                        _uiState.update { it.copy(isSpeakingTest = false) }
                    }
                }
            }
        }
    }

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
        ttsManager?.setVoicePersonality(voice)
    }

    fun setSpeechRate(rate: Float) {
        _uiState.update { it.copy(speechRate = rate) }
        ttsManager?.setSpeechRate(rate)
    }

    fun setPitchMultiplier(pitch: Float) {
        _uiState.update { it.copy(pitchMultiplier = pitch) }
        ttsManager?.setPitch(pitch)
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

    fun setMicPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(isMicPermissionGranted = granted) }
    }

    fun testVoiceSynthesis() {
        ttsManager?.speakTestUtterance()
    }

    fun stopVoiceSynthesis() {
        ttsManager?.stop()
        _uiState.update { it.copy(isSpeakingTest = false) }
    }

    fun resetToDefaults() {
        _uiState.value = SettingsUiState()
        ttsManager?.setSpeechRate(1.0f)
        ttsManager?.setPitch(1.0f)
        ttsManager?.setVoicePersonality(VoicePersonality.NOVA)
    }

    override fun onCleared() {
        super.onCleared()
        ttsJob?.cancel()
        ttsManager?.shutdown()
        ttsManager = null
    }
}
