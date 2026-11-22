package com.eve.assistant.ui.settings

import com.eve.assistant.ui.home.FakeTextToSpeechEngine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    private lateinit var fakeTtsEngine: FakeTextToSpeechEngine
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        fakeTtsEngine = FakeTextToSpeechEngine()
        viewModel = SettingsViewModel(fakeTtsEngine)
    }

    @Test
    fun initialState_hasSensibleDefaults() = runTest {
        val state = viewModel.uiState.first()
        assertTrue(state.isWakeWordEnabled)
        assertTrue(state.isHapticFeedbackEnabled)
        assertFalse(state.isFloatingOverlayEnabled)
        assertEquals(VoicePersonality.NOVA, state.selectedVoice)
        assertEquals(1.0f, state.speechRate, 0.01f)
        assertEquals(1.0f, state.pitchMultiplier, 0.01f)
    }

    @Test
    fun toggles_updateStateCorrectly() = runTest {
        viewModel.setWakeWordEnabled(false)
        assertFalse(viewModel.uiState.value.isWakeWordEnabled)

        viewModel.setHapticFeedbackEnabled(false)
        assertFalse(viewModel.uiState.value.isHapticFeedbackEnabled)

        viewModel.setContinuousListeningEnabled(true)
        assertTrue(viewModel.uiState.value.isContinuousListeningEnabled)
    }

    @Test
    fun voiceAndAcoustic_updateStateAndEngineCorrectly() = runTest {
        viewModel.selectVoice(VoicePersonality.ECHO)
        assertEquals(VoicePersonality.ECHO, viewModel.uiState.value.selectedVoice)
        assertEquals(VoicePersonality.ECHO, fakeTtsEngine.selectedPersonality)

        viewModel.setSpeechRate(1.25f)
        assertEquals(1.25f, viewModel.uiState.value.speechRate, 0.01f)
        assertEquals(1.25f, fakeTtsEngine.currentRate, 0.01f)

        viewModel.setPitchMultiplier(0.9f)
        assertEquals(0.9f, viewModel.uiState.value.pitchMultiplier, 0.01f)
        assertEquals(0.9f, fakeTtsEngine.currentPitch, 0.01f)
    }

    @Test
    fun testVoiceSynthesis_invokesTtsEngine() = runTest {
        viewModel.testVoiceSynthesis()
        assertNotNull(fakeTtsEngine.lastSpokenText)
        assertTrue(fakeTtsEngine.lastSpokenText!!.contains("operational"))

        viewModel.stopVoiceSynthesis()
        assertTrue(fakeTtsEngine.stopCalled)
    }

    @Test
    fun resetToDefaults_restoresDefaults() = runTest {
        viewModel.selectVoice(VoicePersonality.SOL)
        viewModel.setWakeWordEnabled(false)
        viewModel.resetToDefaults()

        val state = viewModel.uiState.value
        assertEquals(VoicePersonality.NOVA, state.selectedVoice)
        assertTrue(state.isWakeWordEnabled)
        assertEquals(VoicePersonality.NOVA, fakeTtsEngine.selectedPersonality)
    }
}
