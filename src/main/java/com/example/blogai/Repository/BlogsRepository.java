package com.example.blogai.Repository;

import com.example.blogai.entities.Blog;
import com.example.blogai.entities.BlogTag;
import com.example.blogai.entities.User;
import com.example.blogai.enums.BlogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public interface BlogsRepository extends JpaRepository<Blog, UUID> {
    List<Blog> findByAuthor(User user);
    List<Blog> findByAuthorIdAndStatus(UUID authorId, BlogStatus status);

}
