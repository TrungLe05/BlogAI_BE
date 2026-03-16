package com.example.blogai.Oauth2;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Oauth2.Github.GithubOauth2UserInfo;
import com.example.blogai.Oauth2.Google.GoogleOauth2UserInfo;
import com.example.blogai.enums.ErrorCode;

import java.util.Map;

public class Oauth2UserInfoFactory {

    public static Oauth2UserInfo getOauth2UserInfo(String registrationId, Map<String, Object> attributes){
        return switch (registrationId.toLowerCase()){
            case "google" -> new GoogleOauth2UserInfo(attributes);
            case "github" -> new GithubOauth2UserInfo(attributes);
            default -> throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        };
    }
}
