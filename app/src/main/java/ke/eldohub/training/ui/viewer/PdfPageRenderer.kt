package ke.eldohub.training.ui.viewer

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.io.File
import android.graphics.RectF
import androidx.compose.ui.geometry.Size
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

data class SpeechChunk(
    val id: String,
    val text: String,
    val pageIndex: Int,
    val bounds: RectF? = null
)

/**
 * Wraps Android's PdfRenderer for one PDF file.
 *
 * Two important constraints this class handles for you:
 *  1. PdfRenderer can only have ONE page open at a time. Opening page N while
 *     page M is still open throws. We guard every render with a Mutex so calls
 *     are serialized even from different coroutines.
 *  2. Rendering is I/O + CPU work, so it runs on Dispatchers.IO, never the main thread.
 *
 * Call close() when done (the ViewModel does this in onCleared).
 */
class PdfPageRenderer(file: File, maxCacheSize: Int = 12) : Closeable {

    private val fileDescriptor: ParcelFileDescriptor =
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

    private val renderer = PdfRenderer(fileDescriptor)

    // Serializes access — PdfRenderer is not safe for concurrent page opens.
    private val mutex = Mutex()

    // Cache sized dynamically by bytes (1/8th of JVM max heap, clamped between 16MB and 64MB)
    // SizeOf in KB prevents OOM on low-memory tablets regardless of page count
    private val maxCacheMemoryKb = ((Runtime.getRuntime().maxMemory() / 1024) / 8).toInt().coerceIn(16 * 1024, 64 * 1024)

    private val pageCache = object : LruCache<String, Bitmap>(maxCacheMemoryKb) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return (value.byteCount / 1024).coerceAtLeast(1)
        }

        override fun entryRemoved(evicted: Boolean, key: String, old: Bitmap, new: Bitmap?) {
            if (evicted && old != new && !old.isRecycled) {
                old.recycle()
            }
        }
    }

    private val chunkCache = LruCache<Int, List<SpeechChunk>>(100)

    val pageCount: Int get() = renderer.pageCount

    /**
     * Renders one page to a Bitmap.
     *
     * @param index zero-based page number
     * @param targetWidth the width (px) to render at; height is derived from the
     *        page aspect ratio so text stays sharp at the display width.
     *        Capped at 2048 px to prevent OOM on low-RAM tablets.
     */
    suspend fun renderPage(index: Int, targetWidth: Int): Bitmap =
        withContext(Dispatchers.IO) {
            val width = targetWidth.coerceIn(1, 2048)
            val cacheKey = "$index:$width"

            pageCache.get(cacheKey)?.let { return@withContext it }

            mutex.withLock {
                // Double-check after acquiring the lock — another coroutine may have
                // rendered and cached this page while we were waiting.
                pageCache.get(cacheKey)?.let { return@withLock it }

                renderer.openPage(index).use { page ->
                    val ratio = page.height.toFloat() / page.width.toFloat()
                    val height = (width * ratio).toInt().coerceAtLeast(1)

                    // PDFs are typically opaque, but PdfRenderer requires ARGB_8888 for rendering.
                    val tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    // PDFs assume a white page; without this, transparent areas render black.
                    tempBitmap.eraseColor(Color.WHITE)

                    page.render(tempBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    // Convert to RGB_565 for storage in cache. Saves 50% RAM with no perceptual difference for PDF text.
                    val cachedBitmap = tempBitmap.copy(Bitmap.Config.RGB_565, false) ?: tempBitmap
                    if (cachedBitmap !== tempBitmap) {
                        tempBitmap.recycle()
                    }

                    pageCache.put(cacheKey, cachedBitmap)
                    cachedBitmap
                }
            }
        }

    /**
     * Renders [count] pages starting at [startIndex] into the cache without blocking the caller.
     * Safe to call speculatively — pages already cached are skipped immediately.
     */
    suspend fun prefetchPages(startIndex: Int, widthPx: Int, count: Int) {
        val end = (startIndex + count).coerceAtMost(pageCount)
        for (i in startIndex until end) {
            runCatching { renderPage(i, widthPx) }
        }
    }

    /** Returns true if [index] at [widthPx] (capped to 2048) is already in the cache. */
    fun isCached(index: Int, widthPx: Int): Boolean {
        val width = widthPx.coerceIn(1, 2048)
        return pageCache.get("$index:$width") != null
    }

    /** Returns the page size in points (1/72 inch). */
    suspend fun getPageSize(index: Int): android.util.Size = withContext(Dispatchers.IO) {
        mutex.withLock {
            renderer.openPage(index).use { page ->
                android.util.Size(page.width, page.height)
            }
        }
    }

    /** Returns the page aspect ratio (width / height) without rendering. */
    suspend fun getPageAspectRatio(index: Int): Float = withContext(Dispatchers.IO) {
        mutex.withLock {
            renderer.openPage(index).use { page ->
                page.width.toFloat() / page.height.toFloat()
            }
        }
    }

    /**
     * Extracts structured speech chunks from a specific page.
     * On Android 15+, includes bounding boxes for highlighting.
     * On Android <15, uses ML Kit OCR for extraction.
     */
    suspend fun getSpeechChunksForPage(pageIndex: Int): List<SpeechChunk> = withContext(Dispatchers.IO) {
        if (pageIndex !in 0 until pageCount) return@withContext emptyList()
        
        chunkCache.get(pageIndex)?.let { return@withContext it }
        
        val chunks = mutableListOf<SpeechChunk>()
        
        if (android.os.Build.VERSION.SDK_INT >= 35) {
            mutex.withLock {
                renderer.openPage(pageIndex).use { page ->
                    val contents = page.getTextContents()
                    
                    // Group contents into rows to handle tables correctly
                    val sortedContents = contents
                        .filter { it.text.isNotBlank() }
                        .sortedBy { (it.bounds.firstOrNull()?.top ?: 0).toInt() }
                    
                    val rows = mutableListOf<MutableList<android.graphics.pdf.content.PdfPageTextContent>>()
                    val rowThreshold = 15 // pixels; adjust based on average text height if possible
                    
                    sortedContents.forEach { content ->
                        val contentTop = (content.bounds.firstOrNull()?.top ?: 0).toDouble()
                        val existingRow = rows.find { row ->
                            val rowAvgTop = row.map { (it.bounds.firstOrNull()?.top ?: 0).toDouble() }.average()
                            kotlin.math.abs(rowAvgTop - contentTop) < rowThreshold
                        }
                        
                        if (existingRow != null) {
                            existingRow.add(content)
                        } else {
                            rows.add(mutableListOf(content))
                        }
                    }
                    
                    // Now sort each row by Left coordinate
                    rows.forEach { row ->
                        row.sortBy { (it.bounds.firstOrNull()?.left ?: 0).toInt() }
                    }
                    
                    var chunkCount = 0
                    rows.forEach { row ->
                        row.forEach { content ->
                            chunks.add(
                                SpeechChunk(
                                    id = "p${pageIndex}_c${chunkCount++}",
                                    text = content.text,
                                    pageIndex = pageIndex,
                                    bounds = content.bounds.firstOrNull()?.let { RectF(it) }
                                )
                            )
                        }
                    }
                }
            }
        } else {
            // Android < 15: Use ML Kit Text Recognition
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            // OCR works best on ~1500px width.
            val bitmap = renderPage(pageIndex, 1500)
            val image = InputImage.fromBitmap(bitmap, 0)
            try {
                val result = recognizer.process(image).await()
                
                // Group blocks into rows to handle tables correctly
                val sortedBlocks = result.textBlocks
                    .filter { it.text.isNotBlank() }
                    .sortedBy { it.boundingBox?.top ?: 0 } // Primary sort by Top
                
                val rows = mutableListOf<MutableList<com.google.mlkit.vision.text.Text.TextBlock>>()
                val rowThreshold = 15 // pixels; adjust based on average text height if possible
                
                    sortedBlocks.forEach { block ->
                        val blockTop = block.boundingBox?.top ?: 0
                        val existingRow = rows.find { row ->
                            val rowAvgTop = row.map { (it.boundingBox?.top ?: 0).toDouble() }.average()
                            kotlin.math.abs(rowAvgTop - blockTop.toDouble()) < rowThreshold
                        }
                        
                        if (existingRow != null) {
                            existingRow.add(block)
                        } else {
                            rows.add(mutableListOf(block))
                        }
                    }
                
                // Now sort each row by Left coordinate
                rows.forEach { row ->
                    row.sortBy { it.boundingBox?.left ?: 0 }
                }
                
                var chunkCount = 0
                rows.forEach { row ->
                    row.forEach { block ->
                        chunks.add(
                            SpeechChunk(
                                id = "p${pageIndex}_c${chunkCount++}",
                                text = block.text,
                                pageIndex = pageIndex,
                                bounds = block.boundingBox?.let { RectF(it) }
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        chunkCache.put(pageIndex, chunks)
        chunks
    }

    /**
     * Extracts speech chunks for a specific range of pages.
     */
    suspend fun getSpeechChunks(range: IntRange = 0 until pageCount): List<SpeechChunk> {
        val all = mutableListOf<SpeechChunk>()
        for (i in range) {
            if (i in 0 until pageCount) {
                all.addAll(getSpeechChunksForPage(i))
            }
        }
        return all
    }

    /**
     * Extracts text from all pages of the document.
     * Note: [getTextContents] requires API 35 (Android 15).
     * On older devices, we return a fallback message.
     */
    suspend fun getAllText(): String {
        val chunks = getSpeechChunks()
        return if (chunks.isNotEmpty()) {
            chunks.joinToString(" ") { it.text }
        } else {
            "Digital extraction failed or is not supported on this Android version. Please provide a .txt companion file for 'Read Aloud'."
        }
    }

    /**
     * Renders a low-resolution thumbnail (default 300 px wide) for quick display.
     * Cache key has an "L" suffix so it never collides with full-res entries.
     */
    suspend fun renderPageLowRes(index: Int, thumbWidth: Int = 300): Bitmap =
        withContext(Dispatchers.IO) {
            val cacheKey = "$index:${thumbWidth}L"
            pageCache.get(cacheKey)?.let { return@withContext it }
            mutex.withLock {
                pageCache.get(cacheKey)?.let { return@withLock it }
                renderer.openPage(index).use { page ->
                    val ratio = page.height.toFloat() / page.width.toFloat()
                    val h = (thumbWidth * ratio).toInt().coerceAtLeast(1)
                    val tempBmp = Bitmap.createBitmap(thumbWidth, h, Bitmap.Config.ARGB_8888)
                    tempBmp.eraseColor(Color.WHITE)
                    page.render(tempBmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    val finalBmp = tempBmp.copy(Bitmap.Config.RGB_565, false) ?: tempBmp
                    if (finalBmp !== tempBmp) {
                        tempBmp.recycle()
                    }
                    pageCache.put(cacheKey, finalBmp)
                    finalBmp
                }
            }
        }

    /** Renders low-res thumbnails for [count] pages starting at [startIndex]. */
    suspend fun prefetchPagesLowRes(startIndex: Int, count: Int) {
        val end = (startIndex + count).coerceAtMost(pageCount)
        for (i in startIndex until end) runCatching { renderPageLowRes(i) }
    }

    override fun close() {
        pageCache.evictAll()
        renderer.close()
        fileDescriptor.close()
    }
}
