package com.example.blogai.Config;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.Service.JwtService;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Không có token → bỏ qua, Spring Security tự xử lý
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            JWTClaimsSet claims = jwtService.verifyToken(token);

            // Chỉ chấp nhận ACCESS token cho các request thông thường
            // TEMP_2FA và REFRESH token không được dùng để access API
            String tokenType = claims.getStringClaim("tokenType");
            if (!"ACCESS".equals(tokenType)) {
                chain.doFilter(request, response);
                return;
            }

            // Lấy user từ DB và đặt vào SecurityContext
            UUID userId = UUID.fromString(claims.getSubject());
            userRepository.findById(userId).ifPresent(user -> {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                        );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            });

        } catch (AppException e) {
            // Token invalid hoặc expired
            // Không set Authentication → Spring Security trả 401 tự động
            log.debug("JWT validation failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in JwtAuthFilter", e);
        }

        chain.doFilter(request, response);
    }
}