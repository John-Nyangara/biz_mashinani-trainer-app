package ke.eldohub.training.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

data class ManualSummary(
    val id: String,
    val title: String,
    val version: Int,
    val assetPath: String? = null,
    val filePath: String? = null,
    val remoteUrl: String? = null,
    val lastViewedAt: Long? = null
)

@Dao
interface ManualDao {

    // Optimized projection query: skips large textContent column for list/dashboard rendering
    @Query("SELECT id, title, version, assetPath, filePath, remoteUrl, lastViewedAt FROM manuals ORDER BY title ASC")
    fun observeSummaries(): Flow<List<ManualSummary>>

    // Flow: the UI re-renders automatically whenever rows change (e.g. after a sync insert).
    @Query("SELECT * FROM manuals ORDER BY title ASC")
    fun observeAll(): Flow<List<ManualEntity>>

    @Query("SELECT * FROM manuals WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ManualEntity?

    @Upsert
    suspend fun upsertAll(manuals: List<ManualEntity>)

    @Upsert
    suspend fun upsert(manual: ManualEntity)

    @Query("UPDATE manuals SET lastViewedAt = :timestamp WHERE id = :id")
    suspend fun updateLastViewed(id: String, timestamp: Long)

    @Query("SELECT id, title, version, assetPath, filePath, remoteUrl, lastViewedAt FROM manuals WHERE lastViewedAt IS NOT NULL ORDER BY lastViewedAt DESC LIMIT :limit")
    fun observeRecentSummaries(limit: Int): Flow<List<ManualSummary>>

    @Query("SELECT * FROM manuals WHERE lastViewedAt IS NOT NULL ORDER BY lastViewedAt DESC LIMIT :limit")
    fun observeRecentlyViewed(limit: Int): Flow<List<ManualEntity>>
}
