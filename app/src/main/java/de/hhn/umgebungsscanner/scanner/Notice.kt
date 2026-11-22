package de.hhn.umgebungsscanner.scanner

/** Was passieren soll, wenn der Nutzer auf den Button einer Meldung tippt. Bei Bedarf erweitern. */
enum class NoticeAction {
    REQUEST_LOCATION_PERMISSION,
    RETRY_LOCATION,
    OPEN_LOCATION_SETTINGS,
    REQUEST_CAMERA_PERMISSION,
    RETRY_CAMERA,
    OPEN_APP_SETTINGS,
}

/**
 * Verständliche Meldung statt Absturz, optional mit einem Button, der das Problem löst.
 * Wird auf dem Startbildschirm mit NoticeCard angezeigt.
 */
data class Notice(
    val title: String,
    val message: String,
    val action: NoticeAction? = null,
    val actionLabel: String? = null,
    val isError: Boolean = true,
)
