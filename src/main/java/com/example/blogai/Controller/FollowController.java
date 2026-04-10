package com.example.blogai.Controller;

import com.example.blogai.Service.FollowService;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.UserResponse;
import com.example.blogai.entities.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/follows")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FollowController {

    FollowService followService;

    @NonFinal
    String emailClaim = "email";

    @PostMapping("/{targetUserId}")
    public ApiResponse<Void> follow(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID targetUserId) {
        followService.follow(UUID.fromString(jwt.getSubject()), targetUserId);
        return ApiResponse.<Void>builder()
                .message("Followed successfully")
                .build();
    }

    @DeleteMapping("/{targetUserId}")
    public ApiResponse<Void> unfollow(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID targetUserId) {
        followService.unfollow(UUID.fromString(jwt.getSubject()), targetUserId);
        return ApiResponse.<Void>builder()
                .message("Unfollowed successfully")
                .build();
    }

    @GetMapping("/followers")
    public ApiResponse<List<UserResponse>> getFollowers(
            @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<List<UserResponse>>builder()
                .result(followService.getFollowers(UUID.fromString(jwt.getSubject())))
                .build();
    }

    @GetMapping("/following")
    public ApiResponse<List<UserResponse>> getFollowing(
            @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<List<UserResponse>>builder()
                .result(followService.getFollowing(UUID.fromString(jwt.getSubject())))
                .build();
    }
}