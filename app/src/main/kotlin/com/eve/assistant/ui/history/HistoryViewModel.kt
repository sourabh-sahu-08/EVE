package com.eve.assistant.ui.history

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing the conversation history timeline and search/filter state.
 */
class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            val updated = current.copy(searchQuery = query)
            updated.copy(filteredSessions = applyFilters(current.sessions, updated.selectedFilter, query))
        }
    }

    fun onFilterSelected(filter: HistoryFilter) {
        _uiState.update { current ->
            val updated = current.copy(selectedFilter = filter)
            updated.copy(filteredSessions = applyFilters(current.sessions, filter, current.searchQuery))
        }
    }

    fun toggleFavorite(sessionId: String) {
        _uiState.update { current ->
            val updatedSessions = current.sessions.map {
                if (it.id == sessionId) it.copy(isFavorite = !it.isFavorite) else it
            }
            current.copy(
                sessions = updatedSessions,
                filteredSessions = applyFilters(updatedSessions, current.selectedFilter, current.searchQuery)
            )
        }
    }

    fun clearHistory() {
        _uiState.update {
            it.copy(
                sessions = emptyList(),
                filteredSessions = emptyList()
            )
        }
    }

    private fun applyFilters(
        sessions: List<HistorySession>,
        filter: HistoryFilter,
        query: String
    ): List<HistorySession> {
        return sessions
            .filter { session ->
                when (filter) {
                    HistoryFilter.ALL -> true
                    HistoryFilter.TODAY -> true // In future phases, compared against timestamp
                    HistoryFilter.FAVORITES -> session.isFavorite
                }
            }
            .filter { session ->
                if (query.isBlank()) true
                else session.userQuery.contains(query, ignoreCase = true) ||
                        session.eveResponse.contains(query, ignoreCase = true)
            }
    }
}
