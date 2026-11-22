package de.hhn.umgebungsscanner.challenge

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import de.hhn.umgebungsscanner.ui.components.SoftCard
import de.hhn.umgebungsscanner.ui.theme.AppTheme

private const val BALL_RADIUS_DP = 21f

/**
 * Teil B: Balance-Challenge.
 *
 * Mit dem Gyroskop einen Punkt in den Zielkreis steuern und dort [ChallengeConfig.holdSeconds] Sekunden halten.
 * Die Spielwerte kommen aus der Matrikelnummer ([ChallengeConfig.fromMatrikel]).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeScreen(
    matrikel: String,
    simulateMissingGyro: Boolean,
    bestTime: Float?,
    onExit: () -> Unit,
) {
    val activity = LocalActivity.current
    val config = ChallengeConfig.fromMatrikel(matrikel) ?: ChallengeConfig.DEFAULT
    val logic = remember(config) { BalanceLogic(config) }

    var state by remember { mutableStateOf<BalanceState>(BalanceState.Playing) }
    var snapshot by remember(logic) { mutableStateOf(logic.snapshot()) }

    // Hochformat festhalten: sonst vertauschen sich beim Drehen die Achsen des Gyroskops
    DisposableEffect(activity) {
        val host = activity ?: return@DisposableEffect onDispose { }
        val previous = host.requestedOrientation
        host.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose { host.requestedOrientation = previous }
    }

    // TODO (Teil B): Gyroskop über den SensorManager holen.
    //  Kein Gyroskop (oder simulateMissingGyro == true)? -> state = BalanceState.NoSensor

    // TODO (Teil B): Listener für das Gyroskop an- und wieder abmelden (DisposableEffect).
    //  Jede Messung an logic.onRotation(...) weitergeben, dt aus SensorEvent.timestamp berechnen.

    // TODO (Teil B): Spielschleife: einmal pro Frame logic.step(dt) aufrufen und snapshot aktualisieren.
    //  Tipp: LaunchedEffect + withFrameNanos. Ist das Spiel gewonnen -> state = BalanceState.Won(...)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                title = {
                    Column {
                        Text("Sensor Challenge", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Matr.-Nr. $matrikel",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (val current = state) {
                BalanceState.NoSensor -> {
                    // TODO (Teil B): Verständliche Meldung anzeigen ("Kein Gyroskop gefunden") statt abzustürzen.
                }

                else -> {
                    SoftCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("Balance Challenge", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(8.dp))
                            // TODO (Teil B): "Geschafft!" anzeigen, wenn current is BalanceState.Won
                            Text("${snapshot.holdTime.formatDe()} s von ${config.holdSeconds} s")
                            Text(
                                "Bestzeit: ${bestTime?.let { "${it.formatDe()} s" } ?: "–"} · Windstufe ${config.windLevel}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(18.dp))
                            Arena(
                                snapshot = snapshot,
                                config = config,
                                onArenaMeasured = { radiusDp ->
                                    logic.arenaRadiusDp = radiusDp
                                    logic.ballRadiusDp = BALL_RADIUS_DP
                                },
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        OutlinedButton(
                            onClick = {
                                logic.reset()
                                snapshot = logic.snapshot()
                                state = BalanceState.Playing
                            },
                            modifier = Modifier.weight(1f),
                        ) { Text("Neu starten") }
                        Button(onClick = onExit, modifier = Modifier.weight(1f)) { Text("Beenden") }
                    }
                }
            }
        }
    }
}

@Composable
private fun Arena(
    snapshot: BalanceSnapshot,
    config: ChallengeConfig,
    onArenaMeasured: (Float) -> Unit,
) {
    val extra = AppTheme.extraColors
    val outline = MaterialTheme.colorScheme.outline
    val ballColor = MaterialTheme.colorScheme.primary

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .widthIn(max = 380.dp)
            .aspectRatio(1f),
    ) {
        val radiusDp = maxWidth.value / 2f
        DisposableEffect(radiusDp) {
            onArenaMeasured(radiusDp)
            onDispose { }
        }

        Canvas(Modifier.fillMaxSize()) {
            val arenaRadius = size.minDimension / 2f

            // Spielfläche
            drawCircle(extra.arenaFill, radius = arenaRadius)
            drawCircle(outline, radius = arenaRadius - 1.dp.toPx(), style = Stroke(1.5.dp.toPx()))

            // TODO (Teil B): Zielkreis mit config.targetRadiusDp in der Mitte zeichnen (dp -> px: .dp.toPx()).
            // TODO (Teil B): Punkt mit Radius BALL_RADIUS_DP an Position (snapshot.x, snapshot.y) relativ zur Mitte zeichnen.
        }
    }
}
