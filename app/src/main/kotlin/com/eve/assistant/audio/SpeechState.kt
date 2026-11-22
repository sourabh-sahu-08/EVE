package com.eve.assistant.audio

/**
 * Real-time event streams emitted by the SpeechRecognitionManager.
 */
sealed interface SpeechEvent {
    data class ReadyForSpeech(val isReady: Boolean) : SpeechEvent
    object BeginningOfSpeech : SpeechEvent
    data class RmsChanged(val rmsDb: Float, val normalizedLevel: Float) : SpeechEvent
    data class PartialResult(val text: String) : SpeechEvent
    data class FinalResult(val text: String) : SpeechEvent
    data class Error(val errorCode: Int, val message: String) : SpeechEvent
    object EndOfSpeech : SpeechEvent
}

/**
 * High-level operational state of the speech recognition pipeline.
 */
enum class RecognitionStatus {
    IDLE,
    PREPARING,
    READY,
    LISTENING,
    PROCESSING,
    ERROR
}
