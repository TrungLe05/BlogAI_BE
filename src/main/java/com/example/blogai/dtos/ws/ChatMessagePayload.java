package com.example.blogai.dtos.ws;

import lombok.*;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ChatMessagePayload {
    String type; // NEW_MESSAGE, TYPING, STOP_TYPING, READ
    String conversationId;
    String messageId;
    String senderId;
    String senderName;
    String senderAvatar;
    String content;
    String messageType; // TEXT, IMAGE, FILE
    String createdAt;
}
