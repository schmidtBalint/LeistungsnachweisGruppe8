# Umgebungsscanner – Leistungsnachweis „Permissions und Systemzugriffe“

Labor für App-Entwicklung (AI3), WiSe 2026/27 · Kotlin, Jetpack Compose, Material 3

Aufgabe für zuhause, Umfang ca. ein halber Tag. Das Starterprojekt enthält das Gradle-Setup, alle nötigen
Abhängigkeiten, die Oberfläche und die Navigation zwischen den beiden Teilen. Was fehlt, ist mit `TODO` markiert
(in Android Studio: **View → Tool Windows → TODO**).

**Abgabe bis 30.11.2026, 23:59 Uhr** (Details unten unter [Abgabe](#abgabe)).

---

## Aufbau des Projekts

| Bereich | Datei |
|---|---|
| Navigation, Einstellungen (Matrikelnummer) | `MainActivity.kt`, `settings/SettingsDialog.kt` |
| Startbildschirm Teil A | `scanner/HomeScreen.kt` |
| Hilfsfunktionen für Permissions | `scanner/Permissions.kt` |
| Meldungen (Titel, Text, Button) | `scanner/Notice.kt`, `ui/components/Components.kt` (`NoticeCard`) |
| Standort abfragen | `data/LocationRepository.kt` |
| Permission-Protokoll | `data/PermissionLogger.kt` |
| Balance-Challenge (Screen, Sensor, Canvas) | `challenge/ChallengeScreen.kt` |
| Spiellogik ohne Android | `challenge/BalanceLogic.kt` |
| Individuelle Werte aus der Matrikelnummer | `challenge/ChallengeConfig.kt` |
| Unit-Tests zur Selbstkontrolle | `app/src/test/.../BalanceLogicTest.kt` |
| Manifest | `app/src/main/AndroidManifest.xml` |

Bauen und testen:

```bash
./gradlew assembleDebug testDebugUnitTest
```

Benötigt JDK 17+ und das Android SDK (compileSdk 37). Im Starterstand schlagen einige Unit-Tests absichtlich fehl.
Zum Testen im Emulator ein Image **mit Google Play** verwenden (wegen `FusedLocationProviderClient`).

---

## Teil A: Umgebungsscanner (60 Punkte)

1. **Standort:** Breiten- und Längengrad anzeigen. Vor der Abfrage prüfen, ob die Standortdienste an sind,
   und mit einem Timeout abfragen: Kommt keine Position, gibt die App nach ein paar Sekunden auf, statt endlos zu laden.
2. **Foto:** mit `TakePicturePreview` aufnehmen und in der UI anzeigen. Abbruch behandeln, ein vorhandenes Foto bleibt erhalten.
3. **Permissions:** Standort (FINE und COARSE gemeinsam) und Kamera zur Laufzeit anfragen.
   - verweigert: Erklärung und „Erneut fragen“
   - dauerhaft verweigert: Hinweis mit Button zu den App-Einstellungen
4. **„Scan starten“:** erst Standort, danach Foto.
5. **Fehlerfälle:** Die App darf in keinem Fall abstürzen und zeigt jedes Mal eine verständliche Meldung.
6. **Permission-Protokoll:** siehe nächster Abschnitt.

### Fehlerfälle

Für jeden Fall gehört ein Screenshot in den Ordner `screenshots/`, auf dem die Meldung der App **und eure
Matrikelnummer in der oberen Leiste** zu sehen sind. Tragt in der Tabelle ein, wie ihr den Fall ausgelöst habt.

| Fall | So ausgelöst (ausfüllen) | Screenshot |
|---|---|---|
| Standortrecht verweigert | | |
| Standortrecht dauerhaft verweigert | | |
| GPS aus | | |
| Kein Standort (Timeout) | | |
| Kamerarecht verweigert | | |
| Kamerarecht dauerhaft verweigert | | |
| Kamera abgebrochen | | |
| Kein Gyroskop (Teil B) | | |

Tipps: Ein Recht lässt sich mit `adb shell pm revoke <applicationId> <recht>` wieder entziehen.
„Kein Standort“ bekommt ihr z. B. mit einem Cold Boot des Emulators ohne gesetzte Position.
„Kein Gyroskop“ lässt sich über den Debug-Schalter in den Einstellungen (Zahnrad) simulieren.

### Permission-Protokoll

Die App schreibt **jede Interaktion mit einer Permission** als eine Zeile in die Datei `permissions.log`
im internen Speicher der App (`context.filesDir`). Dafür braucht man keine Berechtigung.

Format: `Zeitpunkt | Recht | Ereignis`

```
2026-11-28 14:32:05 | ACCESS_FINE_LOCATION | ANGEFRAGT
2026-11-28 14:32:09 | ACCESS_FINE_LOCATION | VERWEIGERT
2026-11-28 14:33:12 | ACCESS_FINE_LOCATION | DAUERHAFT_VERWEIGERT
2026-11-28 14:33:20 | ACCESS_FINE_LOCATION | EINSTELLUNGEN_GEOEFFNET
2026-11-28 14:35:41 | CAMERA | ANGEFRAGT
2026-11-28 14:35:44 | CAMERA | ERTEILT
```

| Ereignis | Wann |
|---|---|
| `ANGEFRAGT` | kurz bevor der Permission-Dialog gestartet wird |
| `ERTEILT` / `VERWEIGERT` | im Callback, je nach Ergebnis |
| `DAUERHAFT_VERWEIGERT` | im Callback, wenn abgelehnt und Android nicht mehr nachfragen wird |
| `EINSTELLUNGEN_GEOEFFNET` | wenn der Nutzer über den Hinweis in die App-Einstellungen springt |

- Zeitpunkt im Format `yyyy-MM-dd HH:mm:ss`
- Recht ohne das Präfix `android.permission.`, also z. B. `ACCESS_FINE_LOCATION`
- Bei FINE und COARSE zusammen: pro Recht eine Zeile
- Neue Zeilen werden angehängt, die Datei wird nicht überschrieben

**Datei vom Emulator holen:**

- Android Studio: **View → Tool Windows → Device Explorer**, dann
  `/data/data/de.hhn.umgebungsscanner/files/permissions.log`, Rechtsklick → **Save As**
- oder im Terminal (nur Debug-Build):
  ```bash
  adb shell run-as de.hhn.umgebungsscanner cat files/permissions.log > permissions.log
  ```

Die Datei kommt ins **Hauptverzeichnis** des Repos und wird mit committet. Sie sollte alle Fehlerfälle
enthalten, die ihr auf den Screenshots zeigt.

---

## Teil B: Balance-Challenge (40 Punkte)

Ein kleines Geschicklichkeitsspiel: In der Mitte ist ein Zielkreis. Ein Punkt bewegt sich, wenn man das Gerät
dreht bzw. kippt. Zusätzlich schiebt „Wind“ den Punkt mit kleinen zufälligen Stößen weg. Ziel ist, den Punkt eine
bestimmte Zeit **ununterbrochen** im Zielkreis zu halten. Verlässt er den Kreis, beginnt die Zeit von vorn. Ist die
Haltezeit erreicht, erscheint **„Geschafft!“**.

Anforderungen:

- Gyroskop über den `SensorManager`, an- und abmelden mit `DisposableEffect`
- Bewegung aus den Messwerten berechnen: Gyroskop (ω) → Geschwindigkeit (`v += ω · k`) → Wind (`v += Zufall`) → Position (`p += v`)
- Zielkreis und Punkt mit Compose `Canvas` zeichnen
- Der Punkt bleibt am Rand der Spielfläche hängen und verlässt sie nie
- Timer: läuft nur, solange der Punkt im Kreis ist
- Kein Gyroskop: verständliche Meldung statt Absturz

Hinweis: Das Gyroskop misst **Drehgeschwindigkeit**, nicht Neigung. Hält man das Gerät still, ist ω ungefähr 0.

### Individuelle Werte

Tragt eure Matrikelnummer in den Einstellungen (Zahnrad oben rechts) ein. Sie steht in der oberen Leiste
und bestimmt eure Spielwerte:

| Ziffer | Wert |
|---|---|
| letzte | Haltezeit = 3 + (Ziffer mod 4) s |
| vorletzte | Zielradius = 40 + Ziffer · 5 dp |
| drittletzte | Windstufe = 1 + (Ziffer mod 3) |

Beispiel 214857: Haltezeit 6 s, Radius 65 dp, Windstufe 3.

| Windstufe | Stärke eines Stoßes | Abstand zwischen zwei Stößen |
|---|---|---|
| 1 | ca. 18 dp/s | 1,6 – 2,6 s |
| 2 | ca. 28 dp/s | 1,1 – 1,9 s |
| 3 | ca. 38 dp/s | 0,7 – 1,3 s |

(Die Werte stehen auch in `WindSettings` in `ChallengeConfig.kt`.)

Für die Abgabe: ein Screenshot der Balance-Challenge mit der Meldung „Geschafft!“ und eurer Matrikelnummer.

### Testen im Emulator

Extended Controls (drei Punkte neben dem Emulator) → **Virtual sensors** → Tab **Device Pose** → Modus **Rotate**,
dann das 3D-Handy mit der Maus in kleinen Bewegungen ziehen. Das Gyroskop liefert nur Werte, solange sich das Gerät dreht.

---

## Bonus (+10 Punkte, maximal 100 Punkte gesamt)

- Zustand in ViewModels mit `StateFlow`, in der UI mit `collectAsStateWithLifecycle()` lesen (Foto und Standort überstehen dann das Drehen)
- Bestzeit der Balance-Challenge (pro Matrikelnummer) mit DataStore speichern, sodass sie einen Neustart der App übersteht

Die Abhängigkeiten (`lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`, `datastore-preferences`) sind schon eingetragen.

---

## Punkteraster

| Teil | Kriterium | Punkte |
|---|---|---|
| A | Standort anzeigen, mit GPS-Prüfung und Timeout | 10 |
| A | Foto aufnehmen und anzeigen, Abbruch behandelt | 8 |
| A | Standort-Permission: FINE und COARSE gemeinsam, verweigert, dauerhaft verweigert → Einstellungen | 12 |
| A | Kamera-Permission: verweigert, dauerhaft verweigert → Einstellungen | 8 |
| A | Fehlerfälle ohne Absturz mit verständlicher Meldung, Screenshots und Beschreibung in der README | 12 |
| A | Permission-Protokoll `permissions.log` (Format, alle Ereignisse, Datei im Repo) | 10 |
| B | Gyroskop über `SensorManager`, An- und Abmelden mit `DisposableEffect` | 8 |
| B | Bewegung: `v += ω · k`, Wind nach Windstufe, `p += v` | 10 |
| B | Canvas: Zielkreis und Punkt, Punkt bleibt in der Spielfläche | 8 |
| B | Timer mit Neustart beim Verlassen, „Geschafft!“ | 8 |
| B | Individuelle Werte aus der Matrikelnummer, Anzeige in der oberen Leiste | 6 |
| Bonus | ViewModel mit `StateFlow` | 5 |
| Bonus | Bestzeit mit DataStore | 5 |
| | **Summe** | **100 (+10)** |

Bewertet wird, ob jeder Fall korrekt behandelt ist, nicht wie schön die App aussieht.

---

## Abgabe

1. **Klonen:** Dieses Repository klonen bzw. als Vorlage für ein eigenes Repository verwenden.
   Euer Repo muss für die Bewertung **öffentlich** sein.
2. **Bearbeiten:** Ganz normal committen und pushen, gern oft. Mit ins Repo gehören:
   - euer Code
   - `permissions.log` im Hauptverzeichnis
   - die Screenshots in `screenshots/` und die ausgefüllte Tabelle „Fehlerfälle“ oben in dieser README
3. **Pushen:** Der letzte Commit heißt **`fertig`** und ist vor der Frist gepusht:
   ```bash
   git commit -m "fertig"
   git push
   ```
   Erst der Push zählt. Wer danach noch etwas ändert, nennt den neuen letzten Commit wieder `fertig`.
4. **Ilias:** Eine Textdatei `Nachname-Vorname-Matrikelnummer.txt` mit dem Link zu eurem Repo über Ilias abgeben.

**Abgabe über Ilias bis 30.11.2026, 23:59 Uhr.** Wer zu spät abgibt, bekommt keine Punkte.

### KI-Werkzeuge

KI-Werkzeuge (ChatGPT, Copilot, Gemini, …) dürft ihr nutzen, um etwas zu **verstehen**, z. B. eine Fehlermeldung
oder ein Konzept aus dem Vortrag. Zum blinden Lösen der Aufgabe sind sie nicht erlaubt. Ihr müsst jeden Teil eures
Codes erklären können. Bei Zweifeln bitten wir euch zu einem kurzen Gespräch. Wer seinen Code dort nicht erklären
kann oder blind mit KI gelöst hat, bekommt keine Punkte.

Bei Fragen: baschmidt@stud.hs-heilbronn.de, brestelica@stud.hs-heilbronn.de
