-- Create company table
CREATE TABLE IF NOT EXISTS company
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
    );