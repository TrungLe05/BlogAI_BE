package com.example.blogai.customAnnotation;

import com.example.blogai.Validator.ImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = ImageValidator.class
)
public @interface ValidImage {
    String message() default "Invalid image file";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    long maxSizeMB() default 5;
    String[] allowedTypes() default {"image/jpeg", "image/png", "image/webp"};
}
