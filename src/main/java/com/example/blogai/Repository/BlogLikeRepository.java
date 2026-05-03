package com.example.blogai.Repository;

import com.example.blogai.entities.BlogLike;
import com.example.blogai.entities.BlogLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlogLikeRepository extends JpaRepository<BlogLike, BlogLikeId> {
    long countByIdBlogId(UUID blogId);

    boolean existsByIdBlogIdAndIdUserId(UUID blogId, UUID userId);

    @Modifying
    @Query("DELETE FROM BlogLike bl WHERE bl.id.blogId = :blogId AND bl.id.userId = :userId")
    int deleteByIdBlogIdAndIdUserId(@Param("blogId") UUID blogId, @Param("userId") UUID userId);

    @Modifying
    @Query(value = """
            INSERT INTO blog_likes (blog_id, user_id, created_at)
            VALUES (:blogId, :userId, now())
            ON CONFLICT (blog_id, user_id) DO NOTHING
            """, nativeQuery = true)
    void likeIfNotExists(@Param("blogId") UUID blogId, @Param("userId") UUID userId);
}
