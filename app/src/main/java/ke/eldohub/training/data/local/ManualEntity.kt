package ke.eldohub.training.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One row per training manual known to the app.
 *
 * - For bundled PDFs (shipped in assets), [assetPath] is set and [filePath] is null until copied.
 * - For downloaded PDFs, [filePath] points to the file in internal storage.
 * - [version] mirrors the manifest's per-manual version so we know when to re-download.
 */
@Entity(
    tableName = "manuals",
    indices = [
        androidx.room.Index("lastViewedAt"),
        androidx.room.Index("title")
    ]
)
data class ManualEntity(
    @PrimaryKey val id: String,
    val title: String,
    val version: Int,
    val assetPath: String? = null,   // e.g. "manuals/digital_finance_basics.pdf"
    val filePath: String? = null,    // absolute path once available on disk
    val remoteUrl: String? = null,   // where to download from, if remote
    val textContent: String? = null, // Full text for "Read Aloud" feature
    val lastViewedAt: Long? = null   // Timestamp of last view
)
