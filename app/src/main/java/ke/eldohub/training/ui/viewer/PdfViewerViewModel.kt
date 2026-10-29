package ke.eldohub.training.ui.viewer

import android.app.ActivityManager
import android.content.Context
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ke.eldohub.training.data.repo.ManualRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

sealed interface PdfViewerUiState {
    data object Loading : PdfViewerUiState
    data class Ready(
        val renderer: PdfPageRenderer,
        val pageCount: Int,
        val title: String,
        val fullText: String? = null,
        val speechChunks: List<SpeechChunk> = emptyList()
    ) : PdfViewerUiState
    data class Error(val message: String) : PdfViewerUiState
}

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    private val repository: ManualRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<PdfViewerUiState>(PdfViewerUiState.Loading)
    val uiState: StateFlow<PdfViewerUiState> = _uiState.asStateFlow()

    private val _currentChunkId = MutableStateFlow<String?>(null)
    val currentChunkId: StateFlow<String?> = _currentChunkId.asStateFlow()

    private val _discoveredChunks = MutableStateFlow<List<SpeechChunk>>(emptyList())

    private var renderer: PdfPageRenderer? = null
    private var prefetchJob: Job? = null
    private var crawlerJob: Job? = null
    private var savedWidthPx: Int = 0

    fun onSpeechChunkStarted(id: String) {
        _currentChunkId.value = id
    }

    fun stopReading() {
        _currentChunkId.value = null
    }

    fun startReadingFrom(chunkId: String) {
        _currentChunkId.value = chunkId
    }

    fun load(manualId: String) {
        if (_uiState.value is PdfViewerUiState.Ready) return

        viewModelScope.launch {
            try {
                val file = repository.resolvePdfFile(manualId) ?: run {
                    _uiState.value = PdfViewerUiState.Error("Manual file not found.")
                    return@launch
                }

                val activityManager = context.getSystemService<ActivityManager>()
                val isLowRam = activityManager?.isLowRamDevice == true
                val maxCacheSize = if (isLowRam) 6 else 12

                val r = PdfPageRenderer(file, maxCacheSize)
                renderer = r

                val entity = repository.getManual(manualId)
                val title = entity?.title ?: "Manual"
                
                repository.markManualAsViewed(manualId)

                // Lazy extraction: Start crawler
                startCrawler(r)

                val estimatedWidth = context.resources.displayMetrics.widthPixels.coerceAtMost(2048)
                savedWidthPx = estimatedWidth
                prefetchJob = viewModelScope.launch {
                    r.prefetchPagesLowRes(0, 4)
                    r.prefetchPages(0, estimatedWidth, 4)
                }

                // Combine discovered chunks into UI state
                _discoveredChunks.onEach { chunks ->
                    val s = _uiState.value
                    if (s is PdfViewerUiState.Ready) {
                        _uiState.value = s.copy(speechChunks = chunks)
                    } else {
                        _uiState.value = PdfViewerUiState.Ready(
                            renderer = r,
                            pageCount = r.pageCount,
                            title = title,
                            fullText = if (chunks.isEmpty()) entity?.textContent else null,
                            speechChunks = chunks
                        )
                    }
                }.launchIn(viewModelScope)

            } catch (e: Exception) {
                _uiState.value = PdfViewerUiState.Error("Could not open PDF: ${e.message}")
            }
        }
    }

    private fun startCrawler(r: PdfPageRenderer) {
        crawlerJob?.cancel()
        crawlerJob = viewModelScope.launch {
            for (i in 0 until r.pageCount) {
                // Check if already processed (maybe by ensurePageProcessed)
                if (_discoveredChunks.value.any { it.pageIndex == i }) continue
                
                val chunks = r.getSpeechChunksForPage(i)
                _discoveredChunks.update { current ->
                    (current + chunks).sortedBy { it.pageIndex }
                }
                yield() // Prioritize UI interactions
            }
        }
    }

    /**
     * Prioritizes a specific page for OCR (e.g. if user is looking at it or double-tapped).
     */
    fun ensurePageProcessed(pageIndex: Int) {
        val r = renderer ?: return
        if (_discoveredChunks.value.any { it.pageIndex == pageIndex }) return // already done
        
        viewModelScope.launch {
            val chunks = r.getSpeechChunksForPage(pageIndex)
            _discoveredChunks.update { current ->
                if (current.any { it.pageIndex == pageIndex }) current
                else (current + chunks).sortedBy { it.pageIndex }
            }
        }
    }

    /**
     * Called once the composable knows the pixel width it will render at.
     * If the estimated width from load() already matches, this is a no-op (cache hits).
     * On rotation or multi-window resize, cancels the old job and restarts at the new width.
     */
    fun warmUp(widthPx: Int) {
        if (widthPx <= 0) return
        val r = renderer ?: return
        if (widthPx.coerceAtMost(2048) == savedWidthPx) return  // already warming at this width
        prefetchJob?.cancel()
        savedWidthPx = widthPx.coerceAtMost(2048)
        prefetchJob = viewModelScope.launch {
            r.prefetchPagesLowRes(0, 4)
            r.prefetchPages(0, widthPx, 4)
        }
    }

    override fun onCleared() {
        super.onCleared()
        renderer?.close()
        renderer = null
    }
}
