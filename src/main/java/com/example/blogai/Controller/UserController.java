package com.example.blogai.Controller;

import com.example.blogai.Service.UserService;
import com.example.blogai.dtos.request.RegisterRequest;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.UserResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> createUser (@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();

    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable UUID userId, @AuthenticationPrincipal Jwt jwt) throws Exception {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId, UUID.fromString(jwt.getSubject())))
                .build();
    }


}
