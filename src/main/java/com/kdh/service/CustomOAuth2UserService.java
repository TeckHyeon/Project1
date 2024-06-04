package com.kdh.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.kdh.domain.UserVo;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
	private final SignInService signInService;
	private final HttpServletRequest request;

	public CustomOAuth2UserService(@Lazy SignInService signInService, HttpServletRequest request) {
		this.signInService = signInService;
		this.request = request;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = delegate.loadUser(userRequest);
		System.out.println("oAuth2User = " + oAuth2User);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		System.out.println("registrationId = " + registrationId);
		Map<String, Object> attributes = oAuth2User.getAttributes();
		System.out.println("attributes = " + attributes);
		Map<String, Object> responseAttributes = null;

		if ("google".equals(registrationId)) {
			responseAttributes = processGoogleUser(attributes, request);
		} else if ("naver".equals(registrationId)) {
			responseAttributes = processNaverUser(attributes);
		} else {
			throw new OAuth2AuthenticationException("Unsupported provider");
		}

		return new DefaultOAuth2User(oAuth2User.getAuthorities(), responseAttributes, "email");
	}

	private Map<String, Object> processGoogleUser(Map<String, Object> attributes, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		String userId = (String) attributes.get("email");
		String email = (String) attributes.get("email");
		String name = (String) attributes.get("name");
		String phone = "010-0000-0000";
		response.put("email", email);
		response.put("id", email);
		UserVo user = signInService.loadOrCreateUser(userId, email, name, phone);
	    if ("010-0000-0000".equals(user.getUser_phone())) {
	    	request.getSession().setAttribute("pendingUser", user);
	        throw new OAuth2AuthenticationException(new OAuth2Error("additional_info_required"), "Additional information is required");
	    }
		System.out.println("response = " + response);
		return response;
	}

	private Map<String, Object> processNaverUser(Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		if (response == null) {
			throw new OAuth2AuthenticationException("Invalid Naver user information");
		}
		System.out.println("response = " + response);
		String userId = (String) response.get("email");
		String email = (String) response.get("email");
		String name = (String) response.get("nickname");
		String phone = (String) response.get("mobile");
		UserVo user = signInService.loadOrCreateUser(userId, email, name, phone);
		response.put("email", userId);
		System.out.println("메서드 response = " + response);
		return response;
	}
}