package ke.eldohub.training.sync

/**
 * Central place for sync settings.
 *
 * TODO: replace MANIFEST_URL with your real server URL before production, e.g.
 *   https://eldohub.co.ke/training/manifest.json
 */
object SyncConfig {
    const val MANIFEST_URL = "https://system.eldohub.co.ke/manifest.json"
    const val UNIQUE_WORK_NAME = "eldohub_content_sync"
}
