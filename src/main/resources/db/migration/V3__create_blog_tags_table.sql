CREATE TABLE blog_tags (
                           blog_id UUID        NOT NULL REFERENCES blogs(id) ON DELETE CASCADE,
                           tag     VARCHAR(50) NOT NULL,
                           PRIMARY KEY (blog_id, tag)
);

CREATE INDEX idx_blog_tags_tag ON blog_tags (tag);