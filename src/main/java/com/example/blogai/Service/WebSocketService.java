package com.example.blogai.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketService {
    SimpMessagingTemplate messageTemplate;

    public void sendToUser(UUID userId, String destination, Object payload) {
        messageTemplate.convertAndSendToUser(
                userId.toString(),      // user identifier
                destination,            // ví dụ: /queue/notifications
                payload
        );
    }
}
