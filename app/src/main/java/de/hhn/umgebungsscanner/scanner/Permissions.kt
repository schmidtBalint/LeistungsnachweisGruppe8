package de.hhn.umgebungsscanner.scanner

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/** Ist das Recht aktuell erteilt? */
fun Context.isGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

/**
 * Wurde das Recht dauerhaft verweigert? Erst im Callback NACH dem Dialog aufrufen.
 */
fun Activity?.isPermanentlyDenied(vararg permissions: String): Boolean {
    // TODO (Teil A): mit shouldShowRequestPermissionRationale() unterscheiden,
    //  ob "nur" verweigert oder dauerhaft verweigert wurde.
    return false
}

/** Öffnet die Einstellungsseite dieser App (dort kann der Nutzer Rechte selbst erteilen). */
fun Context.openAppSettings() {
    // TODO (Teil A): Intent auf die App-Einstellungen starten
}

/** Öffnet die Standort-Einstellungen des Systems (für den Fall "GPS aus"). */
fun Context.openLocationSettings() {
    // TODO (Teil A): Intent auf die Standort-Einstellungen starten
}

fun Context.hasCameraHardware(): Boolean =
    packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
