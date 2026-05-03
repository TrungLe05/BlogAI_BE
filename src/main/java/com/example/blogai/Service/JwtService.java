package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.InvalidatedTokenRepository;
import com.example.blogai.entities.InvalidatedToken;
import com.example.blogai.entities.User;
import com.example.blogai.enums.ErrorCode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    @NonFinal
    String secretKey;

    @NonFinal
    @Value("${jwt.expiration}")
    long expiration;

    @NonFinal
    @Value("${jwt.refresh-expiration}")
    long refreshExpiration;

    @NonFinal
    @Value("${jwt.temp-expiration}")
    long tempExpiration;

    InvalidatedTokenRepository invalidatedTokenRepository;

    public String generateToken(User user){
        return buildToken(user, expiration,"ACCESS");
    }

    public String generateRefreshToken(User user){
        return buildToken(user, refreshExpiration,"REFRESH");
    }

    public String generateTempToken(User user){
        return buildToken(user, tempExpiration, "TEMP_2FA");
    }

    private String buildToken(User user,long expirationTime, String type) {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .jwtID(UUID.randomUUID().toString())
                    .subject(user.getId().toString())
                    .issuer("BlogAI")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(expirationTime, ChronoUnit.SECONDS)))
                    .claim("email", user.getEmail())
                    .claim("role", "ROLE_"+user.getRole())
                    .claim("tokenType", type)
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256), claimsSet
            );

            signedJWT.sign(new MACSigner(secretKey.getBytes()));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public JWTClaimsSet verifyToken (String token){
        try{
            // parse token
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            //verify secret key
            if(!signedJWT.verify(new MACVerifier(secretKey.getBytes()))){
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }


            // check expiration
            if(claimsSet.getExpirationTime().before(new Date())){
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }

            // check token logout
            if(invalidatedTokenRepository.existsById(claimsSet.getJWTID())){
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }

            return claimsSet;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    // Lấy userId từ token
    public String extractUserId(String token) {
        return verifyToken(token).getSubject();
    }

    // Lấy tokenType từ token
    public String extractTokenType(String token) {
        try {
            return verifyToken(token).getStringClaim("tokenType");
        } catch (Exception e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    // JwtService.java
    public JWTClaimsSet extractClaimsFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        return verifyToken(authHeader.substring(7));
    }
}
