package com.example.blogai.Config;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.Service.JwtService;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // Chỉ xử lý lúc client gửi CONNECT frame
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    JWTClaimsSet claims = jwtService.verifyToken(token);

                    String tokenType = claims.getStringClaim("tokenType");
                    if (!"ACCESS".equals(tokenType)) {
                        throw new AppException(null);
                    }

                    UUID userId = UUID.fromString(claims.getSubject());
                    userRepository.findById(userId).ifPresent(user -> {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        List.of(new SimpleGrantedAuthority(
                                                "ROLE_" + user.getRole().name()))
                                );
                        // Gắn authentication vào STOMP session
                        accessor.setUser(auth);
                    });

                } catch (Exception e) {
                    log.warn("WebSocket auth failed: {}", e.getMessage());
                    // Không throw — để connection tiếp tục
                    // Các endpoint secured sẽ tự từ chối
                }
            }
        }

        return message;
    }
}