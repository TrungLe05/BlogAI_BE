package com.example.blogai.Service;

import com.example.blogai.Oauth2.Oauth2UserInfo;
import com.example.blogai.Oauth2.Oauth2UserInfoFactory;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.entities.User;
import com.example.blogai.enums.AuthProvider;
import com.example.blogai.enums.UserRole;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        // xử lý riêng cho github vì email có thể null
        if("github".equalsIgnoreCase(registrationId)){
            String email = (String) oAuth2User.getAttribute("user");
            if(email == null || email.isBlank()){
                email = fetchGithubPrimaryEmail(userRequest);
                attributes.put("email", email);
            }
        }

        Oauth2UserInfo userInfo = Oauth2UserInfoFactory.getOauth2UserInfo(registrationId, attributes);
        log.info("user info {}", userInfo.toString());
        if (userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_not_found"),
                    "Email not found from OAuth2 provider"
            );
        }

        User user = userRepository.findByEmail(userInfo.getEmail()).map(existingUser ->
                        updateExistingUser(existingUser, userInfo)
                ).orElseGet(() -> registerNewUser(userInfo, registrationId));

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "id"  // attribute dùng làm name
        );
    }

    private User registerNewUser(Oauth2UserInfo userInfo, String registrationId) {
        User user = new User();
        user.setEmail(userInfo.getEmail());
        user.setFullName(userInfo.getName());
        user.setAvatarUrl(userInfo.getAvatarUrl());
        user.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
        user.setProviderId(userInfo.getId());
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }

    private User updateExistingUser(User user, Oauth2UserInfo userInfo) {
        user.setFullName(userInfo.getName());
        user.setAvatarUrl(userInfo.getAvatarUrl());
        return userRepository.save(user);
    }

    private String fetchGithubPrimaryEmail(OAuth2UserRequest userRequest) {
        // Lấy access token của GitHub
        String accessToken = userRequest.getAccessToken().getTokenValue();

        // Gọi API GitHub để lấy danh sách email
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getBody() == null) return null;

        // Tìm email primary và verified
        return response.getBody().stream()
                .filter(emailObj ->
                        Boolean.TRUE.equals(emailObj.get("primary")) &&
                                Boolean.TRUE.equals(emailObj.get("verified"))
                )
                .map(emailObj -> (String) emailObj.get("email"))
                .findFirst()
                // Fallback: lấy email verified đầu tiên nếu không có primary
                .orElseGet(() -> response.getBody().stream()
                        .filter(emailObj -> Boolean.TRUE.equals(emailObj.get("verified")))
                        .map(emailObj -> (String) emailObj.get("email"))
                        .findFirst()
                        .orElse(null)
                );
    }
}
