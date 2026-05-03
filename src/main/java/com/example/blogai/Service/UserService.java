package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.FollowRepository;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.dtos.request.RegisterRequest;
import com.example.blogai.dtos.request.ChangePasswordRequest;
import com.example.blogai.dtos.request.UpdateProfileRequest;
import com.example.blogai.dtos.response.UserResponse;
import com.example.blogai.entities.User;
import com.example.blogai.enums.ErrorCode;
import com.example.blogai.enums.UploadType;
import com.example.blogai.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {

    UserMapper userMapper;

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    CloudinaryStorageService cloudinaryStorageService;

    FollowRepository followRepository;
    public UserResponse createUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse getUser(UUID userId, UUID currentUserId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toResponse(user, followRepository.existsByFollowerIdAndFollowingId(userId, currentUserId));
    }

    public UserResponse getMe(String email){
        var user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toResponse(user);
    }

    public UserResponse updateMe(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateProfileUser(user, request);

        // Upload avatar nếu có
        try {
            if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
                // Xóa avatar cũ
                if (user.getAvatarUrl() != null) {
                    cloudinaryStorageService.delete(user.getAvatarUrl());
                }
                String avatarUrl = cloudinaryStorageService.upload(
                        request.getAvatarUrl(), user.getId().toString(), UploadType.AVATAR);
                user.setAvatarUrl(avatarUrl);
                user.setAvatarCustomized(true);
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse updatePassword(String email, ChangePasswordRequest request) {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash()))
            throw new AppException(ErrorCode.CURRENT_PASSWORD_INCORRECT);

        if(!request.getNewPassword().equalsIgnoreCase(request.getConfirmPassword()))
            throw new AppException(ErrorCode.PASSWORD_CONFIRMATION_MISMATCH);

        userMapper.updatePasswordUser(user, request);
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        return userMapper.toResponse(userRepository.save(user));
    }

    public User findById(UUID id){
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
