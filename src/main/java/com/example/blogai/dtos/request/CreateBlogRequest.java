package com.example.blogai.dtos.request;

import com.example.blogai.customAnnotation.ValidImage;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

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
    @ValidImage(maxSizeMB = 5, allowedTypes = {"image/jpeg", "image/png", "image/webp"})
    MultipartFile coverImageUrl;

    @NotNull(message = "BLOG_TAG_REQUIRED")
    Set<String> tags;

}
