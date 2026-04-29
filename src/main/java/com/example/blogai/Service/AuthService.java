package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.InvalidatedTokenRepository;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.dtos.request.IntrospectTokenRequest;
import com.example.blogai.dtos.request.LoginRequest;
import com.example.blogai.dtos.request.RefreshTokenRequest;
import com.example.blogai.dtos.request.RegisterRequest;
import com.example.blogai.dtos.response.AuthResponse;
import com.example.blogai.dtos.response.IntrospectResponse;
import com.example.blogai.dtos.response.UserResponse;
import com.example.blogai.entities.InvalidatedToken;
import com.example.blogai.entities.User;
import com.example.blogai.enums.ErrorCode;
import com.example.blogai.mapper.UserMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor

public class AuthService {

    JwtService jwtService;

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    InvalidatedTokenRepository invalidatedTokenRepository;

    TotpService totpService;

    UserMapper userMapper;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        return userMapper.toResponse(userRepository.save(user));
    }

    // Login step 1: Verify email and password
    public AuthResponse login(LoginRequest request){
        var user = userRepository.findByEmail(
                request.getEmail()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        if(user.isTotpVerified()){
            return AuthResponse.builder()
                    .require2FA(true)
                    .tempToken(jwtService.generateTempToken(user))
                    .build();
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    // Login step 2: verify OTP
    public AuthResponse verifyLoginOtp(String tempToken, String otpCode) {
        JWTClaimsSet jwtClaimsSet;
        // verify token
        try{
            jwtClaimsSet = jwtService.verifyToken(tempToken);

        }catch(AppException e){
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        // verify type token must be tempToken(TEMP_2FA)
        String tokenType = jwtService.extractTokenType(tempToken);
        if(!"TEMP_2FA".equals(tokenType)){
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        UUID userId = UUID.fromString(jwtClaimsSet.getSubject());
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(!totpService.verifyCode(user.getTotpSecret(), otpCode)){
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }


    public void logout(HttpServletRequest request){
        JWTClaimsSet claims = jwtService.extractClaimsFromRequest(request);


        invalidatedTokenRepository.save(
                InvalidatedToken.builder()
                        .id(claims.getJWTID())
                        .expiryTime(Instant.parse(claims.getExpirationTime().toString()))
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
