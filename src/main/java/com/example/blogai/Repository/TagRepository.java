package com.example.blogai.Repository;

import com.example.blogai.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {
    Set<Tag> findAllByTagIn(Set<String> tags);
}
