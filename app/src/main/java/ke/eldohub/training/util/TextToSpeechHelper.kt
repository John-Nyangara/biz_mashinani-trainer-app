package ke.eldohub.training.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TextToSpeechHelper(
    context: Context,
    private val onUtteranceStarted: (String) -> Unit = {}
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        utteranceId?.let { onUtteranceStarted(it) }
                    }
                    override fun onDone(utteranceId: String?) {}
                    override fun onError(utteranceId: String?) {}
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

    /**
     * Specifically for speech chunks with IDs that map back to PDF locations.
     */
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
    }

    fun shutdown() {
        tts?.shutdown()
    }
}
