package com.example.blogai.Config;

import com.example.blogai.Repository.UserRepository;
import com.example.blogai.Service.JwtService;
import com.example.blogai.entities.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Slf4j
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WebSocketAuthConfig implements WebSocketMessageBrokerConfigurer {

    JwtService jwtService;
    UserRepository userRepository;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new MessagingException("Missing or invalid token");
                    }

                    String token = authHeader.substring(7);
                    String email = jwtService.verifyToken(token).getClaim("email").toString();

                    if (email != null && jwtService.verifyToken(token).getJWTID() != null) {
                        User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new MessagingException("User not found"));

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        user.getId().toString(), null, List.of()
                                );
                        accessor.setUser(auth);

                    }
                }
                return message;
            }
        });
    }
}