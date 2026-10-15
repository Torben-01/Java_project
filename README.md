# Java_project

## Voraussetzungen

- Java 21 oder neuer (in IntelliJ unter *File → Project Structure → SDK* einstellen)
- Node.js wird von Vaadin beim ersten Start automatisch installiert

## Starten

### Lokal ohne MySQL (dev-Profil)

Für die Entwicklung reicht eine eingebettete H2-Datenbank, die unter `./data` gespeichert wird:

    mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

In IntelliJ in der Run-Konfiguration unter *Active profiles* `dev` eintragen.

### Mit MySQL

Voraussetzung ist eine laufende MySQL-Instanz mit der Datenbank `photography_db`. Die Tabellen legt Flyway beim
Start an bzw. aktualisiert sie (Skripte unter `src/main/resources/db/migration`). Eine bestehende Datenbank ohne
Flyway-Historie wird automatisch übernommen. Die Zugangsdaten werden über Umgebungsvariablen gesetzt:

| Variable      | Standard                                         |
|---------------|--------------------------------------------------|
| `DB_URL`      | `jdbc:mysql://localhost:3306/photography_db?...` |
| `DB_USERNAME` | `test_user`                                      |
| `DB_PASSWORD` | leer                                             |

Windows (PowerShell):

    $env:DB_PASSWORD = "..."
    mvnw.cmd spring-boot:run

Linux/macOS:

    DB_PASSWORD=... ./mvnw spring-boot:run

Die App läuft danach unter http://localhost:8080.

## Bilder

Bilder werden nicht ins Git eingecheckt, nur die Ordnerstruktur. Bilder einfach in den passenden Ordner legen,
sie erscheinen sofort, ohne Neustart (unterstützt: jpg, jpeg, png, webp, gif, avif, svg).

    bilder/
    ├── startseite/hintergrund/   Hintergrund oben auf der Startseite (erstes Bild)
    ├── portrait/
    │   ├── cover/                Karte auf der Startseite (erstes Bild, Querformat 3:2)
    │   ├── cover-vertikal/       Hochformat-Bild auf der Kategorieseite (erstes Bild, 2:3)
    │   └── beispiele/            Galerie auf der Kategorieseite (alle Bilder)
    ├── auto/                     wie portrait
    └── hochzeit/                 wie portrait

Die Bilder werden nach Dateinamen sortiert, die Reihenfolge lässt sich also mit `01_`, `02_` usw. steuern.
Auf einem Server kann der Ordner über die Umgebungsvariable `IMAGES_DIR` an einen beliebigen Ort gelegt werden.
