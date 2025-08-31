-- Create users table
CREATE TABLE IF NOT EXISTS users (
id UUID PRIMARY KEY,
username VARCHAR(255) NOT NULL UNIQUE,
email VARCHAR(255) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL
);

-- Create user_roles table for @ElementCollection(fetch = EAGER)
CREATE TABLE IF NOT EXISTS user_roles (
user_id UUID NOT NULL,
role VARCHAR(255) NOT NULL,
CONSTRAINT fk_user_roles_user
FOREIGN KEY (user_id) REFERENCES users(id)
ON DELETE CASCADE
);

-- Optional index for faster lookups
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
