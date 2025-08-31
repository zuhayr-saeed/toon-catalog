-- Add username if missing
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='users' AND column_name='username'
    ) THEN
        ALTER TABLE users ADD COLUMN username VARCHAR(255);
    END IF;
END$$;

-- Add email if missing
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='users' AND column_name='email'
    ) THEN
        ALTER TABLE users ADD COLUMN email VARCHAR(255);
    END IF;
END$$;

-- Add password if missing
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='users' AND column_name='password'
    ) THEN
        ALTER TABLE users ADD COLUMN password VARCHAR(255);
    END IF;
END$$;

-- Backfill username if needed (optional heuristic)
-- UPDATE users SET username = split_part(email, '@', 1) WHERE username IS NULL AND email IS NOT NULL;

-- Unique indexes (skip if they exist)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'uk_users_username') THEN
        CREATE UNIQUE INDEX uk_users_username ON users (username);
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'uk_users_email') THEN
        CREATE UNIQUE INDEX uk_users_email ON users (email);
    END IF;
END$$;

-- Ensure user_roles exists and has proper FK
CREATE TABLE IF NOT EXISTS user_roles (
  user_id UUID NOT NULL,
  role VARCHAR(255) NOT NULL,
  CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id) REFERENCES users(id)
      ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
