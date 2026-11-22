package com.eve.assistant.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.eve.assistant.ui.settings.VoicePersonality
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID

sealed interface TtsEvent {
    data class Started(val utteranceId: String) : TtsEvent
    data class Done(val utteranceId: String) : TtsEvent
    data class Error(val utteranceId: String, val errorCode: Int) : TtsEvent
}

/**
 * Robust Text-to-Speech engine wrapper managing initialization, voice selection,
 * speech synthesis, and interruption lifecycle.
 */
class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener, TextToSpeechEngine {

    private var tts: TextToSpeech? = null

    private val _isInitialized = MutableStateFlow(false)
    override val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _events = MutableSharedFlow<TtsEvent>(extraBufferCapacity = 32)
    override val events: SharedFlow<TtsEvent> = _events.asSharedFlow()

    private var currentRate: Float = 1.0f
    private var currentPitch: Float = 1.0f
    private var selectedPersonality: VoicePersonality = VoicePersonality.NOVA

    init {
        initialize()
    }

    fun initialize() {
        if (tts == null) {
            tts = TextToSpeech(context, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                engine.language = Locale.getDefault()
                engine.setSpeechRate(currentRate)
                engine.setPitch(currentPitch)
                configureVoiceForPersonality(selectedPersonality)
                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                        utteranceId?.let { _events.tryEmit(TtsEvent.Started(it)) }
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        utteranceId?.let { _events.tryEmit(TtsEvent.Done(it)) }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        utteranceId?.let { _events.tryEmit(TtsEvent.Error(it, -1)) }
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _isSpeaking.value = false
                        utteranceId?.let { _events.tryEmit(TtsEvent.Error(it, errorCode)) }
                    }
                })
                _isInitialized.value = true
            }
        } else {
            _isInitialized.value = false
        }
    }

    /**
     * Synthesizes and speaks text using the configured voice profile.
     */
    override fun speak(text: String, utteranceId: String) {
        if (!_isInitialized.value || tts == null) {
            return
        }

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        _isSpeaking.value = true
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    override fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    override fun setSpeechRate(rate: Float) {
        currentRate = rate.coerceIn(0.5f, 2.0f)
        tts?.setSpeechRate(currentRate)
    }

    override fun setPitch(pitch: Float) {
        currentPitch = pitch.coerceIn(0.75f, 1.5f)
        tts?.setPitch(currentPitch)
    }

    override fun setVoicePersonality(personality: VoicePersonality) {
        selectedPersonality = personality
        configureVoiceForPersonality(personality)
    }

    override fun speakTestUtterance() {
        speak("I am EVE, your personal artificial intelligence assistant. Speech synthesis is operational.")
    }

    private fun configureVoiceForPersonality(personality: VoicePersonality) {
        val engine = tts ?: return
        try {
            val availableVoices = engine.voices ?: return
            val currentLocale = Locale.getDefault()

            // Filter voices matching current language
            val localeVoices = availableVoices.filter {
                it.locale.language.equals(currentLocale.language, ignoreCase = true)
            }

            if (localeVoices.isNotEmpty()) {
                val voice: Voice? = when (personality) {
                    VoicePersonality.NOVA -> {
                        // High quality crisp voice
                        localeVoices.find { it.name.contains("en-us-x-sfg", ignoreCase = true) || !it.isNetworkConnectionRequired }
                            ?: localeVoices.firstOrNull()
                    }
                    VoicePersonality.ECHO -> {
                        // Resonant voice
                        localeVoices.find { it.name.contains("male", ignoreCase = true) || it.name.contains("x-iob", ignoreCase = true) }
                            ?: localeVoices.lastOrNull()
                    }
                    VoicePersonality.SOL -> {
                        // Balanced neutral voice
                        localeVoices.find { it.quality >= Voice.QUALITY_HIGH }
                            ?: localeVoices.firstOrNull()
                    }
                    VoicePersonality.AURA -> {
                        // Calming melodic voice
                        localeVoices.find { it.name.contains("female", ignoreCase = true) || it.name.contains("x-sfg", ignoreCase = true) }
                            ?: localeVoices.firstOrNull()
                    }
                }
                voice?.let { engine.voice = it }
            }
        } catch (_: Exception) {
            // Graceful fallback to default engine voice
        }
    }

    /**
     * Safely releases TTS engine and stops playback.
     */
    override fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {
        }
        tts = null
        _isInitialized.value = false
        _isSpeaking.value = false
    }
}
