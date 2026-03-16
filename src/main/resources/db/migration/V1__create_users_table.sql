CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE auth_provider AS ENUM ('LOCAL', 'GOOGLE', 'GITHUB');
CREATE TYPE user_role     AS ENUM ('USER', 'ADMIN');

CREATE TABLE users (
                       id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                       email         VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255),
                       full_name     VARCHAR(100) NOT NULL,
                       avatar_url    TEXT,
                       provider      auth_provider NOT NULL DEFAULT 'LOCAL',
                       provider_id   VARCHAR(255),
                       role          user_role    NOT NULL DEFAULT 'USER',
                       created_at    TIMESTAMP    NOT NULL DEFAULT now(),
                       updated_at    TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_email    ON users (email);
CREATE INDEX idx_users_provider ON users (provider, provider_id);

ALTER TABLE users
    ADD CONSTRAINT chk_local_password
        CHECK (
            provider != 'LOCAL' OR password_hash IS NOT NULL
    );