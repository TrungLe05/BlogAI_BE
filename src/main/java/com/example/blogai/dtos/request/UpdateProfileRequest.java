package com.example.blogai.dtos.request;

import com.example.blogai.customAnnotation.ValidImage;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    String fullName;

    @ValidImage(maxSizeMB = 5, allowedTypes = {"image/jpeg", "image/png", "image/webp"})
    MultipartFile avatarUrl;
}
