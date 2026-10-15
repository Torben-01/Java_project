# Torben Photography

Webseite für Fotoshootings (Portrait, Auto, Hochzeit) mit Anfrageformular und einer Verwaltung der
eingegangenen Anfragen. Gebaut mit Spring Boot 4 und Vaadin 25.

- Startseite, Kategorieseiten mit Galerie und ein eigenes Anfrageformular
- Navigation oben, auf kleinen Bildschirmen als ausklappbares Menü
- Deutsch und Englisch, umschaltbar in der Navigationsleiste
- Hell- und Dunkelmodus, standardmäßig nach Systemeinstellung
- Login für den Fotografen mit Übersicht aller Anfragen (filtern, erledigen, löschen)
- E-Mail-Benachrichtigung bei neuen Anfragen
- Datenschutz-Einwilligung im Formular, Seiten für Impressum und Datenschutz

## Voraussetzungen

- Java 21 oder neuer (in IntelliJ unter *File → Project Structure → SDK* einstellen)
- Node.js wird von Vaadin beim ersten Start automatisch installiert

## Starten

### Lokal ohne MySQL (dev-Profil)

Für die Entwicklung reicht eine eingebettete H2-Datenbank, die unter `./data` gespeichert wird:

    mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

In IntelliJ: *Run → Edit Configurations… → PhotographyApplication → Modify options → Add VM options* und dort
`-Dspring.profiles.active=dev` eintragen (in IntelliJ Ultimate gibt es dafür auch das Feld *Active profiles*).
Ohne das Profil versucht die App, sich mit MySQL zu verbinden. Im dev-Profil ist die Verwaltung unter
http://localhost:8080/admin/anfragen mit `admin` / `admin` erreichbar.

### Mit MySQL

Voraussetzung ist eine laufende MySQL-Instanz mit der Datenbank `photography_db`. Die Tabellen legt Flyway beim
Start an bzw. aktualisiert sie (Skripte unter `src/main/resources/db/migration`). Eine bestehende Datenbank ohne
Flyway-Historie wird automatisch übernommen.

Windows (PowerShell):

    $env:DB_PASSWORD = "..."
    $env:ADMIN_PASSWORD = "..."
    mvnw.cmd spring-boot:run

Linux/macOS:

    DB_PASSWORD=... ADMIN_PASSWORD=... ./mvnw spring-boot:run

Die App läuft danach unter http://localhost:8080.

## Konfiguration

Alle Einstellungen lassen sich über Umgebungsvariablen setzen:

| Variable                        | Bedeutung                                              | Standard                                         |
|---------------------------------|--------------------------------------------------------|--------------------------------------------------|
| `DB_URL`                        | JDBC-URL der MySQL-Datenbank                           | `jdbc:mysql://localhost:3306/photography_db?...` |
| `DB_USERNAME` / `DB_PASSWORD`   | Zugang zur Datenbank                                   | `test_user` / leer                               |
| `ADMIN_USERNAME`                | Benutzername für die Verwaltung                        | `admin`                                          |
| `ADMIN_PASSWORD`                | Passwort für die Verwaltung, ohne Passwort kein Login  | leer                                             |
| `IMAGES_DIR`                    | Ordner mit den Bildern                                 | `./bilder`                                       |
| `NOTIFICATION_RECIPIENT`        | E-Mail-Adresse für neue Anfragen                       | leer (nur Log-Eintrag)                           |
| `NOTIFICATION_SENDER`           | Absender der E-Mails                                   | wie Empfänger                                    |
| `NOTIFICATION_CONFIRM_CUSTOMER` | Bestätigung an den Kunden schicken                     | `false`                                          |
| `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` | Mailserver | nicht gesetzt |

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

## Texte und Sprachen

Alle Texte der Oberfläche stehen in `src/main/resources/vaadin-i18n/translations_de.properties` und
`translations_en.properties`. Für einen neuen Text in beiden Dateien denselben Schlüssel anlegen und im Code
mit `getTranslation("schlüssel")` verwenden.

## Tests

    mvnw.cmd test      # schnelle Unit-Tests
    mvnw.cmd verify    # zusätzlich die Browsertests (Playwright + Drama Finder)

Die Browsertests (`src/test/java/de/torbenphotography/ui/*IT.java`) starten die App selbst und laufen ohne
sichtbares Browserfenster. Beim ersten Lauf lädt Playwright einmalig Chromium herunter. Zum Zuschauen:

    mvnw.cmd verify -Dheadless=false

Auf GitHub laufen bei jedem Push alle Tests (`.github/workflows/build.yml`). Zusätzlich startet
`.github/workflows/deployment.yml` den kompletten Server-Stack mit `deploy.sh` und prüft ihn von außen.

## Auf einem Server betreiben

Auf dem Server laufen drei Container: MySQL, die App und [Caddy](https://caddyserver.com) als Webserver, der
automatisch ein HTTPS-Zertifikat für die Domain besorgt und erneuert. Gestartet wird alles mit einem Befehl.

### Einmalig vorbereiten

1. Ein Linux-Server (z. B. Ubuntu 24.04) mit mindestens 2 GB RAM.
2. Die Domain zeigt per DNS (A-Record) auf die IP des Servers, die Ports 80 und 443 sind offen.
3. Docker mit Compose-Plugin installieren, siehe https://docs.docker.com/engine/install/ubuntu/.
   Danach den eigenen Benutzer in die Gruppe `docker` aufnehmen: `sudo usermod -aG docker $USER`
   (neu anmelden).
4. Das Projekt holen:

       git clone https://github.com/Torben-01/Java_project.git
       cd Java_project

### Starten

    ./deploy.sh meine-domain.de

Beim ersten Aufruf legt das Skript die Datei `.env` mit zufälligen Passwörtern an und zeigt den Admin-Zugang
an (er steht auch in `.env`). Der erste Start dauert einige Minuten, weil das Image gebaut wird. Danach ist die
Website unter `https://meine-domain.de` erreichbar, die Verwaltung unter `https://meine-domain.de/admin/anfragen`.
Die Container starten nach einem Neustart des Servers automatisch wieder.

Weitere Einstellungen wie der Mailserver stehen in `.env`. Nach einer Änderung einfach `./deploy.sh` erneut
ausführen.

### Bilder hochladen

Die Bilder kommen auf dem Server in den Ordner `bilder/` im Projekt (Aufbau siehe Abschnitt *Bilder*), z. B. per
`scp` oder SFTP. Sie erscheinen sofort, ein Neustart ist nicht nötig.

### Aktualisieren, Sichern, Logs

| Befehl                           | Wirkung                                                        |
|----------------------------------|----------------------------------------------------------------|
| `./update.sh`                    | neuesten Stand aus Git holen und neu starten, Daten bleiben     |
| `./backup.sh`                    | Datenbank und Bilder nach `backups/` sichern (behält 14 Stück) |
| `docker compose logs -f app`     | Log der App ansehen                                            |
| `docker compose ps`              | Status der Container                                           |
| `docker compose down`            | alles stoppen (Daten bleiben erhalten)                         |

Für eine tägliche Sicherung um 3 Uhr per `crontab -e` eintragen:

    0 3 * * * /home/<benutzer>/Java_project/backup.sh

Eine Sicherung zurückspielen:

    gunzip -c backups/datenbank_<datum>.sql.gz | docker compose exec -T db sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" photography_db'

Die Sicherungen liegen auf demselben Server. Sie sollten zusätzlich regelmäßig an einen anderen Ort kopiert
werden.

## Vor der Veröffentlichung

- Impressum und Datenschutzerklärung vervollständigen: Die Angaben in eckigen Klammern in den
  Sprachdateien (`imprint.*`, `privacy.*`) ergänzen und rechtlich prüfen lassen
- `ADMIN_PASSWORD` auf ein sicheres Passwort setzen (macht `deploy.sh` beim ersten Start automatisch)
- Mailserver und `NOTIFICATION_RECIPIENT` einrichten, damit Anfragen per E-Mail ankommen
