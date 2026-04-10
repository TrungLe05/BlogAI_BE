ALTER TABLE messages
    ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    ADD COLUMN file_url   TEXT,
    ADD COLUMN file_name  TEXT,
    ADD COLUMN file_size  BIGINT;

ALTER TABLE messages
DROP COLUMN read_at;