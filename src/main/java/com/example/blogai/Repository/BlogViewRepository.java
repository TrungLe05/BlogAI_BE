package com.example.blogai.Repository;

import com.example.blogai.entities.BlogView;
import com.example.blogai.entities.BlogViewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BlogViewRepository extends JpaRepository<BlogView, BlogViewId> {
    @Modifying
    @Query(value = """
            INSERT INTO blog_views (blog_id, user_id, viewed_at)
            VALUES (:blogId, :userId, now())
            ON CONFLICT (blog_id, user_id) DO NOTHING
            """, nativeQuery = true)
    int recordViewIfNotExists(@Param("blogId") UUID blogId, @Param("userId") UUID userId);
}
