CREATE TABLE scraper_runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    started_at TIMESTAMP NOT NULL DEFAULT now(),
    finished_at TIMESTAMP,
    series_added INT NOT NULL DEFAULT 0,
    series_updated INT NOT NULL DEFAULT 0,
    series_failed INT NOT NULL DEFAULT 0
);