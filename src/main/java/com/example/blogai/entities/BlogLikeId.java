package com.example.blogai.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogLikeId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "blog_id", nullable = false)
    private UUID blogId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

}
