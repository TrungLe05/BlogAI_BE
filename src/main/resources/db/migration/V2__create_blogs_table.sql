CREATE TYPE blog_status AS ENUM ('DRAFT', 'PUBLISHED');

CREATE TABLE blogs (
                       id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                       title           VARCHAR(255),
                       content         TEXT        NOT NULL,
                       summary         TEXT,
                       cover_image_url TEXT,
                       author_id       UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       status          blog_status NOT NULL DEFAULT 'DRAFT',
                       view_count      INTEGER     NOT NULL DEFAULT 0,
                       created_at      TIMESTAMP   NOT NULL DEFAULT now(),
                       updated_at      TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE INDEX idx_blogs_status_created ON blogs (status, created_at DESC);
CREATE INDEX idx_blogs_author         ON blogs (author_id);