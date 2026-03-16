package com.example.blogai.Oauth2.Google;

import com.example.blogai.Oauth2.Oauth2UserInfo;

import java.util.Map;

public class GoogleOauth2UserInfo extends Oauth2UserInfo {


    public GoogleOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getAvatarUrl() {
        return (String) attributes.get("picture");
    }
}
