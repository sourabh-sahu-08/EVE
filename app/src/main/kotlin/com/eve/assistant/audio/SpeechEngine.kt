package com.eve.assistant.audio

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Clean abstraction for speech-to-text recognition engine.
 */
interface SpeechEngine {
    val status: StateFlow<RecognitionStatus>
    val events: SharedFlow<SpeechEvent>
    val audioRms: StateFlow<Float>

    fun isAvailable(): Boolean
    fun startListening(languageTag: String? = null)
    fun stopListening()
    fun cancel()
    fun destroy()
}
