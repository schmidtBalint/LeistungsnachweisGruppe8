package de.hhn.umgebungsscanner.challenge

/**
 * Individuelle Spielwerte, abgeleitet aus den letzten drei Ziffern der Matrikelnummer.
 *
 * - letzte Ziffer:       Haltezeit  = 3 + (Ziffer mod 4) s
 * - vorletzte Ziffer:    Zielradius = 40 + Ziffer * 5 dp
 * - drittletzte Ziffer:  Windstufe  = 1 + (Ziffer mod 3)
 *
 * Beispiel 214857: Haltezeit 6 s, Radius 65 dp, Wind Stufe 3.
 */
data class ChallengeConfig(
    val holdSeconds: Int,
    val targetRadiusDp: Float,
    val windLevel: Int,
) {
    companion object {
        const val DEFAULT_MATRIKEL = "214857"

        /** Werte für [DEFAULT_MATRIKEL]. */
        val DEFAULT = ChallengeConfig(holdSeconds = 6, targetRadiusDp = 65f, windLevel = 3)

        /**
         * Liefert die Spielwerte zur Matrikelnummer oder null, wenn die Eingabe ungültig ist
         * (weniger als 3 Zeichen oder nicht nur Ziffern). Darf nie abstürzen.
         */
        fun fromMatrikel(matrikel: String): ChallengeConfig? {
            // TODO (Teil B): Werte aus den letzten drei Ziffern berechnen (Formeln siehe oben bzw. README).
            //  Ungültige Eingaben liefern null. Die Unit-Tests in BalanceLogicTest prüfen das.
            return null
        }
    }
}

/**
 * Bedeutung der Windstufen: Wie stark (dp/s Geschwindigkeitsänderung) und
 * wie oft (Abstand in Sekunden, zufällig zwischen min und max) der Wind den Punkt anstößt.
 */
data class WindSettings(
    val impulseDpPerSecond: Float,
    val minIntervalSeconds: Float,
    val maxIntervalSeconds: Float,
) {
    companion object {
        fun forLevel(level: Int): WindSettings = when (level) {
            1 -> WindSettings(impulseDpPerSecond = 18f, minIntervalSeconds = 1.6f, maxIntervalSeconds = 2.6f)
            2 -> WindSettings(impulseDpPerSecond = 28f, minIntervalSeconds = 1.1f, maxIntervalSeconds = 1.9f)
            else -> WindSettings(impulseDpPerSecond = 38f, minIntervalSeconds = 0.7f, maxIntervalSeconds = 1.3f)
        }
    }
}

/** Zahl mit deutschem Komma, z. B. 2,4. */
fun Float.formatDe(decimals: Int = 1): String = String.format(java.util.Locale.GERMANY, "%.${decimals}f", this)
