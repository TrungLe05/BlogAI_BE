package com.example.blogai.Controller;

import com.example.blogai.Service.UserService;
import com.example.blogai.Service.WebSocketService;
import com.example.blogai.dtos.ws.ChatMessagePayload;
import com.example.blogai.dtos.ws.TypingPayload;
import com.example.blogai.entities.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TypingController {
    WebSocketService webSocketService;
    UserService userService;
    @MessageMapping("/typing")
    public void handleTyping(
            @Payload TypingPayload payload,
            @AuthenticationPrincipal UsernamePasswordAuthenticationToken auth) {

        String senderId = (String) auth.getPrincipal();
        User sender = userService.findById(UUID.fromString(senderId));

        webSocketService.sendToUser(
                UUID.fromString(payload.getReceiverId()),
                "/queue/messages",
                ChatMessagePayload.builder()
                        .type(payload.isTyping() ? "TYPING" : "STOP_TYPING")
                        .conversationId(payload.getConversationId())
                        .senderId(senderId)
                        .senderName(sender.getFullName())
                        .build()
        );
    }
}
