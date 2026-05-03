package com.example.blogai.Repository;

import com.example.blogai.entities.Blog;
import com.example.blogai.entities.BlogTag;
import com.example.blogai.entities.User;
import com.example.blogai.enums.BlogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface BlogsRepository extends JpaRepository<Blog, UUID> {
    List<Blog> findByAuthor(User user);
    List<Blog> findByAuthorIdAndStatus(UUID authorId, BlogStatus status);
    @Query("SELECT DISTINCT b FROM Blog b JOIN b.blogTags bt WHERE bt.tag.tag IN :tags AND b.id <> :currentBlogId")
    List<Blog> findBlogsByTagList(@Param("tags") Set<String> tags, @Param("currentBlogId") UUID currentBlogId);

    @Modifying
    @Query("UPDATE Blog b SET b.viewCount = b.viewCount + 1 WHERE b.id = :blogId")
    void incrementViewCount(@Param("blogId") UUID blogId);

    @Query("SELECT b FROM Blog b WHERE b.status = 'PUBLISHED' ORDER BY b.viewCount DESC")
    List<Blog> getFourBlogMostViewer();



}
