package com.eve.assistant.ui.history

/**
 * Filter categories for conversation history.
 */
enum class HistoryFilter {
    ALL,
    TODAY,
    FAVORITES
}

/**
 * A recorded conversation exchange with EVE.
 */
data class HistorySession(
    val id: String,
    val userQuery: String,
    val eveResponse: String,
    val timestamp: String,
    val isFavorite: Boolean = false
)

/**
 * UI State for the Conversation History screen.
 */
data class HistoryUiState(
    val sessions: List<HistorySession> = emptyList(),
    val filteredSessions: List<HistorySession> = emptyList(),
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)
