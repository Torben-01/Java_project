-- Bearbeitungsstatus für die Verwaltung der Anfragen
ALTER TABLE requests ADD COLUMN status VARCHAR(20) DEFAULT 'OFFEN' NOT NULL;
