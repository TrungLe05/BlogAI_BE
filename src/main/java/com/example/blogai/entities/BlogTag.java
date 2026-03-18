package com.example.blogai.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "blog_tags", schema = "public", indexes = {@Index(name = "idx_blog_tags_tag",
        columnList = "tag")})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogTag implements Serializable {
    private static final long serialVersionUID = 1630993737610361159L;
    @EmbeddedId
    private BlogTagId id;

    @MapsId("blogId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @MapsId("tag")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tag", nullable = false)
    private Tag tag;


}