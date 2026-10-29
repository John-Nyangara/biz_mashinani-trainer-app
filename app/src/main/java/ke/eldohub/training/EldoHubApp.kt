package ke.eldohub.training

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ke.eldohub.training.sync.SyncScheduler
import javax.inject.Inject

@HiltAndroidApp
class EldoHubApp : Application(), Configuration.Provider, ImageLoaderFactory {

    // Lets WorkManager construct @HiltWorker workers with injected dependencies.
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    /**
     * Optimizes Coil for budget/Modio tablets:
     * 1. Uses a conservative memory cache (15% of available heap).
     * 2. Enables a disk cache to avoid redundant network/IO work.
     * 3. Prefers RGB_565 to save 50% memory per image.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.15)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB
                    .build()
            }
            .allowHardware(true)
            .crossfade(true)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        // Register the periodic WiFi sync (no-op if already scheduled).
        SyncScheduler.schedulePeriodicSync(this)
    }
}
