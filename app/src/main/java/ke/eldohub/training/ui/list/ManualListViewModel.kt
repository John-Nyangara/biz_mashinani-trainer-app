package ke.eldohub.training.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.eldohub.training.data.repo.Manual
import ke.eldohub.training.data.repo.ManualRepository
import ke.eldohub.training.sync.SyncConfig
import ke.eldohub.training.util.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

data class ManualListUiState(
    val manuals: List<Manual> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val syncMessage: String? = null,
    val isNetworkError: Boolean = false
)

@HiltViewModel
class ManualListViewModel @Inject constructor(
    private val repository: ManualRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val syncing = MutableStateFlow(false)
    private val syncMessage = MutableStateFlow<String?>(null)
    private val isNetworkError = MutableStateFlow(false)

    val uiState: StateFlow<ManualListUiState> =
        combine(
            repository.observeManuals(),
            query,
            syncing,
            syncMessage,
            isNetworkError
        ) { manuals, q, isSyncing, message, netError ->
            val filtered = if (q.isBlank()) manuals
            else manuals.filter { it.title.contains(q, ignoreCase = true) }
            ManualListUiState(
                manuals = filtered,
                query = q,
                isLoading = false,
                isSyncing = isSyncing,
                syncMessage = message,
                isNetworkError = netError
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ManualListUiState()
        )

    init {
        // Seed bundled assets into the DB on first load.
        viewModelScope.launch { repository.seedBundledManuals() }
    }

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    /** Manual "sync now" — checks for active internet connection before proceeding. */
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
                val count = repository.syncFromManifest(SyncConfig.MANIFEST_URL)
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

    fun clearSyncMessage() {
        syncMessage.value = null
        isNetworkError.value = false
    }
}
