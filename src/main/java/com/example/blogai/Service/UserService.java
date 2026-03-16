package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.dtos.request.RegisterRequest;
import com.example.blogai.dtos.response.UserResponse;
import com.example.blogai.entities.User;
import com.example.blogai.enums.ErrorCode;
import com.example.blogai.mapper.UserMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {

    UserMapper userMapper;

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;
    public UserResponse createUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse getUser(String email) throws Exception {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not existed"));

        return userMapper.toResponse(user);
    }
}
