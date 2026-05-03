CREATE TABLE IF NOT EXISTS recovery_codes (
                                              id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    code_hash   VARCHAR(255) NOT NULL,  -- lưu hash, không lưu plain text
    used        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT now()
    );

CREATE INDEX idx_recovery_codes_user_id ON recovery_codes(user_id);