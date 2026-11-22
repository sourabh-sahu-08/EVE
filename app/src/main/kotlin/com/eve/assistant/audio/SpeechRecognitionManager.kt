package com.eve.assistant.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Production-ready speech recognition manager interfacing with Android SpeechRecognizer.
 * Guarantees main thread invocation, handles lifecycle events, manages audio RMS modulation,
 * and formats error codes into actionable telemetry.
 */
class SpeechRecognitionManager(private val context: Context) : SpeechEngine {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null

    private val _status = MutableStateFlow(RecognitionStatus.IDLE)
    override val status: StateFlow<RecognitionStatus> = _status.asStateFlow()

    private val _events = MutableSharedFlow<SpeechEvent>(extraBufferCapacity = 64)
    override val events: SharedFlow<SpeechEvent> = _events.asSharedFlow()

    private val _audioRms = MutableStateFlow(0f)
    override val audioRms: StateFlow<Float> = _audioRms.asStateFlow()

    private var isSessionActive = false

    /**
     * Checks if speech recognition services (e.g., Google Speech Services) are available.
     */
    override fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    /**
     * Starts listening for speech in the specified or device default language.
     * Supports multi-language recognition with primary support for English and Hindi.
     */
    override fun startListening(languageTag: String?) {
        runOnMainThread {
            if (isSessionActive) {
                stopListening()
            }

            if (!isAvailable()) {
                _status.value = RecognitionStatus.ERROR
                _events.tryEmit(
                    SpeechEvent.Error(
                        errorCode = SpeechRecognizer.ERROR_CLIENT,
                        message = "Speech recognition service is not available on this device"
                    )
                )
                return@runOnMainThread
            }

            ensureRecognizer()

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)

                val targetLanguage = languageTag ?: Locale.getDefault().toLanguageTag()
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, targetLanguage)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, targetLanguage)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, targetLanguage)
            }

            try {
                _status.value = RecognitionStatus.PREPARING
                isSessionActive = true
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                isSessionActive = false
                _status.value = RecognitionStatus.ERROR
                _events.tryEmit(
                    SpeechEvent.Error(
                        errorCode = SpeechRecognizer.ERROR_CLIENT,
                        message = e.localizedMessage ?: "Failed to start speech recognition"
                    )
                )
            }
        }
    }

    /**
     * Requests the speech recognizer to stop recording audio and begin finalizing results.
     */
    override fun stopListening() {
        runOnMainThread {
            if (isSessionActive) {
                try {
                    speechRecognizer?.stopListening()
                    _status.value = RecognitionStatus.PROCESSING
                } catch (_: Exception) {
                }
            }
        }
    }

    override fun cancel() {
        runOnMainThread {
            if (isSessionActive) {
                try {
                    speechRecognizer?.cancel()
                } catch (_: Exception) {
                }
                isSessionActive = false
                _status.value = RecognitionStatus.IDLE
                _audioRms.value = 0f
            }
        }
    }

    override fun destroy() {
        runOnMainThread {
            try {
                speechRecognizer?.destroy()
            } catch (_: Exception) {
            }
            speechRecognizer = null
            isSessionActive = false
            _status.value = RecognitionStatus.IDLE
            _audioRms.value = 0f
        }
    }

    private fun ensureRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }
        }
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _status.value = RecognitionStatus.READY
                _events.tryEmit(SpeechEvent.ReadyForSpeech(true))
            }

            override fun onBeginningOfSpeech() {
                _status.value = RecognitionStatus.LISTENING
                _events.tryEmit(SpeechEvent.BeginningOfSpeech)
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Typical Android rmsdB range is -2dB to +10dB
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                _audioRms.value = normalized
                _events.tryEmit(SpeechEvent.RmsChanged(rmsdB, normalized))
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _status.value = RecognitionStatus.PROCESSING
                _audioRms.value = 0f
                _events.tryEmit(SpeechEvent.EndOfSpeech)
            }

            override fun onError(error: Int) {
                isSessionActive = false
                _status.value = RecognitionStatus.ERROR
                _audioRms.value = 0f
                val errorMessage = resolveErrorMessage(error)
                _events.tryEmit(SpeechEvent.Error(error, errorMessage))
            }

            override fun onResults(results: Bundle?) {
                isSessionActive = false
                _status.value = RecognitionStatus.IDLE
                _audioRms.value = 0f

                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val recognizedText = matches?.firstOrNull().orEmpty()

                if (recognizedText.isNotBlank()) {
                    _events.tryEmit(SpeechEvent.FinalResult(recognizedText))
                } else {
                    _status.value = RecognitionStatus.ERROR
                    _events.tryEmit(
                        SpeechEvent.Error(
                            errorCode = SpeechRecognizer.ERROR_NO_MATCH,
                            message = "No speech detected. Please speak clearly."
                        )
                    )
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull()
                if (!text.isNullOrBlank()) {
                    _events.tryEmit(SpeechEvent.PartialResult(text))
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun resolveErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please check your microphone."
            SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
            SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition."
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timed out. Please check your connection."
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak again."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer is busy. Please try again."
            SpeechRecognizer.ERROR_SERVER -> "Recognition server error encountered."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input detected within timeout."
            SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED -> "Selected language is not supported."
            SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE -> "Language data is currently unavailable."
            SpeechRecognizer.ERROR_SERVER_DISCONNECTED -> "Recognition server disconnected."
            SpeechRecognizer.ERROR_TOO_MANY_REQUESTS -> "Too many requests. Please wait a moment."
            else -> "Speech recognition error code: $errorCode"
        }
    }

    private fun runOnMainThread(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            mainHandler.post(action)
        }
    }
}
