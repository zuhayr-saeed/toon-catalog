-- V9__add_external_id_and_ratings_to_series.sql
-- Add fields for external reference + ratings aggregation, without duplicating created_at/updated_at

ALTER TABLE series
    ADD COLUMN IF NOT EXISTS external_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS avg_rating DOUBLE PRECISION DEFAULT 0,
    ADD COLUMN IF NOT EXISTS rating_count INT DEFAULT 0;