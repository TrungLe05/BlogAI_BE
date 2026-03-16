CREATE TABLE refresh_tokens (
                                id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_id    UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                token      VARCHAR(512) NOT NULL UNIQUE,
                                expires_at TIMESTAMP    NOT NULL,
                                revoked    BOOLEAN      NOT NULL DEFAULT false,
                                created_at TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_token   ON refresh_tokens (token);
CREATE INDEX idx_refresh_tokens_user    ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_expires ON refresh_tokens (expires_at)
    WHERE revoked = false;