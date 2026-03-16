package com.example.blogai.Config;

import com.example.blogai.Oauth2.OAuth2AuthenticationFailureHandler;
import com.example.blogai.Oauth2.OAuth2AuthenticationSuccessHandler;
import com.example.blogai.Service.CustomOAuth2UserService;
import com.nimbusds.jose.JWSAlgorithm;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    @NonFinal
    private String secretKey;

    private String[] PUBLIC_ENDPOINTS = {
            "/auth/**", "/users/register", "/v3/api-docs", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**"
    };

    CustomOAuth2UserService customOAuth2UserService;
    OAuth2AuthenticationSuccessHandler successHandler;
    OAuth2AuthenticationFailureHandler failureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests((request) ->
                        request.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(
                                jwt -> jwt.decoder(jwtDecoder())
                        ))
                .oauth2Login(oauth2 ->
                        oauth2.userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                                .successHandler(successHandler)
                                .failureHandler(failureHandler)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncode(){
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    JwtDecoder jwtDecoder(){
        SecretKeySpec scKeySpec = new SecretKeySpec(
                secretKey.getBytes(), "HmacSHA256"
        );

        return NimbusJwtDecoder
                .withSecretKey(scKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}