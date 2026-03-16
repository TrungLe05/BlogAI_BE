CREATE TABLE messages (
                          id              UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
                          conversation_id UUID      NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
                          sender_id       UUID      NOT NULL REFERENCES users(id)         ON DELETE CASCADE,
                          content         TEXT      NOT NULL,
                          read_at         TIMESTAMP,
                          created_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_messages_conv_created ON messages (conversation_id, created_at DESC);
CREATE INDEX idx_messages_sender       ON messages (sender_id);