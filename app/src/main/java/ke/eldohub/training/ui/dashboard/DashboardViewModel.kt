package ke.eldohub.training.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.eldohub.training.data.repo.Manual
import ke.eldohub.training.data.repo.ManualRepository
import ke.eldohub.training.sync.SyncConfig
import ke.eldohub.training.util.NetworkHelper
import ke.eldohub.training.util.SpeechManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

data class DashboardUiState(
    val totalManuals: Int = 0,
    val offlineManuals: Int = 0,
    val recentManuals: List<Manual> = emptyList(),
    val isSyncing: Boolean = false,
    val syncMessage: String? = null,
    val isNetworkError: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val manualRepository: ManualRepository,
    private val speechManager: SpeechManager,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val syncing = MutableStateFlow(false)
    private val syncMessage = MutableStateFlow<String?>(null)
    private val isNetworkError = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> = combine(
        manualRepository.observeManuals(),
        manualRepository.observeRecentlyViewedManuals(3),
        syncing,
        syncMessage,
        isNetworkError
    ) { manuals, recent, isSyncing, message, netError ->
        DashboardUiState(
            totalManuals = manuals.size,
            offlineManuals = manuals.count { it.isAvailable },
            recentManuals = recent,
            isSyncing = isSyncing,
            syncMessage = message,
            isNetworkError = netError
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )

    val isReading = speechManager.isReading

    fun syncNow() {
        if (syncing.value) return

        if (!networkHelper.isOnline()) {
            isNetworkError.value = true
            syncMessage.value = "No internet connection. Please connect to the internet to synchronize training manuals."
            return
        }

        viewModelScope.launch {
            syncing.value = true
            syncMessage.value = null
            isNetworkError.value = false
            try {
                val count = manualRepository.syncFromManifest(SyncConfig.MANIFEST_URL)
                syncMessage.value = if (count > 0) "$count manual(s) updated" else "Up to date"
            } catch (e: Exception) {
                if (e is UnknownHostException || e is SocketTimeoutException || e is ConnectException || e is IOException || !networkHelper.isOnline()) {
                    isNetworkError.value = true
                    syncMessage.value = "No internet connection. Please connect to the internet to synchronize training manuals."
                } else {
                    isNetworkError.value = false
                    syncMessage.value = "Sync failed: ${e.message ?: "Please check your connection and try again."}"
                }
            } finally {
                syncing.value = false
            }
        }
    }

    fun startReading(text: String) {
        speechManager.speak(text)
    }

    fun stopReading() {
        speechManager.stop()
    }

    fun clearSyncMessage() {
        syncMessage.value = null
        isNetworkError.value = false
    }
}
