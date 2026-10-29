package ke.eldohub.training.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ke.eldohub.training.data.repo.ManualRepository

/**
 * Background sync. Runs only under the constraints set when scheduled
 * (unmetered network = WiFi). WorkManager handles retry/backoff and
 * survives process death.
 */
@HiltWorker
class ContentSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: ManualRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            repository.syncFromManifest(SyncConfig.MANIFEST_URL)
            Result.success()
        } catch (e: Exception) {
            // Transient failures (no network mid-run, server hiccup) → retry later.
            Result.retry()
        }
    }
}
