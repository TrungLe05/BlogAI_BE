package com.example.blogai.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogResponse {
    String blogId;
    String title;
    String content;
    String summary;
    String coverImageUrl;
    UserResponse author;
    String blogStatus;
    Integer viewCount;
}
