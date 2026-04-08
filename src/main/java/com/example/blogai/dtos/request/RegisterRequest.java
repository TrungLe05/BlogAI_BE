package com.example.blogai.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
     @Email(message = "EMAIL_INVALID_FORMAT")
     @NotNull(message = "EMAIL_REQUIRED")
     @Size(min = 5, message = "EMAIL_INVALID_FORMAT")
     String email;

     @NotNull(message = "PASSWORD_REQUIRED")
     @Size(min = 8, message = "PASSWORD_TOO_SHORT")
     String passwordHash;

     String fullName;
}
