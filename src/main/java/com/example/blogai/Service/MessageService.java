package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.ConversationRepository;
import com.example.blogai.Repository.MessageRepository;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.dtos.response.MessageResponse;
import com.example.blogai.dtos.ws.ChatMessagePayload;
import com.example.blogai.dtos.ws.NotificationPayload;
import com.example.blogai.entities.Conversation;
import com.example.blogai.entities.Message;
import com.example.blogai.entities.User;
import com.example.blogai.enums.ErrorCode;
import com.example.blogai.enums.MessageType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@RequiredArgsConstructor
public class MessageService {

    MessageRepository messageRepository;
    UserRepository userRepository;
    ConversationRepository conversationRepository;
    WebSocketService webSocketService;

    @Transactional
    public MessageResponse sendMessage(UUID senderId, UUID conversationId, String content, String type) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        boolean isParticipant = conv.getParticipantA().getId().equals(senderId)
                || conv.getParticipantB().getId().equals(senderId);
        if (!isParticipant) throw new AppException(ErrorCode.FORBIDDEN);

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            Message msg = new Message();
        try{
            msg.setConversation(conv);
            msg.setSender(sender);
            msg.setContent(content);
            msg.setType(MessageType.valueOf(type != null ? type : "TEXT"));
            messageRepository.save(msg);
            log.info("Message data: {}", msg.toString());
        }catch(TransactionSystemException e){
            log.info("Debug: {}", e.getMessage());
        }

        UUID receiverId = conv.getParticipantA().getId().equals(senderId)
                ? conv.getParticipantB().getId()
                : conv.getParticipantA().getId();

        // Push realtime tới receiver
        ChatMessagePayload payload = ChatMessagePayload.builder()
                .type("NEW_MESSAGE")
                .conversationId(conv.getId().toString())
                .messageId(msg.getId().toString())
                .senderId(senderId.toString())
                .senderName(sender.getFullName())
                .senderAvatar(sender.getAvatarUrl())
                .content(content)
                .messageType(msg.getType().name())
                .createdAt(msg.getCreatedAt() != null
                        ? msg.getCreatedAt().toString()
                        : Instant.now().toString())
                .build();

        webSocketService.sendToUser(receiverId, "/queue/messages", payload);

        webSocketService.sendToUser(receiverId, "/queue/notifications",
                NotificationPayload.builder()
                        .type("NEW_MESSAGE")
                        .fromUserId(senderId.toString())
                        .fromUserName(sender.getFullName())
                        .fromAvatarUrl(sender.getAvatarUrl())
                        .conversationId(conv.getId().toString())
                        .timestamp(Instant.now())
                        .build()
        );
        return toResponse(msg, sender);
    }

    @Transactional
    public void markAsRead(UUID conversationId, UUID userId) {
        messageRepository.markAllAsRead(conversationId, userId);

        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        UUID otherUserId = conv.getParticipantA().getId().equals(userId)
                ? conv.getParticipantB().getId()
                : conv.getParticipantA().getId();

        webSocketService.sendToUser(otherUserId, "/queue/messages",
                ChatMessagePayload.builder()
                        .type("READ")
                        .conversationId(conversationId.toString())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(UUID conversationId, UUID userId) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        boolean isParticipant = conv.getParticipantA().getId().equals(userId)
                || conv.getParticipantB().getId().equals(userId);
        if (!isParticipant) throw new AppException(ErrorCode.FORBIDDEN);

        return messageRepository.findByConversationId(conversationId)
                .stream()
                .map(m -> toResponse(m, m.getSender()))
                .toList();
    }

    private MessageResponse toResponse(Message m, User sender) {
        return MessageResponse.builder()
                .id(m.getId().toString())
                .conversationId(m.getConversation().getId().toString())
                .senderId(sender.getId().toString())
                .senderName(sender.getFullName())
                .senderAvatar(sender.getAvatarUrl())
                .content(m.getContent())
                .type(m.getType().name())
                .isRead(m.isRead())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
