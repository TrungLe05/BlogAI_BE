package com.example.blogai.Controller;

import com.example.blogai.Service.MessageService;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.MessageResponse;
import com.example.blogai.dtos.ws.SendMessageRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/messages")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MessageController {

    MessageService messageService;

    @GetMapping("{conversationId}")
    public ApiResponse<List<MessageResponse>> getMessages(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID conversationId){
        return ApiResponse.<List<MessageResponse>>builder()
                .result(messageService.getMessages(
                        conversationId,
                        UUID.fromString(jwt.getSubject()))
                )
                .build();
    }

    @PostMapping("/{conversationId}")
    public ApiResponse<MessageResponse> send(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID conversationId,
            @RequestBody SendMessageRequest request
    ){
        return ApiResponse.<MessageResponse>builder()
                .result(messageService.sendMessage(
                        UUID.fromString(jwt.getSubject()),
                        conversationId,
                        request.getContent(),
                        request.getType()
                        ))
                .build();
    }

    @PatchMapping("/{conversationId}/read")
    public ApiResponse<Void> markAsRead(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID conversationId) {
        messageService.markAsRead(conversationId, UUID.fromString(jwt.getSubject()));
        return ApiResponse.<Void>builder().message("Marked as read").build();
    }
}
