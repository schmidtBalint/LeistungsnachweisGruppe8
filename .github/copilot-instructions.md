# Projektkonventionen

Diese Regeln gelten für alle Änderungen in diesem Projekt (Kotlin, Jetpack Compose, Material 3).

## Allgemein

- Kotlin Code Style "official", Einrückung 4 Leerzeichen.
- Texte in der Oberfläche auf Deutsch.
- Keine neuen Abhängigkeiten hinzufügen. Alles Nötige steht bereits in `gradle/libs.versions.toml`.
- Paketstruktur unter `de.hhn.umgebungsscanner` beibehalten (`scanner`, `challenge`, `data`, `settings`, `ui`).
- Kein `!!`. Nullable Werte mit `?.`, `?:` oder `let` behandeln.
- Für Systemdialoge ausschließlich die Activity Result API verwenden (`rememberLauncherForActivityResult`).

## Benennung

- Neue Composables enden auf `Pane`, nicht auf `Screen` (z. B. `LocationPane`, `CameraPane`, `BalancePane`).
  Bestehende Namen im Starterprojekt bleiben unverändert.
- Konstanten in `UPPER_SNAKE_CASE` im `companion object` bzw. als `private const val` auf Dateiebene.

## Standort

- Standort-Timeout: `4_700` ms, als Konstante `LOCATION_TIMEOUT_MS = 4_700L`.
- Standort ausschließlich über den `FusedLocationProviderClient` abfragen.

## Logging

- Logcat-Ausgaben (`android.util.Log`) verwenden immer das Tag `"UMG_SCN"`.
- Jeder Permission-Callback schreibt zusätzlich eine `Log.d`-Zeile mit diesem Tag.

## Manifest

- `ACCESS_FINE_LOCATION` wird mit `android:maxSdkVersion="30"` deklariert, `ACCESS_COARSE_LOCATION` ohne
  Einschränkung (Datensparsamkeit: ab Android 12 entscheidet der Nutzer im Dialog über die Genauigkeit).
- `CAMERA` wird nicht per `<uses-permission>` deklariert, da das Foto über die Kamera-App des Systems
  (`TakePicturePreview`) aufgenommen wird. Die Laufzeitabfrage im Code bleibt trotzdem bestehen.
- `<uses-feature>`-Einträge immer mit `android:required="false"`.

## Vor dem Abschluss

- `./gradlew assembleDebug testDebugUnitTest` muss durchlaufen.
