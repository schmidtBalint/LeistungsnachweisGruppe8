package de.hhn.umgebungsscanner.scanner

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.hhn.umgebungsscanner.data.LocationRepository
import de.hhn.umgebungsscanner.data.LocationResult
import de.hhn.umgebungsscanner.ui.components.FeatureCard
import de.hhn.umgebungsscanner.ui.components.IconBubble
import de.hhn.umgebungsscanner.ui.components.NoticeCard
import de.hhn.umgebungsscanner.ui.components.SoftCard
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

/**
 * Teil A: Umgebungsscanner.
 *
 * Standort anzeigen, Foto aufnehmen, Permissions zur Laufzeit anfragen und alle Fehlerfälle
 * mit einer verständlichen Meldung behandeln.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    matrikel: String,
    onOpenChallenge: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationRepository = remember { LocationRepository(context) }

    // Zustand des Scanners. Achtung: remember überlebt das Drehen des Geräts nicht (siehe Bonus).
    var location by remember { mutableStateOf<LocationResult?>(null) }
    var loadingLocation by remember { mutableStateOf(false) }
    var photo by remember { mutableStateOf<Bitmap?>(null) }
    var locationNotice by remember { mutableStateOf<Notice?>(null) }
    var cameraNotice by remember { mutableStateOf<Notice?>(null) }

    fun fetchLocation() {
        if (loadingLocation) return
        loadingLocation = true
        locationNotice = null
        scope.launch {
            val result = locationRepository.currentLocation()
            location = result
            loadingLocation = false
            // TODO (Teil A): Je nach Ergebnis eine passende Meldung setzen (locationNotice = Notice(...)),
            //  z. B. bei ServicesDisabled mit Button "Standort einschalten".
        }
    }

    // TODO (Teil A): Launcher für die Standort-Permission (FINE und COARSE gemeinsam) anlegen.
    //  Im Callback: erteilt -> fetchLocation(), verweigert bzw. dauerhaft verweigert -> passende Meldung.

    // TODO (Teil A): Launcher für die Kamera-Permission und für die Kamera (TakePicturePreview) anlegen.
    //  Abbruch der Kamera liefert null: Meldung zeigen, ein vorhandenes Foto bleibt erhalten.

    fun startLocationFlow() {
        // TODO (Teil A): Recht schon da? -> fetchLocation(), sonst Permission anfragen
    }

    fun startCameraFlow() {
        // TODO (Teil A): Recht schon da? -> Kamera starten, sonst Permission anfragen
    }

    fun handleAction(action: NoticeAction?) {
        // TODO (Teil A): Button in einer Meldung auswerten (erneut fragen, Einstellungen öffnen, ...)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconBubble(
                        icon = Icons.Outlined.ViewInAr,
                        size = 46,
                        modifier = Modifier.padding(start = 12.dp, end = 4.dp),
                    )
                },
                title = {
                    Column {
                        Text("Umgebungsscanner", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Matr.-Nr. $matrikel",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Einstellungen", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    // TODO (Teil A): "Scan starten": erst Standort, danach Foto
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Filled.PlayArrow, contentDescription = null) },
                text = { Text("Scan starten") },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                "Erfasse Informationen über deine aktuelle Umgebung.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 6.dp, end = 24.dp),
            )

            FeatureCard(
                icon = Icons.Outlined.LocationOn,
                title = "Standort",
                subtitle = "Aktuellen Standort abrufen",
                loading = loadingLocation,
                onClick = { startLocationFlow() },
            )
            locationNotice?.let { notice ->
                NoticeCard(notice, onAction = { handleAction(notice.action) }, onDismiss = { locationNotice = null })
            }

            FeatureCard(
                icon = Icons.Outlined.PhotoCamera,
                title = "Kamera",
                subtitle = "Foto aufnehmen",
                onClick = { startCameraFlow() },
            )
            cameraNotice?.let { notice ->
                NoticeCard(notice, onAction = { handleAction(notice.action) }, onDismiss = { cameraNotice = null })
            }

            FeatureCard(
                icon = Icons.Outlined.SportsEsports,
                title = "Sensor Challenge",
                subtitle = "Balance Challenge mit dem Gyroskop",
                highlighted = true,
                onClick = onOpenChallenge,
            )

            CurrentDataCard(
                location = location,
                loadingLocation = loadingLocation,
                photo = photo,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

@Composable
private fun CurrentDataCard(
    location: LocationResult?,
    loadingLocation: Boolean,
    photo: Bitmap?,
    modifier: Modifier = Modifier,
) {
    SoftCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 22.dp, vertical = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(16.dp))
                Text("Aktuelle Daten", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.size(14.dp))

            DataRow(Icons.Outlined.LocationOn, "Standort") {
                when {
                    loadingLocation -> DataValue("Wird ermittelt …")
                    location == null -> DataValue("Noch nicht erfasst")
                    location is LocationResult.Success -> Column {
                        DataValue(formatCoordinates(location.latitude, location.longitude), strong = true)
                        val accuracy = location.accuracyMeters?.let { "Genauigkeit ±${it.toInt()} m" } ?: "Genauigkeit unbekannt"
                        DataValue(if (location.approximate) "$accuracy · ungefähr" else accuracy)
                    }
                    location is LocationResult.Failure -> DataValue(location.reason, isError = true)
                    else -> DataValue("Kein Standort", isError = true)
                }
            }
            DataRow(Icons.Outlined.PhotoCamera, "Foto") {
                DataValue(if (photo != null) "Aufgenommen" else "Kein Foto", strong = photo != null)
            }

            photo?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Aufgenommenes Foto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .heightIn(max = 220.dp)
                        .clip(RoundedCornerShape(16.dp)),
                )
            }
        }
    }
}

@Composable
private fun DataRow(icon: ImageVector, label: String, value: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(110.dp),
        )
        Column(Modifier.weight(1f)) { value() }
    }
}

@Composable
private fun DataValue(text: String, isError: Boolean = false, strong: Boolean = false) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (strong) FontWeight.Medium else FontWeight.Normal,
        color = when {
            isError -> MaterialTheme.colorScheme.error
            strong -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        modifier = Modifier.padding(top = 2.dp),
    )
}

private fun formatCoordinates(latitude: Double, longitude: Double): String {
    val lat = String.format(Locale.GERMANY, "%.5f° %s", abs(latitude), if (latitude >= 0) "N" else "S")
    val lon = String.format(Locale.GERMANY, "%.5f° %s", abs(longitude), if (longitude >= 0) "O" else "W")
    return "$lat, $lon"
}
