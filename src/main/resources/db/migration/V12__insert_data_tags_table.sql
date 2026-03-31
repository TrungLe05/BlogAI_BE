-- V12__seed_tags_and_constraint.sql

-- Insert tags
INSERT INTO tags (tag, group_name) VALUES
                                       ('Java', 'Language'),
                                       ('Python', 'Language'),
                                       ('JavaScript', 'Language'),
                                       ('TypeScript', 'Language'),
                                       ('Go', 'Language'),
                                       ('Rust', 'Language'),
                                       ('C++', 'Language'),
                                       ('Swift', 'Language'),
                                       ('React', 'Frontend'),
                                       ('Vue', 'Frontend'),
                                       ('Angular', 'Frontend'),
                                       ('Tailwind', 'Frontend'),
                                       ('NextJS', 'Frontend'),
                                       ('Spring Boot', 'Backend'),
                                       ('NodeJS', 'Backend'),
                                       ('NestJS', 'Backend'),
                                       ('Django', 'Backend'),
                                       ('FastAPI', 'Backend'),
                                       ('Docker', 'DevOps'),
                                       ('Kubernetes', 'DevOps'),
                                       ('AWS', 'DevOps'),
                                       ('CI/CD', 'DevOps'),
                                       ('PostgreSQL', 'Database'),
                                       ('MySQL', 'Database'),
                                       ('MongoDB', 'Database'),
                                       ('Redis', 'Database');

-- Xóa các tag trong blog_tags không có trong bảng tags
DELETE FROM blog_tags
WHERE tag NOT IN (SELECT tag FROM tags);

-- Sau đó mới add constraint
ALTER TABLE blog_tags
    ADD CONSTRAINT fk_blog_tags_tag
        FOREIGN KEY (tag) REFERENCES tags(tag) ON DELETE CASCADE;