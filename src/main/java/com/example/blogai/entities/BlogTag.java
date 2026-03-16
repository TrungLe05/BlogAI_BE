package com.example.blogai.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "blog_tags", schema = "public", indexes = {@Index(name = "idx_blog_tags_tag",
        columnList = "tag")})
public class BlogTag {
    @EmbeddedId
    private BlogTagId id;

    @MapsId("blogId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;


}