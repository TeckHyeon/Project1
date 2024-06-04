package com.kdh.configuration;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.kdh.domain.UserVo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		if (exception instanceof OAuth2AuthenticationException) {
			OAuth2AuthenticationException oAuth2Exception = (OAuth2AuthenticationException) exception;
			String errorCode = oAuth2Exception.getError().getErrorCode();

			// "additional_info_required" 오류 코드에 대한 처리
			if ("additional_info_required".equals(errorCode)) {
				response.sendRedirect("/oauth2/additional-info");
			}
		} else {
			// 다른 모든 인증 실패에 대한 기본 처리
			response.sendRedirect("/loginFail");
		}
	}

}
