package com.eve.assistant.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eve.assistant.audio.RecognitionStatus
import com.eve.assistant.audio.SpeechEngine
import com.eve.assistant.audio.SpeechEvent
import com.eve.assistant.audio.SpeechRecognitionManager
import com.eve.assistant.audio.TextToSpeechEngine
import com.eve.assistant.audio.TextToSpeechManager
import com.eve.assistant.audio.TtsEvent
import com.eve.assistant.data.model.AssistantResult
import com.eve.assistant.data.model.ChatMessage
import com.eve.assistant.data.repository.AssistantRepository
import com.eve.assistant.data.repository.DefaultAssistantRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * ViewModel orchestrating real voice interaction (STT and TTS) and multi-provider AI routing for EVE.
 * Enforces explicit finite state transitions, handles audio RMS modulation,
 * coordinates bounded fallback (Groq -> Gemini -> OpenRouter -> Ollama), and safeguards lifecycle.
 */
class HomeViewModel(
    private var speechManager: SpeechEngine? = null,
    private var ttsManager: TextToSpeechEngine? = null,
    private var assistantRepository: AssistantRepository = DefaultAssistantRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            greeting = resolveGreeting(),
            statusMessage = "Neural Engine Ready"
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var speechJob: Job? = null
    private var ttsJob: Job? = null
    private var aiJob: Job? = null
    private val conversationHistory = mutableListOf<ChatMessage>()

    init {
        speechManager?.let { bindSpeechManager(it) }
        ttsManager?.let { bindTtsManager(it) }
    }

    /**
     * Attaches speech and TTS managers dynamically from UI context if not injected via constructor.
     */
    fun attachContext(context: Context) {
        val appContext = context.applicationContext
        if (speechManager == null) {
            val sm = SpeechRecognitionManager(appContext)
            speechManager = sm
            bindSpeechManager(sm)
            _uiState.update { it.copy(isRecognitionAvailable = sm.isAvailable()) }
        }
        if (ttsManager == null) {
            val tm = TextToSpeechManager(appContext)
            ttsManager = tm
            bindTtsManager(tm)
        }
    }

    /**
     * Injects custom or mock engines for unit testing.
     */
    fun setManagers(
        speech: SpeechEngine,
        tts: TextToSpeechEngine,
        repository: AssistantRepository = DefaultAssistantRepository()
    ) {
        speechJob?.cancel()
        ttsJob?.cancel()
        aiJob?.cancel()
        this.speechManager = speech
        this.ttsManager = tts
        this.assistantRepository = repository
        bindSpeechManager(speech)
        bindTtsManager(tts)
    }

    fun setServerUrl(url: String) {
        assistantRepository.setServerUrl(url)
    }

    private fun bindSpeechManager(sm: SpeechEngine) {
        speechJob?.cancel()
        speechJob = viewModelScope.launch {
            sm.events.collect { event ->
                handleSpeechEvent(event)
            }
        }
        _uiState.update { it.copy(isRecognitionAvailable = sm.isAvailable()) }
    }

    private fun bindTtsManager(tm: TextToSpeechEngine) {
        ttsJob?.cancel()
        ttsJob = viewModelScope.launch {
            tm.events.collect { event ->
                handleTtsEvent(event)
            }
        }
    }

    private fun handleSpeechEvent(event: SpeechEvent) {
        when (event) {
            is SpeechEvent.ReadyForSpeech -> {
                _uiState.update {
                    it.copy(
                        eveState = EveState.LISTENING,
                        statusMessage = "Listening... Speak now",
                        errorMessage = null,
                        partialTranscript = null
                    )
                }
            }
            is SpeechEvent.BeginningOfSpeech -> {
                _uiState.update {
                    it.copy(
                        eveState = EveState.LISTENING,
                        statusMessage = "Capturing voice input..."
                    )
                }
            }
            is SpeechEvent.RmsChanged -> {
                _uiState.update {
                    it.copy(audioRmsLevel = event.normalizedLevel)
                }
            }
            is SpeechEvent.PartialResult -> {
                _uiState.update {
                    it.copy(
                        partialTranscript = event.text,
                        statusMessage = "Transcribing voice input..."
                    )
                }
            }
            is SpeechEvent.EndOfSpeech -> {
                _uiState.update {
                    it.copy(
                        eveState = EveState.THINKING,
                        statusMessage = "Routing to neural cloud...",
                        audioRmsLevel = 0f
                    )
                }
            }
            is SpeechEvent.FinalResult -> {
                val transcript = event.text
                val currentTime = formatCurrentTime()
                _uiState.update {
                    it.copy(
                        eveState = EveState.THINKING,
                        statusMessage = "Consulting neural intelligence...",
                        lastUserQuery = transcript,
                        partialTranscript = null,
                        lastInteractionTime = currentTime,
                        audioRmsLevel = 0f,
                        errorMessage = null
                    )
                }
                processAiQuery(transcript)
            }
            is SpeechEvent.Error -> {
                _uiState.update {
                    it.copy(
                        eveState = EveState.ERROR,
                        errorMessage = event.message,
                        statusMessage = "Recognition Error",
                        partialTranscript = null,
                        audioRmsLevel = 0f
                    )
                }
            }
        }
    }

    fun processAiQuery(query: String) {
        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    eveState = EveState.THINKING,
                    statusMessage = "Synthesizing answer..."
                )
            }

            when (val result = assistantRepository.interact(query, conversationHistory.toList())) {
                is AssistantResult.Success -> {
                    val resp = result.response
                    conversationHistory.add(ChatMessage(role = "user", content = query))
                    conversationHistory.add(ChatMessage(role = "assistant", content = resp.text))
                    if (conversationHistory.size > 20) {
                        conversationHistory.removeAt(0)
                        conversationHistory.removeAt(0)
                    }

                    val providerTag = if (resp.fallbackCount > 0) {
                        "Fallback: ${resp.provider.uppercase()} (${resp.model})"
                    } else {
                        "Served by ${resp.provider.uppercase()} (${resp.model})"
                    }

                    _uiState.update {
                        it.copy(
                            lastEveResponse = resp.text,
                            activeProvider = resp.provider,
                            activeModel = resp.model,
                            providerLatencyMs = resp.latencyMs,
                            fallbackCount = resp.fallbackCount,
                            statusMessage = providerTag,
                            errorMessage = null
                        )
                    }

                    // Speak the response aloud via TTS
                    ttsManager?.speak(resp.text)
                }
                is AssistantResult.Error -> {
                    val fallbackSummary = if (result.fallbackHistory.isNotEmpty()) {
                        " [" + result.fallbackHistory.joinToString(" -> ") { "${it.provider} (${it.category})" } + "]"
                    } else ""

                    _uiState.update {
                        it.copy(
                            eveState = EveState.ERROR,
                            errorMessage = result.message + fallbackSummary,
                            statusMessage = "All Providers Unavailable",
                            lastEveResponse = "I apologize, but all zero-cost AI providers are currently unavailable or in rate-limit cooldown. Please try again shortly."
                        )
                    }
                }
            }
        }
    }

    private fun handleTtsEvent(event: TtsEvent) {
        when (event) {
            is TtsEvent.Started -> {
                _uiState.update {
                    it.copy(
                        eveState = EveState.SPEAKING,
                        statusMessage = "Speaking response..."
                    )
                }
            }
            is TtsEvent.Done -> {
                _uiState.update {
                    it.copy(
                        eveState = EveState.IDLE,
                        statusMessage = "Neural Engine Ready"
                    )
                }
            }
            is TtsEvent.Error -> {
                _uiState.update {
                    it.copy(
                        eveState = EveState.ERROR,
                        errorMessage = "Speech synthesis error (${event.errorCode})",
                        statusMessage = "TTS Error"
                    )
                }
            }
        }
    }

    /**
     * Primary mic interaction handler.
     * Starts listening, interrupts speech, or cancels active processing based on current state.
     */
    fun onMicClicked(
        hasPermission: Boolean = false,
        onRequestPermission: () -> Unit = {}
    ) {
        when (_uiState.value.eveState) {
            EveState.IDLE -> {
                if (!hasPermission) {
                    onRequestPermission()
                    return
                }
                startVoiceInteraction()
            }
            EveState.LISTENING -> {
                // Cancel/stop listening
                speechManager?.stopListening()
                _uiState.update {
                    it.copy(
                        eveState = EveState.IDLE,
                        statusMessage = "Listening stopped",
                        audioRmsLevel = 0f,
                        partialTranscript = null
                    )
                }
            }
            EveState.THINKING -> {
                // Cancel thinking / AI request
                aiJob?.cancel()
                speechManager?.cancel()
                _uiState.update {
                    it.copy(
                        eveState = EveState.IDLE,
                        statusMessage = "Processing cancelled",
                        audioRmsLevel = 0f
                    )
                }
            }
            EveState.SPEAKING -> {
                // Interrupt speech immediately
                ttsManager?.stop()
                _uiState.update {
                    it.copy(
                        eveState = EveState.IDLE,
                        statusMessage = "Speech interrupted"
                    )
                }
            }
            EveState.ERROR -> {
                // Clear error and reset
                _uiState.update {
                    it.copy(
                        eveState = EveState.IDLE,
                        statusMessage = "Neural Engine Ready",
                        errorMessage = null,
                        audioRmsLevel = 0f
                    )
                }
            }
        }
    }

    fun startVoiceInteraction() {
        val sm = speechManager
        if (sm == null || !sm.isAvailable()) {
            _uiState.update {
                it.copy(
                    eveState = EveState.ERROR,
                    statusMessage = "Recognition Unavailable",
                    errorMessage = "Speech recognition service is not available on this device"
                )
            }
            return
        }

        // Interrupt any ongoing TTS before listening
        ttsManager?.stop()

        _uiState.update {
            it.copy(
                eveState = EveState.LISTENING,
                statusMessage = "Initializing microphone...",
                errorMessage = null,
                partialTranscript = null,
                audioRmsLevel = 0f
            )
        }
        sm.startListening()
    }

    fun onPermissionDenied() {
        _uiState.update {
            it.copy(
                isMicPermissionGranted = false,
                eveState = EveState.ERROR,
                statusMessage = "Permission Required",
                errorMessage = "Microphone access is required for voice interaction. Please grant permission."
            )
        }
    }

    fun onPermissionGranted() {
        _uiState.update {
            it.copy(
                isMicPermissionGranted = true,
                errorMessage = null
            )
        }
        startVoiceInteraction()
    }

    /**
     * Synthesizes an explicitly labelled test utterance to test TTS output.
     */
    fun testTtsSpeech() {
        _uiState.update {
            it.copy(
                eveState = EveState.THINKING,
                statusMessage = "Synthesizing test audio..."
            )
        }
        ttsManager?.speakTestUtterance()
    }

    /**
     * Explicit state setter used for testing and UI component inspection.
     */
    fun setEveState(state: EveState) {
        val status = when (state) {
            EveState.IDLE -> "Neural Engine Ready"
            EveState.LISTENING -> "Listening for voice input..."
            EveState.THINKING -> "Synthesizing response..."
            EveState.SPEAKING -> "Streaming audio response..."
            EveState.ERROR -> "Neural stream error encountered"
        }
        val err = if (state == EveState.ERROR) "Speech recognition timed out" else null

        _uiState.update { current ->
            current.copy(
                eveState = state,
                statusMessage = status,
                errorMessage = err,
                audioRmsLevel = if (state == EveState.LISTENING) 0.5f else 0f
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

    /**
     * Cleans up ongoing sessions when app is backgrounded to prevent microphone leakage.
     */
    fun onAppBackgrounded() {
        speechManager?.cancel()
        ttsManager?.stop()
        aiJob?.cancel()
        if (_uiState.value.eveState == EveState.LISTENING || _uiState.value.eveState == EveState.SPEAKING) {
            _uiState.update {
                it.copy(
                    eveState = EveState.IDLE,
                    statusMessage = "Neural Engine Ready",
                    audioRmsLevel = 0f
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechJob?.cancel()
        ttsJob?.cancel()
        aiJob?.cancel()
        speechManager?.destroy()
        ttsManager?.shutdown()
        speechManager = null
        ttsManager = null
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

    private fun formatCurrentTime(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }
}
