package com.example.blogai.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @Email(message = "EMAIL_INVALID_FORMAT")
    @NotNull(message = "EMAIL_REQUIRED")
    String email;

    @NotNull(message = "PASSWORD_REQUIRED")
    String password;
}
