package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.FollowRepository;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.dtos.response.UserResponse;
import com.example.blogai.dtos.ws.NotificationPayload;
import com.example.blogai.entities.Follow;
import com.example.blogai.entities.User;
import com.example.blogai.enums.ErrorCode;
import com.example.blogai.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FollowService {
      FollowRepository followRepository;
      UserRepository userRepository;
      AsyncConversationService asyncConversationService;
      WebSocketService webSocketService;
      UserMapper userMapper;

    @Transactional
    public void follow(UUID followerId, UUID followingId) {
        if (followerId.equals(followingId))
            throw new AppException(ErrorCode.CANNOT_FOLLOW_YOURSELF);

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId))
            throw new AppException(ErrorCode.ALREADY_FOLLOWED);

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        try{
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            followRepository.saveAndFlush(follow);
        }catch(DataIntegrityViolationException e){
            throw new AppException(ErrorCode.ALREADY_FOLLOWED);
        }

        // Push notification tới B
        webSocketService.sendToUser(
                followingId,
                "/queue/notifications",
                NotificationPayload.builder()
                        .type("FOLLOW")
                        .fromUserId(followerId.toString())
                        .fromUserName(follower.getFullName())
                        .fromAvatarUrl(follower.getAvatarUrl())
                        .timestamp(Instant.now())
                        .build()
        );

        // Kiểm tra mutual follow → tạo conversation
        boolean isMutual = followRepository
                .existsByFollowerIdAndFollowingId(followingId, followerId);
        String followerName = follower.getFullName();

        if (isMutual) {
            // Chỉ spawn thread B SAU KHI transaction A commit xong
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            asyncConversationService
                                    .createConversationAndNotify(followerId, followingId, followerName);
                        }
                    }
            );
        }
    }

    @Transactional
    public void unfollow(UUID followerId, UUID followingId) {
        Follow follow = followRepository
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOLLOWED));
        followRepository.delete(follow);
        followRepository.flush(); // đảm bảo DELETE committed trước khi return
    }

    public List<UserResponse> getFollowers(UUID userId) {
        return followRepository.findByFollowingId(userId)
                .stream()
                .map(f -> userMapper.toResponse(f.getFollower()))
                .toList();
    }

    public List<UserResponse> getFollowing(UUID userId) {
        return followRepository.findByFollowerId(userId)
                .stream()
                .map(f -> userMapper.toResponse(f.getFollowing()))
                .toList();
    }
}
