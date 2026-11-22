package com.eve.assistant.ui.home

import com.eve.assistant.audio.RecognitionStatus
import com.eve.assistant.audio.SpeechEngine
import com.eve.assistant.audio.SpeechEvent
import com.eve.assistant.audio.TextToSpeechEngine
import com.eve.assistant.audio.TtsEvent
import com.eve.assistant.data.model.AssistantResult
import com.eve.assistant.data.model.ChatMessage
import com.eve.assistant.data.model.FallbackStep
import com.eve.assistant.data.model.InteractResponse
import com.eve.assistant.data.model.TokenUsage
import com.eve.assistant.data.repository.AssistantRepository
import com.eve.assistant.ui.settings.VoicePersonality
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class FakeSpeechEngine(
    var available: Boolean = true
) : SpeechEngine {

    private val _status = MutableStateFlow(RecognitionStatus.IDLE)
    override val status: StateFlow<RecognitionStatus> = _status.asStateFlow()

    private val _events = MutableSharedFlow<SpeechEvent>(extraBufferCapacity = 64)
    override val events: SharedFlow<SpeechEvent> = _events.asSharedFlow()

    private val _audioRms = MutableStateFlow(0f)
    override val audioRms: StateFlow<Float> = _audioRms.asStateFlow()

    var startListeningCalled = false
    var stopListeningCalled = false
    var cancelCalled = false
    var destroyCalled = false
    var lastLanguage: String? = null

    override fun isAvailable(): Boolean = available

    override fun startListening(languageTag: String?) {
        startListeningCalled = true
        lastLanguage = languageTag
        _status.value = RecognitionStatus.LISTENING
    }

    override fun stopListening() {
        stopListeningCalled = true
        _status.value = RecognitionStatus.PROCESSING
    }

    override fun cancel() {
        cancelCalled = true
        _status.value = RecognitionStatus.IDLE
        _audioRms.value = 0f
    }

    override fun destroy() {
        destroyCalled = true
        _status.value = RecognitionStatus.IDLE
    }

    suspend fun emitEvent(event: SpeechEvent) {
        _events.emit(event)
    }
}

class FakeTextToSpeechEngine : TextToSpeechEngine {

    private val _isInitialized = MutableStateFlow(true)
    override val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _events = MutableSharedFlow<TtsEvent>(extraBufferCapacity = 32)
    override val events: SharedFlow<TtsEvent> = _events.asSharedFlow()

    var lastSpokenText: String? = null
    var stopCalled = false
    var shutdownCalled = false
    var currentRate: Float = 1.0f
    var currentPitch: Float = 1.0f
    var selectedPersonality: VoicePersonality = VoicePersonality.NOVA

    override fun speak(text: String, utteranceId: String) {
        lastSpokenText = text
        _isSpeaking.value = true
        _events.tryEmit(TtsEvent.Started(utteranceId))
    }

    override fun stop() {
        stopCalled = true
        _isSpeaking.value = false
    }

    override fun setSpeechRate(rate: Float) {
        currentRate = rate
    }

    override fun setPitch(pitch: Float) {
        currentPitch = pitch
    }

    override fun setVoicePersonality(personality: VoicePersonality) {
        selectedPersonality = personality
    }

    override fun speakTestUtterance() {
        speak("I am EVE, your personal artificial intelligence assistant. Speech synthesis is operational.")
    }

    override fun shutdown() {
        shutdownCalled = true
        _isSpeaking.value = false
    }

    suspend fun emitEvent(event: TtsEvent) {
        _events.emit(event)
    }
}

class FakeAssistantRepository(
    var simulatedResult: AssistantResult = AssistantResult.Success(
        InteractResponse(
            text = "Good day, I am EVE.",
            provider = "groq",
            model = "llama-3.3-70b-versatile",
            latencyMs = 280,
            fallbackCount = 0,
            fallbackHistory = emptyList(),
            tokens = TokenUsage(10, 15, 25, false)
        )
    )
) : AssistantRepository {

    private var url: String = "http://127.0.0.1:3000"
    var lastQuery: String? = null
    var queryCount = 0

    override fun setServerUrl(url: String) {
        this.url = url
    }

    override fun getServerUrl(): String = url

    override suspend fun interact(
        message: String,
        history: List<ChatMessage>
    ): AssistantResult {
        lastQuery = message
        queryCount++
        return simulatedResult
    }
}
