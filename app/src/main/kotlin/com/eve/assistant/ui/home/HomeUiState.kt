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
 * State container for the EVE Home Screen.
 */
data class HomeUiState(
    val eveState: EveState = EveState.IDLE,
    val connectionStatus: ConnectionStatus = ConnectionStatus.CONNECTED,
    val latencyMs: Int = 24,
    val greeting: String = "Good day, Sourabh",
    val statusMessage: String = "Neural Engine Ready",
    val lastUserQuery: String? = null,
    val lastEveResponse: String? = null,
    val lastInteractionTime: String? = null,
    val errorMessage: String? = null
)
