-- V12_sync_schema_with_entities.sql
-- Final alignment of DB schema with JPA entities

-- =======================  
-- SERIES  
-- =======================  
ALTER TABLE series  
    ADD COLUMN IF NOT EXISTS author VARCHAR(255),  
    ADD COLUMN IF NOT EXISTS description VARCHAR(2000),  
    ADD COLUMN IF NOT EXISTS cover_image VARCHAR(255),  
    ADD COLUMN IF NOT EXISTS genre VARCHAR(255);  
  
ALTER TABLE series  
    ALTER COLUMN avg_rating SET DEFAULT 0,  
    ALTER COLUMN rating_count SET DEFAULT 0;  
  
-- =======================  
-- RATINGS  
-- =======================  
ALTER TABLE ratings  
    ADD COLUMN IF NOT EXISTS review VARCHAR(2000),  
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT now(),  
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT now();  
  
-- =======================  
-- LIST_ENTRY  
-- =======================  
ALTER TABLE list_entry  
    ADD COLUMN IF NOT EXISTS last_updated TIMESTAMP DEFAULT now();  
  
-- =======================  
-- EPISODE  
-- =======================  
CREATE TABLE IF NOT EXISTS episode (  
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),  
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,  
    number INT NOT NULL,  
    title VARCHAR(255),  
    release_date DATE,  
    created_at TIMESTAMP DEFAULT now(),  
    CONSTRAINT uq_episode_series_number UNIQUE (series_id, number)  
);