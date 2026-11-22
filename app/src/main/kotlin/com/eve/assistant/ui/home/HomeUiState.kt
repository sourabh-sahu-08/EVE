package com.eve.assistant.ui.home

/**
 * Explicit operational state of the EVE voice assistant interface.
 */
enum class EveState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    ERROR
}

/**
 * Cloud backend connection status.
 */
enum class ConnectionStatus {
    CONNECTED,
    CONNECTING,
    OFFLINE
}

/**
 * Reactive state container for the EVE Home Screen and multi-provider voice interaction pipeline.
 */
data class HomeUiState(
    val eveState: EveState = EveState.IDLE,
    val connectionStatus: ConnectionStatus = ConnectionStatus.CONNECTED,
    val latencyMs: Int = 24,
    val greeting: String = "Good day, Sourabh",
    val statusMessage: String = "Neural Engine Ready",
    val partialTranscript: String? = null,
    val lastUserQuery: String? = null,
    val lastEveResponse: String? = null,
    val lastInteractionTime: String? = null,
    val errorMessage: String? = null,
    val audioRmsLevel: Float = 0f,
    val isMicPermissionGranted: Boolean = false,
    val isRecognitionAvailable: Boolean = true,
    val isTtsReady: Boolean = false,

    // Phase 3 Multi-Provider AI Telemetry
    val activeProvider: String? = null,
    val activeModel: String? = null,
    val providerLatencyMs: Long? = null,
    val fallbackCount: Int = 0
)
