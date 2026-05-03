package com.example.blogai.dtos.ws;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TypingPayload {
     String conversationId;
     String receiverId;
     boolean typing;
}
