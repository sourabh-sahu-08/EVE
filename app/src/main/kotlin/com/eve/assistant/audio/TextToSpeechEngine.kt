package com.eve.assistant.audio

import com.eve.assistant.ui.settings.VoicePersonality
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Clean abstraction for text-to-speech synthesis engine.
 */
interface TextToSpeechEngine {
    val isInitialized: StateFlow<Boolean>
    val isSpeaking: StateFlow<Boolean>
    val events: SharedFlow<TtsEvent>

    fun speak(text: String, utteranceId: String = UUID.randomUUID().toString())
    fun stop()
    fun setSpeechRate(rate: Float)
    fun setPitch(pitch: Float)
    fun setVoicePersonality(personality: VoicePersonality)
    fun speakTestUtterance()
    fun shutdown()
}
