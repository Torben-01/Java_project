#!/usr/bin/env bash
# Startet die komplette Website (Datenbank, App, HTTPS) bzw. startet sie nach Änderungen neu.
# Beim ersten Aufruf wird .env mit zufälligen Passwörtern angelegt.
#
#   ./deploy.sh                  fragt beim ersten Mal nach der Domain
#   ./deploy.sh meine-domain.de  setzt die Domain direkt
set -euo pipefail
cd "$(dirname "$0")"

fail() { echo "FEHLER: $*" >&2; exit 1; }

command -v docker >/dev/null 2>&1 || fail "Docker ist nicht installiert, siehe README, Abschnitt 'Auf einem Server betreiben'."
docker compose version >/dev/null 2>&1 || fail "Docker Compose fehlt (Paket docker-compose-plugin)."

# reads a fixed amount of random bytes, so no pipe is closed early (which would fail with pipefail)
random_secret() { head -c 1024 /dev/urandom | LC_ALL=C tr -dc 'A-Za-z0-9' | cut -c1-32; }
set_value() { sed -i "s|^$1=.*|$1=$2|" .env; }
get_value() { grep -E "^$1=" .env | head -1 | cut -d= -f2-; }

if [ ! -f .env ]; then
    cp .env.example .env
    chmod 600 .env
    set_value DB_PASSWORD "$(random_secret)"
    set_value DB_ROOT_PASSWORD "$(random_secret)"
    admin_password="$(random_secret)"
    set_value ADMIN_PASSWORD "$admin_password"
    echo "Neue Einstellungen in .env angelegt."
    echo "Admin-Zugang: $(get_value ADMIN_USERNAME) / $admin_password  (steht auch in .env)"
fi

if [ $# -ge 1 ]; then
    set_value DOMAIN "$1"
fi
domain="$(get_value DOMAIN)"
if [ -z "$domain" ] || [ "$domain" = "example.com" ]; then
    if [ -t 0 ]; then
        read -r -p "Domain der Website (z. B. torben-photography.de): " domain
        [ -n "$domain" ] || fail "Keine Domain angegeben."
        set_value DOMAIN "$domain"
    else
        fail "Bitte die Domain angeben: ./deploy.sh meine-domain.de"
    fi
fi

# Ordnerstruktur für die Bilder (Inhalt wird nicht eingecheckt)
for category in portrait auto hochzeit; do
    mkdir -p "bilder/$category/cover" "bilder/$category/cover-vertikal" "bilder/$category/beispiele"
done
mkdir -p bilder/startseite/hintergrund

echo "Baue und starte die Website für $domain ..."
if ! docker compose up -d --build; then
    docker compose ps
    docker compose logs --tail 80 app
    fail "Start fehlgeschlagen, siehe Log oben."
fi

echo "Warte, bis die App bereit ist (beim ersten Start einige Minuten) ..."
for _ in $(seq 1 60); do
    status="$(docker inspect --format '{{.State.Health.Status}}' "$(docker compose ps -q app)" 2>/dev/null || true)"
    if [ "$status" = "healthy" ]; then
        echo "Fertig: https://$domain"
        echo "Verwaltung: https://$domain/admin/anfragen"
        exit 0
    fi
    sleep 5
done

docker compose logs --tail 50 app
fail "Die App ist nach 5 Minuten nicht bereit, siehe Log oben (docker compose logs app)."
