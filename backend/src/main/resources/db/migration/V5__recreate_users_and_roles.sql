-- 1) Drop dependent FKs that reference users.id
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'rating_user_id_fkey'
          AND table_name = 'rating'
    ) THEN
        ALTER TABLE rating DROP CONSTRAINT rating_user_id_fkey;
    END IF;
END$$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'list_entry_user_id_fkey'
          AND table_name = 'list_entry'
    ) THEN
        ALTER TABLE list_entry DROP CONSTRAINT list_entry_user_id_fkey;
    END IF;
END$$;

-- 2) Drop user_roles first (it also references users), then drop users
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;

-- 3) Recreate users (Java-generated UUIDs; no DB default)
CREATE TABLE users (
  id UUID PRIMARY KEY,
  username VARCHAR(255) NOT NULL UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);

-- 4) Recreate user_roles with FK to users
CREATE TABLE user_roles (
  user_id UUID NOT NULL,
  role VARCHAR(255) NOT NULL,
  CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id) REFERENCES users(id)
      ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);

-- 5) Recreate the dropped FKs on dependent tables
-- Adjust column names if your schema differs.

-- rating.user_id -> users.id
DO $$
BEGIN
    -- Only add if column exists (so this script works across dev db variants)
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='rating' AND column_name='user_id'
    ) THEN
        ALTER TABLE rating
        ADD CONSTRAINT rating_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE;
    END IF;
END$$;

-- list_entry.user_id -> users.id
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='list_entry' AND column_name='user_id'
    ) THEN
        ALTER TABLE list_entry
        ADD CONSTRAINT list_entry_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE;
    END IF;
END$$;
