package ke.eldohub.training.data.repo

/**
 * UI-facing model. Kept separate from ManualEntity so the screen layer
 * doesn't depend on Room types directly.
 */
data class Manual(
    val id: String,
    val title: String,
    val isAvailable: Boolean // true if a PDF file is actually on disk / in assets and openable
)
