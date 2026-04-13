package com.example.blogai.dtos.ws;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationPayload {
     String type;       // FOLLOW, MESSAGE, TYPING, SEEN
     String fromUserId;
     String fromUserName;
     String fromAvatarUrl;
     Object data;       // payload tuỳ theo type
     Instant timestamp;
     String conversationId; // ← thêm field này để frontend có thể navigate

}
