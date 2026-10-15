# Java_project

## Starten

### Lokal ohne MySQL (dev-Profil)

Für die Entwicklung reicht eine eingebettete H2-Datenbank, die unter `./data` gespeichert wird:

    mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

In IntelliJ in der Run-Konfiguration unter *Active profiles* `dev` eintragen.

### Mit MySQL

Voraussetzung ist eine laufende MySQL-Instanz mit der Datenbank `photography_db`. Die Tabellen legt Hibernate
beim Start selbst an. Die Zugangsdaten werden über Umgebungsvariablen gesetzt:

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
sie werden nach einem Neustart automatisch angezeigt (unterstützt: jpg, jpeg, png, webp, gif, avif, svg).

    src/main/resources/META-INF/resources/images/
    ├── startseite/hintergrund/   Hintergrund oben auf der Startseite (erstes Bild)
    ├── portrait/
    │   ├── cover/                Kachel auf der Startseite (erstes Bild, Querformat 3:2)
    │   ├── cover-vertikal/       Hochformat-Bild neben dem Anfrageformular (erstes Bild, 2:3)
    │   └── beispiele/            Galerie unter dem Formular (alle Bilder)
    ├── auto/                     wie portrait
    └── hochzeit/                 wie portrait

Die Bilder werden nach Dateinamen sortiert, die Reihenfolge lässt sich also mit `01_`, `02_` usw. steuern.
Wer ein Bild löscht, sollte danach `mvnw.cmd clean` ausführen, sonst bleibt es in `target/classes` liegen.
