package ke.eldohub.training.ui.accessibility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.eldohub.training.util.SpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class AccessibilityState(
    val highContrast: Boolean = false,
    val speechRate: Float = 1.0f,
    val reducedMotion: Boolean = false,
    val screenReaderFriendly: Boolean = true
)

@HiltViewModel
class AccessibilityViewModel @Inject constructor(
    private val speechManager: SpeechManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccessibilityState())
    val uiState: StateFlow<AccessibilityState> = _uiState.asStateFlow()

    val isReading = speechManager.isReading
    val currentUtteranceId = speechManager.currentUtteranceId

    init {
        // Sync speech rate immediately
        _uiState.onEach { state ->
            speechManager.setSpeechRate(state.speechRate)
        }.launchIn(viewModelScope)
    }

    fun toggleHighContrast() {
        _uiState.value = _uiState.value.copy(highContrast = !_uiState.value.highContrast)
    }

    fun setSpeechRate(rate: Float) {
        _uiState.value = _uiState.value.copy(speechRate = rate)
        // Rate is applied via the init block's observer
    }

    fun toggleReducedMotion() {
        _uiState.value = _uiState.value.copy(reducedMotion = !_uiState.value.reducedMotion)
    }

    fun startReading(text: String) {
        speechManager.speak(text)
    }

    fun stopReading() {
        speechManager.stop()
    }

    fun speakQueue(text: String, id: String, isFirst: Boolean) {
        speechManager.speakQueue(text, id, isFirst)
    }
}
