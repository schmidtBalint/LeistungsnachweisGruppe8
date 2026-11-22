package de.hhn.umgebungsscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import de.hhn.umgebungsscanner.challenge.ChallengeConfig
import de.hhn.umgebungsscanner.challenge.ChallengeScreen
import de.hhn.umgebungsscanner.scanner.HomeScreen
import de.hhn.umgebungsscanner.settings.SettingsDialog
import de.hhn.umgebungsscanner.ui.theme.UmgebungsscannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UmgebungsscannerTheme {
                App()
            }
        }
    }
}

private enum class Screen { HOME, CHALLENGE }

/**
 * Navigation zwischen Umgebungsscanner (Teil A) und Balance-Challenge (Teil B).
 *
 * Die Einstellungen (Matrikelnummer, Debug-Schalter) liegen hier nur per rememberSaveable im Speicher
 * und sind nach einem Neustart der App weg.
 *
 * Bonus: Zustand in ViewModels mit StateFlow verschieben und Bestzeit (pro Matrikelnummer) mit DataStore speichern.
 * Die Abhängigkeiten dafür sind in app/build.gradle.kts schon eingetragen.
 */
@Composable
private fun App() {
    var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
    var showSettings by rememberSaveable { mutableStateOf(false) }

    var matrikel by rememberSaveable { mutableStateOf(ChallengeConfig.DEFAULT_MATRIKEL) }
    var simulateMissingGyro by rememberSaveable { mutableStateOf(false) }

    // Bonus: Bestzeit der Balance-Challenge, soll einen Neustart der App überstehen (DataStore)
    val bestTime: Float? = null

    when (screen) {
        Screen.HOME -> HomeScreen(
            matrikel = matrikel,
            onOpenChallenge = { screen = Screen.CHALLENGE },
            onOpenSettings = { showSettings = true },
        )

        Screen.CHALLENGE -> {
            BackHandler { screen = Screen.HOME }
            ChallengeScreen(
                matrikel = matrikel,
                simulateMissingGyro = simulateMissingGyro,
                bestTime = bestTime,
                onExit = { screen = Screen.HOME },
            )
        }
    }

    if (showSettings) {
        SettingsDialog(
            matrikel = matrikel,
            simulateMissingGyro = simulateMissingGyro,
            bestTime = bestTime,
            onSave = { newMatrikel, simulate ->
                matrikel = newMatrikel
                simulateMissingGyro = simulate
            },
            onResetBestTime = { /* Bonus */ },
            onDismiss = { showSettings = false },
        )
    }
}
