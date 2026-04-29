package com.example.blogai.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogResponse {
    String blogId;
    String title;
    String content;
    String summary;
    String coverImageUrl;
    UserResponse author;
    String blogStatus;
    Integer viewCount;
    List<String> tags;
    String createdAt;
    long likeCount;
    boolean likedByCurrentUser;
}
