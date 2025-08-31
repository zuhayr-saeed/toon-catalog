CREATE TABLE ratings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    series_id UUID NOT NULL,
    score INT NOT NULL CHECK (score >= 1 AND score <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_series FOREIGN KEY(series_id) REFERENCES series(id) ON DELETE CASCADE,
    CONSTRAINT uc_user_series UNIQUE(user_id, series_id)
);
