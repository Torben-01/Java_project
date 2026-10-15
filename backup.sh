#!/usr/bin/env bash
# Sichert Datenbank und Bilder nach ./backups, die letzten 14 Sicherungen bleiben erhalten.
# Für eine tägliche Sicherung per cron:  0 3 * * * /pfad/zum/projekt/backup.sh
set -euo pipefail
cd "$(dirname "$0")"

mkdir -p backups
stamp="$(date +%Y-%m-%d_%H-%M)"

docker compose exec -T db sh -c 'exec mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --single-transaction photography_db' \
    | gzip > "backups/datenbank_$stamp.sql.gz"
tar -czf "backups/bilder_$stamp.tar.gz" bilder

# ältere Sicherungen löschen
ls -1t backups/datenbank_*.sql.gz | tail -n +15 | xargs -r rm --
ls -1t backups/bilder_*.tar.gz | tail -n +15 | xargs -r rm --

echo "Sicherung erstellt: backups/datenbank_$stamp.sql.gz und backups/bilder_$stamp.tar.gz"
