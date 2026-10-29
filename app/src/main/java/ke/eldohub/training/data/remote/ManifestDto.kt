package ke.eldohub.training.data.remote

import org.json.JSONObject

/**
 * Parsed form of the server's manifest.json.
 *
 * Top-level "version" is the catalog version — bump it whenever the list changes.
 * Each manual carries its own "version" so we only re-download a manual whose
 * individual version increased, not the whole set.
 */
data class ManifestDto(
    val version: Int,
    val manuals: List<ManifestManual>
)

data class ManifestManual(
    val id: String,
    val title: String,
    val version: Int,
    val url: String,
    val textUrl: String? = null
)

/**
 * Hand-rolled parser using org.json (bundled with Android — no extra dependency).
 * Kept defensive: a malformed entry is skipped rather than failing the whole sync.
 */
object ManifestParser {

    fun parse(raw: String): ManifestDto {
        val root = JSONObject(raw)
        val catalogVersion = root.optInt("version", 0)
        val arr = root.optJSONArray("manuals")

        val manuals = buildList {
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i) ?: continue
                    val id = obj.optString("id").takeIf { it.isNotBlank() } ?: continue
                    val url = obj.optString("url").takeIf { it.isNotBlank() } ?: continue
                    add(
                        ManifestManual(
                            id = id,
                            title = obj.optString("title", id),
                            version = obj.optInt("version", 1),
                            url = url,
                            textUrl = obj.optString("text_url").takeIf { it.isNotBlank() }
                        )
                    )
                }
            }
        }
        return ManifestDto(version = catalogVersion, manuals = manuals)
    }
}
