package com.example.blogai.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WebSocketService {
    SimpMessagingTemplate messageTemplate;
    public void sendToUser(UUID userId, String destination, Object payload) {
        messageTemplate.convertAndSendToUser(
                userId.toString(),
                destination,
                payload
        );
    }
}