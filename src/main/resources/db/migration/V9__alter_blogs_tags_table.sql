ALTER TABLE blog_tags
    ADD CONSTRAINT fk_blog_tags_tag
        FOREIGN KEY (tag) REFERENCES tags(tag) ON DELETE CASCADE;