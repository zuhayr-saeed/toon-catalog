-- Enable UUID support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT NOW()
);

-- Series
CREATE TABLE IF NOT EXISTS series (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('WEBTOON','WEBNOVEL')),
    synopsis TEXT,
    cover_image_url TEXT,
    genres TEXT[],
    tags TEXT[],
    authors TEXT[],
    created_at TIMESTAMP DEFAULT NOW()
);

-- Episodes/Chapters
CREATE TABLE IF NOT EXISTS episode (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,
    number INT NOT NULL,
    title VARCHAR(255),
    release_date DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT uq_episode UNIQUE(series_id, number)
);

-- Ratings
CREATE TABLE IF NOT EXISTS rating (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,
    score INT NOT NULL CHECK(score BETWEEN 1 AND 5),
    review TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT uq_rating UNIQUE(user_id, series_id)
);

-- List Entries
CREATE TABLE IF NOT EXISTS list_entry (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    series_id UUID NOT NULL REFERENCES series(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL CHECK(status IN ('READING','COMPLETED','ON_HOLD','DROPPED','PLAN_TO_READ')),
    progress INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT NOW(),
    CONSTRAINT uq_list UNIQUE(user_id, series_id)
);
