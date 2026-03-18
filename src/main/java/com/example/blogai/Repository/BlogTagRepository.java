package com.example.blogai.Repository;

import com.example.blogai.entities.BlogTag;
import com.example.blogai.entities.BlogTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlogTagRepository extends JpaRepository<BlogTag, BlogTagId> {
    List<BlogTag> findAllByIdBlogId(UUID blogId);
    void deleteAllByIdBlogId(UUID blogId);
}
