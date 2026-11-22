package de.hhn.umgebungsscanner.challenge

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Zustände der Balance-Challenge. */
sealed interface BalanceState {
    data object Playing : BalanceState
    data class Won(val seconds: Float) : BalanceState
    data object NoSensor : BalanceState
}

/** Momentaufnahme des Spiels. Positionen in dp relativ zur Mitte der Spielfläche. */
data class BalanceSnapshot(
    val x: Float = 0f,
    val y: Float = 0f,
    val inside: Boolean = false,
    val holdTime: Float = 0f,
    val elapsed: Float = 0f,
    val won: Boolean = false,
)

/**
 * Spiellogik der Balance-Challenge, ohne Android-Abhängigkeiten (deshalb per Unit-Test prüfbar).
 *
 * Kette: Gyroskop (ω in rad/s) -> Geschwindigkeit (v += ω · k) -> Wind (v += Zufall)
 *        -> Position (p += v) -> Im Kreis? -> Timer läuft.
 *
 * Achtung: Das Gyroskop misst Drehgeschwindigkeit, nicht Neigung.
 */
class BalanceLogic(
    private val config: ChallengeConfig,
    private val random: Random = Random.Default,
    /** k: Empfindlichkeit. Gern anpassen, bis es sich gut anfühlt. */
    private val sensitivity: Float = 420f,
) {
    private val wind = WindSettings.forLevel(config.windLevel)

    /** Radius der Spielfläche in dp. Wird von der UI gesetzt, sobald die Größe bekannt ist. */
    var arenaRadiusDp: Float = 160f
    var ballRadiusDp: Float = 22f

    private var x = 0f
    private var y = 0f
    private var vx = 0f
    private var vy = 0f
    private var holdTime = 0f
    private var elapsed = 0f
    private var won = false

    init {
        reset()
    }

    /** Startet eine neue Runde. Der Punkt beginnt außerhalb des Ziels, damit man ihn hinsteuern muss. */
    fun reset() {
        val angle = random.nextFloat() * 2f * PI.toFloat()
        val startDistance = (config.targetRadiusDp + arenaRadiusDp) / 2f
        x = cos(angle) * startDistance
        y = sin(angle) * startDistance
        vx = 0f
        vy = 0f
        holdTime = 0f
        elapsed = 0f
        won = false
    }

    /**
     * Neue Messung des Gyroskops.
     *
     * @param omegaX Drehgeschwindigkeit um die x-Achse in rad/s (Kippen nach vorne/hinten)
     * @param omegaY Drehgeschwindigkeit um die y-Achse in rad/s (Kippen nach links/rechts)
     * @param dt Zeit seit der letzten Messung in Sekunden (aus SensorEvent.timestamp)
     */
    fun onRotation(omegaX: Float, omegaY: Float, dt: Float) {
        // TODO (Teil B): Drehung in Geschwindigkeit umrechnen (v += ω · k).
        //  Welche Achse bewegt den Punkt in welche Richtung? Vorzeichen im Emulator ausprobieren.
    }

    /**
     * Ein Simulationsschritt, wird einmal pro Frame aufgerufen.
     *
     * @param dtSeconds Zeit seit dem letzten Frame in Sekunden
     */
    fun step(dtSeconds: Float): BalanceSnapshot {
        // TODO (Teil B), Schritt für Schritt:
        //  1. Wind: in zufälligen Abständen ein Stoß in zufällige Richtung (Stärke und Abstand: siehe [wind])
        //  2. Reibung, damit der Punkt nicht ewig weiterrollt
        //  3. Position: p += v · dt
        //  4. Am Rand der Spielfläche hängen bleiben: Der Punkt darf die Fläche nie verlassen
        //  5. Im Kreis? Dann läuft holdTime hoch, sonst beginnt sie bei 0
        //  6. Haltezeit erreicht? Dann ist das Spiel gewonnen
        return snapshot()
    }

    fun snapshot() = BalanceSnapshot(
        x = x,
        y = y,
        inside = false, // TODO (Teil B): liegt der Punkt im Zielkreis?
        holdTime = holdTime,
        elapsed = elapsed,
        won = won,
    )
}
