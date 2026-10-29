package ke.eldohub.training.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isReading = MutableStateFlow(false)
    val isReading: StateFlow<Boolean> = _isReading.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _currentUtteranceId.value = utteranceId
                        _isReading.value = true
                    }
                    override fun onDone(utteranceId: String?) {
                        _isReading.value = false
                    }
                    override fun onError(utteranceId: String?) {
                        _isReading.value = false
                    }
                })
                isInitialized = true
            }
        }
    }

    fun speak(text: String, utteranceId: String = "default") {
        if (isInitialized) {
            val chunks = text.chunked(4000)
            chunks.forEachIndexed { index, chunk ->
                val mode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                tts?.speak(chunk, mode, null, "${utteranceId}_$index")
            }
        }
    }

    fun speakQueue(text: String, id: String, isFirst: Boolean) {
        if (isInitialized) {
            val mode = if (isFirst) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(text, mode, null, id)
        }
    }

    fun setSpeechRate(rate: Float) {
        if (isInitialized) {
            tts?.setSpeechRate(rate)
        }
    }

    fun stop() {
        tts?.stop()
        _isReading.value = false
        _currentUtteranceId.value = null
    }

    fun shutdown() {
        tts?.shutdown()
    }
}
