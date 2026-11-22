package de.hhn.umgebungsscanner

import de.hhn.umgebungsscanner.challenge.BalanceLogic
import de.hhn.umgebungsscanner.challenge.ChallengeConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot
import kotlin.random.Random

/**
 * Selbstkontrolle für Teil B. Ausführen mit: ./gradlew testDebugUnitTest
 * (oder in Android Studio: Rechtsklick auf die Klasse -> Run).
 * Im Starterstand schlagen einige Tests fehl, das ist Absicht.
 */
class BalanceLogicTest {

    @Test
    fun `Beispiel aus dem Vortrag 214857`() {
        assertEquals(
            ChallengeConfig(holdSeconds = 6, targetRadiusDp = 65f, windLevel = 3),
            ChallengeConfig.fromMatrikel("214857"),
        )
    }

    @Test
    fun `Werte decken alle Ziffern ab`() {
        assertEquals(ChallengeConfig(holdSeconds = 3, targetRadiusDp = 40f, windLevel = 1), ChallengeConfig.fromMatrikel("000"))
        assertEquals(ChallengeConfig(holdSeconds = 4, targetRadiusDp = 85f, windLevel = 1), ChallengeConfig.fromMatrikel("999"))
    }

    @Test
    fun `ungueltige Matrikelnummern liefern null statt Absturz`() {
        assertNull(ChallengeConfig.fromMatrikel(""))
        assertNull(ChallengeConfig.fromMatrikel("12"))
        assertNull(ChallengeConfig.fromMatrikel("12a456"))
    }

    @Test
    fun `Punkt verlaesst die Spielflaeche nie`() {
        val logic = BalanceLogic(ChallengeConfig.DEFAULT, Random(1)).apply {
            arenaRadiusDp = 150f
            ballRadiusDp = 20f
        }
        repeat(600) {
            logic.onRotation(omegaX = 3f, omegaY = 3f, dt = 0.02f)
            val s = logic.step(1f / 60f)
            assertTrue("Punkt liegt außerhalb der Spielfläche", hypot(s.x, s.y) <= 130.01f)
        }
    }

    @Test
    fun `Drehung bewegt den Punkt`() {
        val logic = BalanceLogic(ChallengeConfig.DEFAULT, Random(4)).apply { arenaRadiusDp = 150f }
        val before = logic.snapshot()
        repeat(10) {
            logic.onRotation(omegaX = 1f, omegaY = 1f, dt = 0.02f)
            logic.step(1f / 60f)
        }
        val after = logic.snapshot()
        assertTrue("Punkt hat sich nicht bewegt", hypot(after.x - before.x, after.y - before.y) > 1f)
    }

    @Test
    fun `Ausserhalb des Ziels bleibt der Timer bei 0`() {
        val logic = BalanceLogic(ChallengeConfig.DEFAULT, Random(2)).apply { arenaRadiusDp = 150f }
        repeat(30) {
            val s = logic.step(1f / 60f)
            if (!s.inside) assertEquals(0f, s.holdTime)
        }
    }
}
