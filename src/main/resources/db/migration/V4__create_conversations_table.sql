CREATE TABLE conversations (
                               id         UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
                               created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE conversation_participants (
                                           conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
                                           user_id         UUID NOT NULL REFERENCES users(id)         ON DELETE CASCADE,
                                           PRIMARY KEY (conversation_id, user_id)
);

CREATE INDEX idx_conv_participants_user ON conversation_participants (user_id);