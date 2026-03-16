package com.example.blogai.Controller;

import com.example.blogai.Service.AuthService;
import com.example.blogai.Service.UserService;
import com.example.blogai.dtos.request.*;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.AuthResponse;
import com.example.blogai.dtos.response.IntrospectResponse;
import com.example.blogai.dtos.response.UserResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    AuthService authService;

    UserService userService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request){
        return ApiResponse.<AuthResponse>builder()
                .result(authService.login(request))
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectTokenRequest request){
        return ApiResponse.<IntrospectResponse>builder()
                .result(authService.introspect(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        authService.logout(jwt.getId(), jwt.getExpiresAt());
        return ApiResponse.<Void>builder()
                .result(null)
                .message("logout successfully")
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        return ApiResponse.<AuthResponse>builder()
                .result(authService.refreshToken(request))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(@AuthenticationPrincipal Jwt jwt){

        return ApiResponse.<UserResponse>builder()
                .result(userService.getMe(jwt.getClaim("email")))
                .build();
    }
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> updateMe(@AuthenticationPrincipal Jwt jwt, @ModelAttribute @Valid UpdateProfileRequest request) throws IOException {

        return ApiResponse.<UserResponse>builder()
                .result(userService.updateMe(jwt.getClaim("email"), request))
                .build();
    }

    @PutMapping("/me/change-password")
    public ApiResponse<UserResponse> updatePassword(@AuthenticationPrincipal Jwt jwt,@RequestBody @Valid ChangePasswordRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updatePassword(jwt.getClaim("email"),request))
                .build();
    }
}
