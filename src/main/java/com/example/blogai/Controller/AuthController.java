package com.example.blogai.Controller;

import com.example.blogai.Service.AuthService;
import com.example.blogai.dtos.request.IntrospectTokenRequest;
import com.example.blogai.dtos.request.LoginRequest;
import com.example.blogai.dtos.request.RefreshTokenRequest;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.AuthResponse;
import com.example.blogai.dtos.response.IntrospectResponse;
import com.example.blogai.dtos.response.UserResponse;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    AuthService authService;

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
                .result(authService.getMe(jwt.getClaim("email")))
                .build();
    }
}
