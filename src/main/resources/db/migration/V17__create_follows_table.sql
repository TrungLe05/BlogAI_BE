CREATE TABLE follows (
                         id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         follower_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         following_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         created_at   TIMESTAMP DEFAULT now(),
                         CONSTRAINT uq_follow UNIQUE (follower_id, following_id),
                         CONSTRAINT chk_no_self_follow CHECK (follower_id != following_id)
    );

CREATE INDEX idx_follows_follower  ON follows(follower_id);
CREATE INDEX idx_follows_following ON follows(following_id);