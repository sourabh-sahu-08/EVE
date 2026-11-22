package com.eve.assistant.ui.history

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HistoryViewModelTest {

    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        viewModel = HistoryViewModel()
    }

    @Test
    fun initialState_hasEmptySessions() = runTest {
        val state = viewModel.uiState.first()
        assertTrue(state.sessions.isEmpty())
        assertTrue(state.filteredSessions.isEmpty())
        assertEquals(HistoryFilter.ALL, state.selectedFilter)
        assertEquals("", state.searchQuery)
    }

    @Test
    fun onFilterSelected_updatesSelectedFilter() = runTest {
        viewModel.onFilterSelected(HistoryFilter.FAVORITES)
        assertEquals(HistoryFilter.FAVORITES, viewModel.uiState.value.selectedFilter)
    }

    @Test
    fun onSearchQueryChanged_updatesSearchQuery() = runTest {
        viewModel.onSearchQueryChanged("test query")
        assertEquals("test query", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun clearHistory_clearsAllSessions() = runTest {
        viewModel.clearHistory()
        assertTrue(viewModel.uiState.value.sessions.isEmpty())
        assertTrue(viewModel.uiState.value.filteredSessions.isEmpty())
    }
}
