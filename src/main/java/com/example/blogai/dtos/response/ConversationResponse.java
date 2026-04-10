package com.example.blogai.dtos.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    // thông tin conversation
    String id;
    UserResponse otherUser;
    Instant createdAt;

    //thông tin message để trực quan hóa conversation
    String lastMessage;
    Instant lastMessageAt;
    int unreadCount;

}
