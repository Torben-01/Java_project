#!/usr/bin/env bash
# Holt den neuesten Stand aus Git und startet die Website damit neu.
# Die Daten in der Datenbank, die Bilder und .env bleiben erhalten.
set -euo pipefail
cd "$(dirname "$0")"

git pull --ff-only
./deploy.sh
