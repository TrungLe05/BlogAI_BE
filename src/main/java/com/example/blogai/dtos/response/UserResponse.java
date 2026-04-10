package com.example.blogai.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    String id;
    String email;
    String fullName;
    String avatarUrl;
    String role;
    boolean isFollowing;
}
