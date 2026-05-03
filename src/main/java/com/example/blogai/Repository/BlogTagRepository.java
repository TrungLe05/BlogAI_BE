package com.example.blogai.Repository;

import com.example.blogai.entities.BlogTag;
import com.example.blogai.entities.BlogTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlogTagRepository extends JpaRepository<BlogTag, BlogTagId> {
    List<BlogTag> findAllByIdBlogId(UUID blogId);
    void deleteAllByIdBlogId(UUID blogId);

    // Top tags by view count
    @Query("""
        SELECT bt.tag.tag, SUM(b.viewCount) as totalViews
        FROM BlogTag bt
        JOIN bt.blog b
        WHERE b.status = 'PUBLISHED'
        GROUP BY bt.tag.tag
        ORDER BY totalViews DESC
        LIMIT :limit
        """)
    List<Object[]> findTopTagsByViewCount(@Param("limit") int limit);

    // Top tags by like count
    @Query("""
        SELECT bt.tag.tag, COUNT(bl) as totalLikes
        FROM BlogTag bt
        JOIN bt.blog b
        LEFT JOIN BlogLike bl ON bl.id.blogId = b.id
        WHERE b.status = 'PUBLISHED'
        GROUP BY bt.tag.tag
        ORDER BY totalLikes DESC
        LIMIT :limit
        """)
    List<Object[]> findTopTagsByLikeCount(@Param("limit") int limit);

    @Query(value = """
        SELECT t.group_name, COUNT(DISTINCT bt.blog_id) as postCount
        FROM public.blog_tags bt
        JOIN public.tags t ON t.tag = bt.tag
        JOIN public.blogs b ON b.id = bt.blog_id
        WHERE b.status = 'PUBLISHED'
        GROUP BY t.group_name
        ORDER BY postCount DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopGroupsByPostCount(@Param("limit") int limit);
}
