-- Xóa foreign key constraint trước
ALTER TABLE blog_tags DROP CONSTRAINT fk_blog_tags_tag;

-- Sau đó xóa bảng tags
DROP TABLE IF EXISTS tags;