-- Schema der Anfragen wie es bis zur Einführung von Flyway von Hibernate angelegt wurde.
-- IF NOT EXISTS, damit bestehende Datenbanken übernommen werden (baseline-on-migrate).
CREATE TABLE IF NOT EXISTS requests (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    salutation  VARCHAR(20)   NOT NULL,
    first_name  VARCHAR(100)  NOT NULL,
    last_name   VARCHAR(100)  NOT NULL,
    email       VARCHAR(255)  NOT NULL,
    date        DATE          NOT NULL,
    comment     VARCHAR(1000),
    category    VARCHAR(20)   NOT NULL,
    created_at  DATETIME(6)   NOT NULL
);
