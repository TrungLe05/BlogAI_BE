package com.example.blogai.dtos.request;

import com.example.blogai.customAnnotation.ValidImage;
import jakarta.validation.constraints.NotBlank;
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
public class UpdateBlogRequest {
    @NotBlank(message = "NOT_BLANK")
    String title;
    @NotBlank(message = "NOT_BLANK")
    String content;
    @NotBlank(message = "NOT_BLANK")
    String summary;
    @ValidImage(maxSizeMB = 2, allowedTypes = {"image/jpeg", "image/png", "image/webp"})
    MultipartFile coverImageUrl;

    Set<String> tags;
}
