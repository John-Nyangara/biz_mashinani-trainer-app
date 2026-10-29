package ke.eldohub.training.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Plain HttpURLConnection networking — no Retrofit/OkHttp dependency needed for
 * two simple GETs (fetch manifest, download a file). Everything runs on IO.
 */
@Singleton
class ContentRemoteSource @Inject constructor() {

    /** Download and parse the manifest. Throws on network/parse failure. */
    suspend fun fetchManifest(manifestUrl: String): ManifestDto =
        withContext(Dispatchers.IO) {
            val conn = (URL(manifestUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15_000
                readTimeout = 15_000
            }
            try {
                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    error("Manifest fetch failed: HTTP ${conn.responseCode}")
                }
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                ManifestParser.parse(body)
            } finally {
                conn.disconnect()
            }
        }

    /**
     * Download a file to [destination]. Writes to a temp file first, then renames,
     * so a half-finished download is never mistaken for a complete file.
     */
    suspend fun downloadFile(fileUrl: String, destination: File): Unit =
        withContext(Dispatchers.IO) {
            val conn = (URL(fileUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15_000
                readTimeout = 30_000
            }
            try {
                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    error("Download failed: HTTP ${conn.responseCode} for $fileUrl")
                }
                val tmp = File(destination.parentFile, destination.name + ".tmp")
                conn.inputStream.use { input ->
                    tmp.outputStream().use { output -> input.copyTo(output) }
                }
                if (destination.exists()) destination.delete()
                if (!tmp.renameTo(destination)) {
                    tmp.delete()
                    error("Could not finalize download for ${destination.name}")
                }
            } finally {
                conn.disconnect()
            }
        }
}
