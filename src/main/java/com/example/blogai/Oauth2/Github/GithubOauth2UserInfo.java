package com.example.blogai.Oauth2.Github;

import com.example.blogai.Oauth2.Oauth2UserInfo;

import java.util.Map;

public class GithubOauth2UserInfo extends Oauth2UserInfo {
    public GithubOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getName() {
        String name = (String) attributes.get("name");
        return name != null ? name : (String) attributes.get("login");
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getAvatarUrl() {
        return (String) attributes.get("avatar_url");
    }
}
