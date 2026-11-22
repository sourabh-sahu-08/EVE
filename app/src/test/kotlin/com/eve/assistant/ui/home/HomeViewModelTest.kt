package com.eve.assistant.ui.home

import com.eve.assistant.audio.SpeechEvent
import com.eve.assistant.audio.TtsEvent
import com.eve.assistant.data.model.AssistantResult
import com.eve.assistant.data.model.FallbackStep
import com.eve.assistant.data.model.InteractResponse
import com.eve.assistant.data.model.TokenUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeSpeechEngine: FakeSpeechEngine
    private lateinit var fakeTtsEngine: FakeTextToSpeechEngine
    private lateinit var fakeAssistantRepo: FakeAssistantRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeSpeechEngine = FakeSpeechEngine()
        fakeTtsEngine = FakeTextToSpeechEngine()
        fakeAssistantRepo = FakeAssistantRepository()
        viewModel = HomeViewModel(fakeSpeechEngine, fakeTtsEngine, fakeAssistantRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isIdleWithGreeting() = runTest {
        val state = viewModel.uiState.first()
        assertEquals(EveState.IDLE, state.eveState)
        assertEquals(ConnectionStatus.CONNECTED, state.connectionStatus)
        assertNotNull(state.greeting)
        assertEquals("Neural Engine Ready", state.statusMessage)
        assertNull(state.errorMessage)
        assertNull(state.partialTranscript)
    }

    @Test
    fun onMicClicked_withoutPermission_requestsPermission() = runTest {
        var permissionRequested = false
        viewModel.onMicClicked(
            hasPermission = false,
            onRequestPermission = { permissionRequested = true }
        )

        assertTrue(permissionRequested)
        assertFalse(fakeSpeechEngine.startListeningCalled)
        assertEquals(EveState.IDLE, viewModel.uiState.value.eveState)
    }

    @Test
    fun onPermissionDenied_updatesErrorState() = runTest {
        viewModel.onPermissionDenied()
        val state = viewModel.uiState.value
        assertEquals(EveState.ERROR, state.eveState)
        assertFalse(state.isMicPermissionGranted)
        assertNotNull(state.errorMessage)
    }

    @Test
    fun onPermissionGranted_startsVoiceInteraction() = runTest {
        viewModel.onPermissionGranted()
        assertTrue(viewModel.uiState.value.isMicPermissionGranted)
        assertTrue(fakeSpeechEngine.startListeningCalled)
        assertEquals(EveState.LISTENING, viewModel.uiState.value.eveState)
    }

    @Test
    fun speechEvent_readyForSpeech_transitionsToListening() = runTest {
        fakeSpeechEngine.emitEvent(SpeechEvent.ReadyForSpeech(true))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(EveState.LISTENING, state.eveState)
        assertEquals("Listening... Speak now", state.statusMessage)
    }

    @Test
    fun speechEvent_rmsChanged_modulatesAudioLevel() = runTest {
        fakeSpeechEngine.emitEvent(SpeechEvent.RmsChanged(6.0f, 0.67f))
        advanceUntilIdle()

        assertEquals(0.67f, viewModel.uiState.value.audioRmsLevel, 0.01f)
    }

    @Test
    fun speechEvent_partialResult_streamsTranscriptToUi() = runTest {
        fakeSpeechEngine.emitEvent(SpeechEvent.PartialResult("Hello Eve"))
        advanceUntilIdle()

        assertEquals("Hello Eve", viewModel.uiState.value.partialTranscript)
        assertEquals("Transcribing voice input...", viewModel.uiState.value.statusMessage)
    }

    @Test
    fun speechEvent_endOfSpeech_transitionsToThinking() = runTest {
        fakeSpeechEngine.emitEvent(SpeechEvent.EndOfSpeech)
        advanceUntilIdle()

        assertEquals(EveState.THINKING, viewModel.uiState.value.eveState)
        assertEquals(0f, viewModel.uiState.value.audioRmsLevel, 0.01f)
    }

    @Test
    fun speechEvent_finalResult_queriesAiAndSpeaksResponse() = runTest {
        fakeSpeechEngine.emitEvent(SpeechEvent.FinalResult("What is quantum computing?"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("What is quantum computing?", state.lastUserQuery)
        assertNull(state.partialTranscript)
        assertNotNull(state.lastInteractionTime)
        assertEquals("Good day, I am EVE.", state.lastEveResponse)
        assertEquals("groq", state.activeProvider)
        assertEquals("llama-3.3-70b-versatile", state.activeModel)
        assertEquals(280L, state.providerLatencyMs)
        assertEquals(0, state.fallbackCount)
        assertEquals("Good day, I am EVE.", fakeTtsEngine.lastSpokenText)
    }

    @Test
    fun aiFallback_showsFallbackProviderAttribution() = runTest {
        fakeAssistantRepo.simulatedResult = AssistantResult.Success(
            InteractResponse(
                text = "Fallback answer from Ollama.",
                provider = "ollama",
                model = "qwen2.5:7b",
                latencyMs = 4200,
                fallbackCount = 3,
                fallbackHistory = listOf(
                    FallbackStep("groq", "llama-3.3-70b-versatile", "429 Rate Limit", "RATE_LIMIT", 120),
                    FallbackStep("gemini", "gemini-2.0-flash", "503 Unavailable", "SERVER_ERROR", 200),
                    FallbackStep("openrouter", "meta-llama/llama-3.3-70b-instruct:free", "Timeout", "TIMEOUT", 5000)
                ),
                tokens = TokenUsage(15, 20, 35, false)
            )
        )

        viewModel.processAiQuery("Tell me a fact")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Fallback answer from Ollama.", state.lastEveResponse)
        assertEquals("ollama", state.activeProvider)
        assertEquals("qwen2.5:7b", state.activeModel)
        assertEquals(3, state.fallbackCount)
        assertEquals("Speaking response...", state.statusMessage)
        assertEquals("Fallback answer from Ollama.", fakeTtsEngine.lastSpokenText)
    }

    @Test
    fun aiError_showsHonestFailureAndDoesNotSpeakFakeAi() = runTest {
        fakeAssistantRepo.simulatedResult = AssistantResult.Error(
            message = "All AI providers failed. Rate limits active.",
            fallbackHistory = listOf(
                FallbackStep("groq", "default", "429", "RATE_LIMIT", 50)
            )
        )

        viewModel.processAiQuery("What is the weather?")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(EveState.ERROR, state.eveState)
        assertTrue(state.errorMessage!!.contains("All AI providers failed"))
        assertEquals("All Providers Unavailable", state.statusMessage)
    }

    @Test
    fun speechEvent_error_transitionsToErrorState() = runTest {
        fakeSpeechEngine.emitEvent(SpeechEvent.Error(7, "No speech detected"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(EveState.ERROR, state.eveState)
        assertEquals("No speech detected", state.errorMessage)
    }

    @Test
    fun ttsEvent_started_transitionsToSpeaking() = runTest {
        fakeTtsEngine.emitEvent(TtsEvent.Started("utt-1"))
        advanceUntilIdle()

        assertEquals(EveState.SPEAKING, viewModel.uiState.value.eveState)
        assertEquals("Speaking response...", viewModel.uiState.value.statusMessage)
    }

    @Test
    fun ttsEvent_done_returnsToIdle() = runTest {
        fakeTtsEngine.emitEvent(TtsEvent.Started("utt-1"))
        advanceUntilIdle()
        assertEquals(EveState.SPEAKING, viewModel.uiState.value.eveState)

        fakeTtsEngine.emitEvent(TtsEvent.Done("utt-1"))
        advanceUntilIdle()
        assertEquals(EveState.IDLE, viewModel.uiState.value.eveState)
        assertEquals("Neural Engine Ready", viewModel.uiState.value.statusMessage)
    }

    @Test
    fun micClicked_duringListening_cancelsAndReturnsToIdle() = runTest {
        viewModel.onMicClicked(hasPermission = true)
        assertEquals(EveState.LISTENING, viewModel.uiState.value.eveState)

        viewModel.onMicClicked(hasPermission = true)
        assertTrue(fakeSpeechEngine.stopListeningCalled)
        assertEquals(EveState.IDLE, viewModel.uiState.value.eveState)
    }

    @Test
    fun micClicked_duringSpeaking_interruptsTts() = runTest {
        fakeTtsEngine.emitEvent(TtsEvent.Started("utt-1"))
        advanceUntilIdle()
        assertEquals(EveState.SPEAKING, viewModel.uiState.value.eveState)

        viewModel.onMicClicked(hasPermission = true)
        assertTrue(fakeTtsEngine.stopCalled)
        assertEquals(EveState.IDLE, viewModel.uiState.value.eveState)
    }

    @Test
    fun onAppBackgrounded_stopsActiveVoiceSessions() = runTest {
        viewModel.onMicClicked(hasPermission = true)
        assertEquals(EveState.LISTENING, viewModel.uiState.value.eveState)

        viewModel.onAppBackgrounded()
        assertTrue(fakeSpeechEngine.cancelCalled)
        assertTrue(fakeTtsEngine.stopCalled)
        assertEquals(EveState.IDLE, viewModel.uiState.value.eveState)
    }

    @Test
    fun testTtsSpeech_speaksExplicitTestUtterance() = runTest {
        viewModel.testTtsSpeech()
        assertEquals(EveState.THINKING, viewModel.uiState.value.eveState)
        assertNotNull(fakeTtsEngine.lastSpokenText)
        assertTrue(fakeTtsEngine.lastSpokenText!!.contains("operational"))
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
