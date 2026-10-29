package ke.eldohub.training.ui.viewer

import android.graphics.Bitmap
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material3.ExperimentalMaterial3Api
import ke.eldohub.training.ui.accessibility.AccessibilityViewModel
import ke.eldohub.training.ui.accessibility.AccessibilityDialog
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.toComposeRect
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import ke.eldohub.training.R

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    manualId: String,
    onBack: () -> Unit,
    viewModel: PdfViewerViewModel = hiltViewModel(),
    accessibilityViewModel: AccessibilityViewModel = hiltViewModel()
) {
    LaunchedEffect(manualId) { viewModel.load(manualId) }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val accessibilityState by accessibilityViewModel.uiState.collectAsStateWithLifecycle()
    val isReading by accessibilityViewModel.isReading.collectAsStateWithLifecycle()
    val currentUtteranceId by accessibilityViewModel.currentUtteranceId.collectAsStateWithLifecycle()
    val currentChunkId by viewModel.currentChunkId.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Sync UI state with TTS progress
    LaunchedEffect(currentUtteranceId) {
        currentUtteranceId?.let { viewModel.onSpeechChunkStarted(it) }
    }
    
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    // Stop reading when leaving this screen
    DisposableEffect(Unit) {
        onDispose {
            accessibilityViewModel.stopReading()
            viewModel.stopReading()
        }
    }

    if (showAccessibilityDialog) {
        AccessibilityDialog(
            state = accessibilityState,
            onToggleHighContrast = { accessibilityViewModel.toggleHighContrast() },
            onSetSpeechRate = { accessibilityViewModel.setSpeechRate(it) },
            onToggleReducedMotion = { accessibilityViewModel.toggleReducedMotion() },
            onDismiss = { showAccessibilityDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (val s = state) {
                            is PdfViewerUiState.Ready -> s.title
                            else -> stringResource(R.string.viewer)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.go_back))
                    }
                },
                actions = {
                    IconButton(onClick = { showAccessibilityDialog = true }) {
                        Icon(Icons.Default.Accessibility, contentDescription = "Accessibility Hub")
                    }
                    
                    if (state is PdfViewerUiState.Ready) {
                        val readyState = state as PdfViewerUiState.Ready
                        
                        // Structured speech if available (Android 15+ or extracted)
                        val hasChunks = readyState.speechChunks.isNotEmpty()
                        val hasFullText = hasChunks || !readyState.fullText.isNullOrBlank()
                        
                        IconButton(onClick = {
                            if (isReading) {
                                accessibilityViewModel.stopReading()
                                viewModel.stopReading()
                            } else {
                                if (hasChunks) {
                                    readyState.speechChunks.forEachIndexed { i, chunk ->
                                        accessibilityViewModel.speakQueue(chunk.text, chunk.id, isFirst = i == 0)
                                    }
                                } else {
                                    val fallbackText = readyState.fullText ?: readyState.title
                                    accessibilityViewModel.startReading(fallbackText)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (isReading) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isReading) stringResource(R.string.stop_reading) else stringResource(R.string.read_aloud),
                                tint = if (hasFullText) {
                                    if (MaterialTheme.colorScheme.surface == Color.Black) Color.Yellow else MaterialTheme.colorScheme.primary
                                } else Color.Gray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            when (val s = state) {
                is PdfViewerUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is PdfViewerUiState.Error ->
                    Text(
                        text = s.message,
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        color = MaterialTheme.colorScheme.error
                    )

                is PdfViewerUiState.Ready -> {
                    val activeChunk by remember {
                        derivedStateOf<SpeechChunk?> { s.speechChunks.find { it.id == currentChunkId } }
                    }
                    PdfPageList(
                        renderer = s.renderer,
                        pageCount = s.pageCount,
                        activeChunk = activeChunk,
                        highContrast = accessibilityState.highContrast,
                        onRenderWidthKnown = viewModel::warmUp,
                        chunks = s.speechChunks,
                        onDoubleTap = { chunk ->
                            accessibilityViewModel.stopReading()
                            viewModel.startReadingFrom(chunk.id)
                            val startIndex = s.speechChunks.indexOf(chunk)
                            s.speechChunks.drop(startIndex).forEachIndexed { i, c ->
                                accessibilityViewModel.speakQueue(c.text, c.id, isFirst = i == 0)
                            }
                        },
                        onPageVisible = viewModel::ensurePageProcessed
                    )
                }
            }
        }
    }
}

@Composable
private fun PdfPageList(
    renderer: PdfPageRenderer,
    pageCount: Int,
    activeChunk: SpeechChunk?,
    highContrast: Boolean,
    onRenderWidthKnown: (Int) -> Unit,
    onDoubleTap: (SpeechChunk) -> Unit,
    onPageVisible: (Int) -> Unit,
    chunks: List<SpeechChunk>
) {
    val listState = rememberLazyListState()
    var containerWidthPx by remember { mutableStateOf(0) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    // Auto-scroll to current page only when page changes and is not visible
    val activePageIndex by remember { derivedStateOf { activeChunk?.pageIndex } }
    LaunchedEffect(activePageIndex) {
        activePageIndex?.let { pageIndex ->
            val isVisible = listState.layoutInfo.visibleItemsInfo.any { it.index == pageIndex }
            if (!isVisible) {
                listState.animateScrollToItem(pageIndex)
            }
        }
    }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 3f)
        offset += offsetChange
    }

    val firstVisiblePage by remember { derivedStateOf { (listState.firstVisibleItemIndex + 1).coerceAtMost(pageCount) } }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .onSizeChanged { size ->
                    containerWidthPx = size.width
                    if (size.width > 0) onRenderWidthKnown(size.width)
                }
                .transformable(state = transformState)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = if (scale > 1f) offset.x else 0f,
                    translationY = if (scale > 1f) offset.y else 0f
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 72.dp)
        ) {
            items(count = pageCount, key = { it }) { pageIndex ->
                // Mark page as visible for OCR prioritization
                SideEffect { onPageVisible(pageIndex) }

                PdfPage(
                    renderer = renderer,
                    pageIndex = pageIndex,
                    pageCount = pageCount,
                    widthPx = containerWidthPx.coerceAtMost(2048),
                    highlightBounds = if (activeChunk?.pageIndex == pageIndex) activeChunk.bounds else null,
                    highContrast = highContrast,
                    onDoubleTap = { offset ->
                        // Find chunk at offset
                        val chunk = chunks.find { 
                            it.pageIndex == pageIndex && 
                            it.bounds?.contains(offset.x, offset.y) == true 
                        }
                        chunk?.let { onDoubleTap(it) }
                    },
                    pageWidthPtsKnown = { /* maybe store for scaling */ }
                )
            }
        }

        // Floating Page Scrubber / Indicator Pill
        Surface(
            shape = RoundedCornerShape(50),
            color = if (highContrast) Color.Black else MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, if (highContrast) Color.Yellow else MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = if (highContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
                Text(
                    text = "Page $firstVisiblePage of $pageCount",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (highContrast) Color.Yellow else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun PdfPage(
    renderer: PdfPageRenderer,
    pageIndex: Int,
    pageCount: Int,
    widthPx: Int,
    highlightBounds: RectF? = null,
    highContrast: Boolean = false,
    onDoubleTap: (Offset) -> Unit = {},
    pageWidthPtsKnown: (Float) -> Unit = {}
) {
    var thumbBitmap by remember(pageIndex) { mutableStateOf<Bitmap?>(null) }
    var fullBitmap  by remember(pageIndex) { mutableStateOf<Bitmap?>(null) }
    var aspectRatio by remember(pageIndex) { mutableStateOf(0.707f) }
    var pageWidthPts by remember(pageIndex) { mutableStateOf(595f) }

    LaunchedEffect(pageIndex, widthPx) {
        if (widthPx > 0) {
            val size = renderer.getPageSize(pageIndex)
            pageWidthPts = size.width.toFloat()
            pageWidthPtsKnown(pageWidthPts)
            aspectRatio = size.width.toFloat() / size.height.toFloat()
            
            if (!renderer.isCached(pageIndex, widthPx)) {
                thumbBitmap = renderer.renderPageLowRes(pageIndex)
            }
            fullBitmap = renderer.renderPage(pageIndex, widthPx)
            thumbBitmap = null

            if (pageIndex + 1 < pageCount) {
                launch {
                    renderer.prefetchPages(
                        startIndex = pageIndex + 1,
                        widthPx = widthPx,
                        count = 2
                    )
                }
            }
        }
    }

    DisposableEffect(pageIndex) {
        onDispose {
            thumbBitmap = null
            fullBitmap = null
        }
    }

    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    
    val imageModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(aspectRatio)
        .background(Color.White)
        .pointerInput(pageIndex, pageWidthPts) {
            detectTapGestures(
                onDoubleTap = { offset ->
                    val scaleFactor = size.width / pageWidthPts
                    val ptsOffset = Offset(offset.x / scaleFactor, offset.y / scaleFactor)
                    onDoubleTap(ptsOffset)
                }
            )
        }
        .drawWithContent {
            drawContent()
            highlightBounds?.let { rectF ->
                val composeRect = rectF.toComposeRect()
                
                // Calculate scale between PDF points and actual screen pixels
                val scaleFactor = size.width / pageWidthPts
                
                drawRect(
                    color = highlightColor,
                    topLeft = Offset(
                        composeRect.left * scaleFactor, 
                        composeRect.top * scaleFactor
                    ),
                    size = Size(
                        composeRect.width * scaleFactor, 
                        composeRect.height * scaleFactor
                    )
                )
            }
        }

    // Color matrix for high contrast (invert + slight yellow tint for readability)
    val colorFilter = if (highContrast) {
        val matrix = ColorMatrix(floatArrayOf(
            -1f,  0f,  0f, 0f, 255f,
             0f, -1f,  0f, 0f, 255f,
             0f,  0f, -1f, 0f, 255f,
             0f,  0f,  0f, 1f, 0f
        ))
        // Mix with yellow tint: [R, G, B] -> [R, G, B*0.8] approx
        val yellowMatrix = ColorMatrix(floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 0.8f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        matrix.timesAssign(yellowMatrix)
        ColorFilter.colorMatrix(matrix)
    } else null

    when {
        fullBitmap != null -> Image(
            bitmap = fullBitmap!!.asImageBitmap(),
            contentDescription = stringResource(R.string.page_description, pageIndex + 1),
            contentScale = ContentScale.FillWidth,
            modifier = imageModifier,
            colorFilter = colorFilter
        )
        thumbBitmap != null -> Image(
            bitmap = thumbBitmap!!.asImageBitmap(),
            contentDescription = stringResource(R.string.page_description, pageIndex + 1),
            contentScale = ContentScale.FillWidth,
            modifier = imageModifier,
            colorFilter = colorFilter
        )
        else -> Box(
            modifier = imageModifier,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
