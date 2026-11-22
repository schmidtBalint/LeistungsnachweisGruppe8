package de.hhn.umgebungsscanner.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.hhn.umgebungsscanner.challenge.ChallengeConfig
import de.hhn.umgebungsscanner.challenge.formatDe

/** Einstellungen: Matrikelnummer, Bestzeit (Bonus) und ein Debug-Schalter zum Testen von "kein Gyroskop". */
@Composable
fun SettingsDialog(
    matrikel: String,
    simulateMissingGyro: Boolean,
    bestTime: Float?,
    onSave: (matrikel: String, simulateMissingGyro: Boolean) -> Unit,
    onResetBestTime: () -> Unit,
    onDismiss: () -> Unit,
) {
    var input by rememberSaveable { mutableStateOf(matrikel) }
    var simulate by rememberSaveable { mutableStateOf(simulateMissingGyro) }
    val valid = input.length >= 3 && input.all { it.isDigit() }
    // Zeigt die Spielwerte an, sobald ChallengeConfig.fromMatrikel() implementiert ist
    val config = if (valid) ChallengeConfig.fromMatrikel(input) else null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Einstellungen") },
        text = {
            Column {
                OutlinedTextField(
                    value = input,
                    onValueChange = { value -> input = value.filter { it.isDigit() }.take(10) },
                    label = { Text("Matrikelnummer") },
                    singleLine = true,
                    isError = !valid,
                    supportingText = { Text(if (!valid) "Mindestens 3 Ziffern" else "Bestimmt deine Spielwerte") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (config != null) {
                    ValueLine("Haltezeit", "${config.holdSeconds} s")
                    ValueLine("Zielradius", "${config.targetRadiusDp.toInt()} dp")
                    ValueLine("Windstufe", "${config.windLevel}")
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Bestzeit: ${bestTime?.let { "${it.formatDe()} s" } ?: "noch keine"}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onResetBestTime, enabled = bestTime != null) { Text("Zurücksetzen") }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Debug: Sensor fehlt", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Tut so, als hätte das Gerät kein Gyroskop.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Switch(checked = simulate, onCheckedChange = { simulate = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(input, simulate)
                    onDismiss()
                },
                enabled = valid,
            ) { Text("Speichern") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } },
    )
}

@Composable
private fun ValueLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
