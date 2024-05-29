package com.kdh.service;

import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.kdh.domain.UserVo;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    private final SignInService signInService;

    public CustomOAuth2UserService(@Lazy SignInService signInService) {
        this.signInService = signInService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        System.out.println("response = "+response);
        String userId = (String) response.get("email");
        String email = (String) response.get("email");
        String name = (String) response.get("nickname");
        String phone = (String) response.get("mobile");
        UserVo user = signInService.loadOrCreateUser(userId, email, name, phone);
        System.out.println("유저 = "+user);
        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                response,
                "id");
    }
}