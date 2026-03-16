package com.example.blogai.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBlogRequest {
    String title;
    @NotNull(message = "BLOG_CONTENT_REQUIRED")
    String content;
    String summary;
    @NotNull(message = "BLOG_COVER_IMAGE_REQUIRED")

    MultipartFile coverImageUrl;
    @NotNull(message = "BLOG_AUTHOR_REQUIRED")
    String author; // user_id


}
