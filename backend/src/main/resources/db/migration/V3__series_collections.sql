-- Clean up the old series table array columns (if they exist)  
ALTER TABLE series DROP COLUMN IF EXISTS authors;  
ALTER TABLE series DROP COLUMN IF EXISTS genres;  
ALTER TABLE series DROP COLUMN IF EXISTS tags;  
  
-- Normalized authors  
CREATE TABLE IF NOT EXISTS series_authors (  
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,  
    author VARCHAR(255) NOT NULL,  
    PRIMARY KEY (series_id, author)  
);  
  
-- Normalized genres  
CREATE TABLE IF NOT EXISTS series_genres (  
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,  
    genre VARCHAR(255) NOT NULL,  
    PRIMARY KEY (series_id, genre)  
);  
  
-- Normalized tags  
CREATE TABLE IF NOT EXISTS series_tags (  
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,  
    tag VARCHAR(255) NOT NULL,  
    PRIMARY KEY (series_id, tag)  
);  
