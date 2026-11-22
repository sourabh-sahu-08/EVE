package com.eve.assistant.ui.home

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        viewModel = HomeViewModel()
    }

    @Test
    fun initialState_isIdleWithGreeting() = runTest {
        val state = viewModel.uiState.first()
        assertEquals(EveState.IDLE, state.eveState)
        assertEquals(ConnectionStatus.CONNECTED, state.connectionStatus)
        assertNotNull(state.greeting)
        assertEquals("Neural Engine Ready", state.statusMessage)
        assertNull(state.errorMessage)
    }

    @Test
    fun onMicClicked_togglesBetweenIdleAndListening() = runTest {
        viewModel.onMicClicked()
        assertEquals(EveState.LISTENING, viewModel.uiState.value.eveState)
        assertEquals("Listening for voice input...", viewModel.uiState.value.statusMessage)

        viewModel.onMicClicked()
        assertEquals(EveState.IDLE, viewModel.uiState.value.eveState)
        assertEquals("Neural Engine Ready", viewModel.uiState.value.statusMessage)
    }

    @Test
    fun setEveState_updatesStateAndStatusCorrectly() = runTest {
        viewModel.setEveState(EveState.THINKING)
        assertEquals(EveState.THINKING, viewModel.uiState.value.eveState)
        assertEquals("Synthesizing response...", viewModel.uiState.value.statusMessage)

        viewModel.setEveState(EveState.SPEAKING)
        assertEquals(EveState.SPEAKING, viewModel.uiState.value.eveState)
        assertEquals("Streaming audio response...", viewModel.uiState.value.statusMessage)

        viewModel.setEveState(EveState.ERROR)
        assertEquals(EveState.ERROR, viewModel.uiState.value.eveState)
        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun retryConnection_restoresConnectedState() = runTest {
        viewModel.setEveState(EveState.ERROR)
        viewModel.retryConnection()

        val state = viewModel.uiState.value
        assertEquals(EveState.IDLE, state.eveState)
        assertEquals(ConnectionStatus.CONNECTED, state.connectionStatus)
        assertNull(state.errorMessage)
    }
}
