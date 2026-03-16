package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.InvalidatedTokenRepository;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.dtos.request.IntrospectTokenRequest;
import com.example.blogai.dtos.request.LoginRequest;
import com.example.blogai.dtos.request.RefreshTokenRequest;
import com.example.blogai.dtos.response.AuthResponse;
import com.example.blogai.dtos.response.IntrospectResponse;
import com.example.blogai.dtos.response.UserResponse;
import com.example.blogai.entities.InvalidatedToken;
import com.example.blogai.enums.ErrorCode;
import com.example.blogai.mapper.UserMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor

public class AuthService {

    JwtService jwtService;

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper;

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    public AuthResponse login(LoginRequest request){
        var user = userRepository.findByEmail(
                request.getEmail()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }


    public void logout(String jwtId, Instant expirationTime){

        invalidatedTokenRepository.save(
                InvalidatedToken.builder()
                        .id(jwtId)
                        .expiryTime(expirationTime)
                        .build()
        );
    }

    public AuthResponse refreshToken(RefreshTokenRequest refreshToken){
        JWTClaimsSet jwtClaimsSet = jwtService.verifyToken(refreshToken.getRefreshToken());

        if(!"refresh".equalsIgnoreCase(jwtClaimsSet.getClaim("tokenType").toString())){
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        invalidatedTokenRepository.save(
          InvalidatedToken.builder()
                  .id(jwtClaimsSet.getJWTID())
                  .expiryTime(jwtClaimsSet.getExpirationTime().toInstant())
                  .build()
        );

        var user = userRepository.findById(
                UUID.fromString(jwtClaimsSet.getSubject()))
                .orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_EXISTED)
                );

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    public IntrospectResponse introspect (IntrospectTokenRequest request){
        try {
            JWTClaimsSet claimsSet = jwtService.verifyToken(request.getToken());

            return IntrospectResponse.builder()
                    .email(claimsSet.getClaim("email").toString())
                    .valid(true)
                    .role(claimsSet.getClaim("role").toString())
                    .build();
        } catch (Exception e) {
            return IntrospectResponse.builder().valid(false).build();
        }

    }



}
