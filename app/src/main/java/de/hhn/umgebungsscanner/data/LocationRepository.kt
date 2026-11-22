package de.hhn.umgebungsscanner.data

import android.content.Context
import com.google.android.gms.location.LocationServices

/** Jede Antwort des Systems kann "nein" oder "nichts" sein. Deshalb ein Ergebnis pro Fall. */
sealed interface LocationResult {
    data class Success(
        val latitude: Double,
        val longitude: Double,
        val accuracyMeters: Float?,
        val approximate: Boolean,
    ) : LocationResult

    /** Kein Standortrecht. */
    data object NoPermission : LocationResult

    /** Recht da, aber die Standortdienste (GPS) sind ausgeschaltet. */
    data object ServicesDisabled : LocationResult

    /** Innerhalb des Timeouts kam keine Position (z. B. Emulator ohne gesetzte Position). */
    data object NoFix : LocationResult

    data class Failure(val reason: String) : LocationResult
}

class LocationRepository(context: Context) {

    private val appContext = context.applicationContext
    private val fused = LocationServices.getFusedLocationProviderClient(appContext)

    /**
     * Fragt eine aktuelle Position ab.
     *
     * Darf in keinem Fall abstürzen und nie endlos laden.
     */
    suspend fun currentLocation(): LocationResult {
        // TODO (Teil A):
        //  1. Recht prüfen (FINE oder COARSE erteilt?)                       -> sonst NoPermission
        //  2. Standortdienste an? (LocationManagerCompat.isLocationEnabled)  -> sonst ServicesDisabled
        //  3. Position mit fused.getCurrentLocation(...) abfragen, mit Timeout (siehe README)
        //     Kommt nichts?                                                   -> NoFix
        //  4. Nur COARSE erteilt? Dann approximate = true
        //  Kein `!!` verwenden.
        return LocationResult.Failure("Noch nicht implementiert")
    }
}
