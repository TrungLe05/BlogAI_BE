package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.*;
import com.example.blogai.dtos.response.ConversationResponse;
import com.example.blogai.entities.*;
import com.example.blogai.enums.ErrorCode;
import com.example.blogai.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConversationService {

    ConversationRepository conversationRepository;
    ConversationParticipantRepository conversationParticipantRepository;
    MessageRepository messageRepository;
    FollowRepository followRepository;
    UserMapper userMapper;
    UserRepository userRepository;

    public ConversationResponse openConversation(UUID currentUserId, UUID targetUserId) {
        boolean aFollowB = followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId);
        boolean bFollowA = followRepository.existsByFollowerIdAndFollowingId(targetUserId, currentUserId);

        if (!aFollowB || !bFollowA) {
            throw new AppException(ErrorCode.CHAT_NOT_ALLOWED);
        }

        return conversationRepository
                .findByParticipants(currentUserId, targetUserId)
                .map(conv -> toResponse(conv, currentUserId))
                .orElseGet(() -> toResponse(createConversation(currentUserId, targetUserId), currentUserId));
    }
    public Conversation createIfNotExists(UUID userA, UUID userB) {
        return conversationRepository
                .findByParticipants(userA, userB)
                .orElseGet(() -> createConversation(userA, userB));
    }
    private Conversation createConversation(UUID userA, UUID userB) {
        User a = userRepository.getReferenceById(userA);
        User b = userRepository.getReferenceById(userB);

        Conversation conv = new Conversation();
        conv.setParticipantA(a);
        conv.setParticipantB(b);
        conv = conversationRepository.save(conv);

        // Tạo participants
        saveParticipant(conv, a);
        saveParticipant(conv, b);

        return conv;
    }

    private void saveParticipant(Conversation conv, User user) {
        ConversationParticipantId pid = new ConversationParticipantId();
        pid.setConversationId(conv.getId());
        pid.setUserId(user.getId());

        ConversationParticipant p = new ConversationParticipant();
        p.setId(pid);
        p.setConversation(conv);
        p.setUser(user);
        conversationParticipantRepository.save(p);
    }

    public List<ConversationResponse> getConversations(UUID userId) {
        return conversationRepository.findByParticipantId(userId)
                .stream()
                .map(conv -> toResponse(conv,userId))
                .toList();
    }

    private ConversationResponse toResponse(Conversation conv, UUID currentUserId) {
        User other = conv.getParticipantA().getId().equals(currentUserId)
                ? conv.getParticipantB()
                : conv.getParticipantA();

        Message lastMsg = messageRepository.findLastMessage(conv.getId()).orElse(null);
        int unreadCount = messageRepository.countUnread(conv.getId(), currentUserId);

        return ConversationResponse.builder()
                .id(conv.getId().toString())
                .otherUser(userMapper.toResponse(other))
                .createdAt(conv.getCreatedAt())
                .lastMessage(lastMsg != null ? lastMsg.getContent() : null)
                .lastMessageAt(lastMsg != null ? lastMsg.getCreatedAt() : null)
                .unreadCount(unreadCount)
                .build();
    }
}
