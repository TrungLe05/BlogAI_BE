package com.example.blogai.Service;

import com.example.blogai.dtos.ws.NotificationPayload;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AsyncConversationService {

    ConversationService conversationService;

    WebSocketService webSocketService;

    @Async
    public void createConversationAndNotify(
            UUID followerId,
            UUID followingId,
            String followerName) {
        try {
            conversationService.createIfNotExists(followerId, followingId);

            NotificationPayload chatUnlocked = NotificationPayload.builder()
                    .type("CHAT_UNLOCKED")
                    .fromUserId(followerId.toString())
                    .fromUserName(followerName)
                    .timestamp(Instant.now())
                    .build();

            webSocketService.sendToUser(followingId, "/queue/notifications", chatUnlocked);
            webSocketService.sendToUser(followerId, "/queue/notifications", chatUnlocked);

        } catch (Exception e) {
            log.error("Failed to create conversation for {} and {}: {}",
                    followerId, followingId, e.getMessage());
            log.error("Full stacktrace: ", e); // ← thêm dòng này

            NotificationPayload errorPayload = NotificationPayload.builder()
                    .type("CHAT_SETUP_FAILED")
                    .fromUserId(followerId.toString())
                    .fromUserName(followerName)
                    .timestamp(Instant.now())
                    .build();

            webSocketService.sendToUser(followerId, "/queue/notifications", errorPayload);
            webSocketService.sendToUser(followingId, "/queue/notifications", errorPayload);
        }
    }
}
