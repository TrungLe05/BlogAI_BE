package com.example.blogai.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
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
