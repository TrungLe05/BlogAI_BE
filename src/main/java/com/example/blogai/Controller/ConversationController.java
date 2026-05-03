package com.example.blogai.Controller;

import com.example.blogai.Service.ConversationService;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.ConversationResponse;
import com.example.blogai.entities.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/conversations")
@RestController
public class ConversationController {
    private final ConversationService conversationService;

    // Mở hoặc tạo conversation (lazy creation)
    @PostMapping("/open")
    public ResponseEntity<ApiResponse<ConversationResponse>> open(
            @AuthenticationPrincipal User user,
            @RequestParam UUID targetUserId) {
        return ResponseEntity.ok(ApiResponse.<ConversationResponse>builder()
                .code(200)
                .result(conversationService.openConversation(
                        user.getId(), targetUserId))
                .build());
    }

    // Danh sách conversation của user
    @GetMapping
    public ApiResponse<List<ConversationResponse>> getAll(
            @AuthenticationPrincipal User user) {
        return ApiResponse.<List<ConversationResponse>>builder()
                .code(200)
                .result(conversationService.getConversations(user.getId()))
                .build();
    }
}
