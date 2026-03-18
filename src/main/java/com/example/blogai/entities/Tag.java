package com.example.blogai.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tags", schema = "public")
public class Tag {
    @Id
    @Size(max = 50)
    @Column(name = "tag", nullable = false, length = 50)
    private String tag;

    @Size(max = 50)
    @NotNull
    @Column(name = "group_name", nullable = false, length = 50)
    private String groupName;


}