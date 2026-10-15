-- Zeitpunkt der Einwilligung in die Datenschutzerklärung
ALTER TABLE requests ADD COLUMN privacy_accepted_at DATETIME(6);
