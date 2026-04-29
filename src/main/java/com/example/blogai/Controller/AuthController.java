package com.example.blogai.Controller;

import com.example.blogai.Service.AuthService;
import com.example.blogai.Service.UserService;
import com.example.blogai.dtos.request.*;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.AuthResponse;
import com.example.blogai.dtos.response.IntrospectResponse;
import com.example.blogai.dtos.response.UserResponse;
import com.example.blogai.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    AuthService authService;

    UserService userService;

    @NonFinal
    String emailClaim = "email";

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody RegisterRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request){
        return ApiResponse.<AuthResponse>builder()
                .result(authService.login(request))
                .build();
    }
    @PostMapping("/login/verify-otp")
    public ApiResponse<AuthResponse> verifyLoginOtp(
            @RequestBody @Valid VerifyLoginOtpRequest req
    ) {
        return ApiResponse.<AuthResponse>builder()
                .result(authService.verifyLoginOtp(req.getTempToken(), req.getOtpCode()))
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectTokenRequest request){
        return ApiResponse.<IntrospectResponse>builder()
                .result(authService.introspect(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        authService.logout(request);
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
    public ApiResponse<UserResponse> getMe(@AuthenticationPrincipal User user){

        return ApiResponse.<UserResponse>builder()
                .result(userService.getMe(user.getEmail()))
                .build();
    }
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> updateMe(@AuthenticationPrincipal User user, @ModelAttribute @Valid UpdateProfileRequest request){

        return ApiResponse.<UserResponse>builder()
                .result(userService.updateMe(user.getEmail(), request))
                .build();
    }

    @PutMapping("/me/change-password")
    public ApiResponse<UserResponse> updatePassword(@AuthenticationPrincipal User user,@RequestBody @Valid ChangePasswordRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updatePassword(user.getEmail(),request))
                .build();
    }
}
