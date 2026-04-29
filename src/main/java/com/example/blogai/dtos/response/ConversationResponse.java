package com.example.blogai.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ConversationResponse {
    // thông tin conversation
    String id;
    UserResponse otherUser;
    Instant createdAt;

    //thông tin message để trực quan hóa conversation
    String lastMessage;
    Instant lastMessageAt;
    int unreadCount;
    String lastMessageSenderId;

}
