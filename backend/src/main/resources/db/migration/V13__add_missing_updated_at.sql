-- V13__add_missing_updated_at.sql
-- Final patch to align schema with JPA entities

-- =======================
-- SERIES
-- =======================
ALTER TABLE series
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT now();

-- Guard: also make sure created_at exists with a default
ALTER TABLE series
    ALTER COLUMN created_at SET DEFAULT now();
