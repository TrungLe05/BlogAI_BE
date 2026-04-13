package com.example.blogai.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    String id;
    String conversationId;
    String senderId;
    String senderName;
    String senderAvatar;
    String content;
    String type;
    boolean isRead;
    Instant createdAt;
}
