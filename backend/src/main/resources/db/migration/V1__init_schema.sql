CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, role)
);

CREATE TABLE series (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    type VARCHAR(32) NOT NULL,
    synopsis VARCHAR(4000),
    cover_image_url VARCHAR(1000),
    external_id VARCHAR(255) UNIQUE,
    avg_rating DOUBLE PRECISION NOT NULL DEFAULT 0,
    rating_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_series_title ON series(title);
CREATE INDEX idx_series_created_at ON series(created_at);
CREATE INDEX idx_series_avg_rating ON series(avg_rating);

CREATE TABLE series_genres (
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,
    genre VARCHAR(255) NOT NULL,
    PRIMARY KEY (series_id, genre)
);

CREATE TABLE series_tags (
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (series_id, tag)
);

CREATE TABLE series_authors (
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,
    author VARCHAR(255) NOT NULL,
    PRIMARY KEY (series_id, author)
);

CREATE TABLE episode (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,
    number INTEGER NOT NULL,
    title VARCHAR(255),
    release_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_episode_series_number UNIQUE (series_id, number)
);

CREATE INDEX idx_episode_series_number ON episode(series_id, number);

CREATE TABLE list_entry (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    progress INTEGER NOT NULL DEFAULT 0,
    favorite BOOLEAN NOT NULL DEFAULT FALSE,
    last_updated TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_list_entry_user_series UNIQUE (user_id, series_id)
);

CREATE INDEX idx_list_entry_user ON list_entry(user_id);
CREATE INDEX idx_list_entry_status ON list_entry(status);
CREATE INDEX idx_list_entry_favorite ON list_entry(favorite);
CREATE INDEX idx_list_entry_last_updated ON list_entry(last_updated);

CREATE TABLE ratings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,
    score INTEGER NOT NULL CHECK (score >= 1 AND score <= 10),
    review VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_ratings_user_series UNIQUE (user_id, series_id)
);

CREATE INDEX idx_ratings_series ON ratings(series_id);

CREATE TABLE follows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    follower_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    following_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_follows_pair UNIQUE (follower_id, following_id)
);

CREATE INDEX idx_follows_follower ON follows(follower_id);
CREATE INDEX idx_follows_following ON follows(following_id);

CREATE TABLE scraper_runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    started_at TIMESTAMP NOT NULL DEFAULT now(),
    finished_at TIMESTAMP,
    series_added INTEGER NOT NULL DEFAULT 0,
    series_updated INTEGER NOT NULL DEFAULT 0,
    series_failed INTEGER NOT NULL DEFAULT 0
);
