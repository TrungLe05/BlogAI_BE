package com.example.blogai.Validator;

import com.example.blogai.customAnnotation.ValidImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    private long maxSizeBytes;
    private String[] allowedTypes;

    @Override
    public void initialize(ValidImage annotation) {
        this.maxSizeBytes = annotation.maxSizeMB() * 1024 * 1024;
        this.allowedTypes = annotation.allowedTypes();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) return true; // để @NotNull xử lý null

        // Kiểm tra content type
        String contentType = file.getContentType();
        boolean validType = Arrays.asList(allowedTypes).contains(contentType);
        if (!validType) {
            buildMessage(context, "Only " + Arrays.toString(allowedTypes) + " are allowed");
            return false;
        }

        // Kiểm tra kích thước
        if (file.getSize() > maxSizeBytes) {
            buildMessage(context, "File size must not exceed " + (maxSizeBytes / 1024 / 1024) + "MB");
            return false;
        }

        return true;
    }

    private void buildMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
