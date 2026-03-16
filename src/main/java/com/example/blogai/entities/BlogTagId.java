package com.example.blogai.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class BlogTagId implements Serializable {
    private static final long serialVersionUID = 74784971521108657L;
    @NotNull
    @Column(name = "blog_id", nullable = false)
    private UUID blogId;

    @Size(max = 50)
    @NotNull
    @Column(name = "tag", nullable = false, length = 50)
    private String tag;


}