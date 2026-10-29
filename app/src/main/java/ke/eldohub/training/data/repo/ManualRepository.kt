package ke.eldohub.training.data.repo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ke.eldohub.training.data.local.ManualDao
import ke.eldohub.training.data.local.ManualEntity
import ke.eldohub.training.data.local.ManualSummary
import ke.eldohub.training.data.remote.ContentRemoteSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManualRepository @Inject constructor(
    private val dao: ManualDao,
    private val remoteSource: ContentRemoteSource,
    @ApplicationContext private val context: Context
) {

    /** Manuals as a Flow of UI models. Auto-updates when the DB changes. */
    fun observeManuals(): Flow<List<Manual>> =
        dao.observeSummaries().map { rows -> rows.map { it.toManual() } }

    suspend fun getManual(manualId: String): ManualEntity? =
        dao.getById(manualId)

    suspend fun markManualAsViewed(manualId: String) = withContext(Dispatchers.IO) {
        dao.updateLastViewed(manualId, System.currentTimeMillis())
    }

    fun observeRecentlyViewedManuals(limit: Int = 3): Flow<List<Manual>> =
        dao.observeRecentSummaries(limit).map { rows -> rows.map { it.toManual() } }

    /**
     * One-time seed of bundled manuals from assets/manuals into the DB.
     * Safe to call repeatedly: Upsert means existing rows are just refreshed.
     */
    suspend fun seedBundledManuals() = withContext(Dispatchers.IO) {
        val assetFiles = context.assets.list("manuals")?.filter {
            it.endsWith(".pdf", ignoreCase = true)
        }.orEmpty()

        val entities = assetFiles.map { fileName ->
            val id = fileName.removeSuffix(".pdf")
            val textPath = "manuals/$id.txt"
            val textContent = try {
                context.assets.open(textPath).bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                null
            }

            ManualEntity(
                id = id,
                title = id.toTitle(),
                version = 1,
                assetPath = "manuals/$fileName",
                textContent = textContent
            )
        }
        if (entities.isNotEmpty()) dao.upsertAll(entities)
    }

    /**
     * Returns a file on disk that PdfRenderer can open for the given manual.
     *
     * - Downloaded manuals already live on disk → return that file directly.
     * - Bundled manuals live inside the APK's assets, which PdfRenderer CANNOT
     *   open directly, so we copy the asset into cache (once) and return that.
     *
     * Returns null if the manual is unknown or has no usable source.
     */
    suspend fun resolvePdfFile(manualId: String): File? {
        val entity = dao.getById(manualId) ?: return null

        // Already downloaded to internal storage.
        entity.filePath?.let { path ->
            val f = File(path)
            if (f.exists()) return f
        }

        // Bundled in assets → copy to cache so PdfRenderer can open it.
        entity.assetPath?.let { assetPath ->
            val cached = File(context.cacheDir, "$manualId.pdf")
            if (!cached.exists()) {
                context.assets.open(assetPath).use { input ->
                    cached.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            return cached
        }

        return null
    }

    /**
     * Sync against the remote manifest.
     *
     * For each manual in the manifest:
     *  - If we don't have it, or its version is higher than our stored version,
     *    download the PDF and upsert the row pointing at the downloaded file.
     *  - Otherwise skip it (already up to date).
     *
     * Bundled (asset) manuals are left untouched unless the manifest lists the
     * same id with a higher version, in which case the downloaded copy wins.
     *
     * Returns the number of manuals downloaded/updated.
     */
    suspend fun syncFromManifest(manifestUrl: String): Int {
        val manifest = remoteSource.fetchManifest(manifestUrl)
        val pdfDir = File(context.filesDir, "manuals").apply { mkdirs() }

        var updated = 0
        for (remote in manifest.manuals) {
            val existing = dao.getById(remote.id)
            val haveCurrent = existing != null &&
                existing.version >= remote.version &&
                existing.filePath?.let { File(it).exists() } == true

            if (haveCurrent) continue

            val destination = File(pdfDir, "${remote.id}.pdf")
            remoteSource.downloadFile(remote.url, destination)

            val textContent = remote.textUrl?.let { url ->
                try {
                    val textFile = File(pdfDir, "${remote.id}.txt")
                    remoteSource.downloadFile(url, textFile)
                    textFile.readText()
                } catch (e: Exception) {
                    null
                }
            }

            dao.upsert(
                ManualEntity(
                    id = remote.id,
                    title = remote.title,
                    version = remote.version,
                    assetPath = existing?.assetPath, // preserve asset fallback if any
                    filePath = destination.absolutePath,
                    remoteUrl = remote.url,
                    textContent = textContent
                )
            )
            updated++
        }
        return updated
    }

    private fun ManualSummary.toManual(): Manual {
        val onDisk = filePath?.let { File(it).exists() } ?: false
        val bundled = assetPath != null
        return Manual(
            id = id,
            title = title,
            isAvailable = onDisk || bundled
        )
    }

    private fun ManualEntity.toManual(): Manual {
        val onDisk = filePath?.let { File(it).exists() } ?: false
        val bundled = assetPath != null
        return Manual(
            id = id,
            title = title,
            isAvailable = onDisk || bundled
        )
    }
}

/** "digital_finance_basics" -> "Digital Finance Basics" */
private fun String.toTitle(): String =
    split("_", "-")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
