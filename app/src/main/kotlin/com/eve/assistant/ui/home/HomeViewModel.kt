package com.eve.assistant.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel governing EVE Home Screen reactive UI state.
 */
class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            greeting = resolveGreeting(),
            statusMessage = "Neural Engine Ready"
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onMicClicked() {
        _uiState.update { current ->
            when (current.eveState) {
                EveState.IDLE -> current.copy(
                    eveState = EveState.LISTENING,
                    statusMessage = "Listening for voice input..."
                )
                EveState.LISTENING -> current.copy(
                    eveState = EveState.IDLE,
                    statusMessage = "Neural Engine Ready"
                )
                EveState.THINKING -> current.copy(
                    eveState = EveState.IDLE,
                    statusMessage = "Processing cancelled"
                )
                EveState.SPEAKING -> current.copy(
                    eveState = EveState.IDLE,
                    statusMessage = "Speech stopped"
                )
                EveState.ERROR -> current.copy(
                    eveState = EveState.IDLE,
                    statusMessage = "Neural Engine Ready",
                    errorMessage = null
                )
            }
        }
    }

    /**
     * Explicit state setter used for testing, QA verification, and component preview.
     */
    fun setEveState(state: EveState) {
        val status = when (state) {
            EveState.IDLE -> "Neural Engine Ready"
            EveState.LISTENING -> "Listening for voice input..."
            EveState.THINKING -> "Synthesizing response..."
            EveState.SPEAKING -> "Streaming audio response..."
            EveState.ERROR -> "Neural stream error encountered"
        }
        val err = if (state == EveState.ERROR) "Network socket handshake timed out" else null

        _uiState.update { current ->
            current.copy(
                eveState = state,
                statusMessage = status,
                errorMessage = err
            )
        }
    }

    fun retryConnection() {
        _uiState.update {
            it.copy(
                connectionStatus = ConnectionStatus.CONNECTED,
                latencyMs = 24,
                errorMessage = null,
                eveState = EveState.IDLE,
                statusMessage = "Neural Engine Connected"
            )
        }
    }

    private fun resolveGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Good night"
        }
    }
}
