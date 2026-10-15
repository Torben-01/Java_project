-- Datenbanken, deren Tabelle noch Hibernate angelegt hat, speichern die Kategorie in MySQL als
-- ENUM('AUTO','HOCHZEIT','PORTRAIT'). Neue Kategorien wie SONSTIGES passen dort nicht hinein.
ALTER TABLE requests MODIFY category VARCHAR(20) NOT NULL;
