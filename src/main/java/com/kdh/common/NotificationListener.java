package com.kdh.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.kdh.service.NotificationService;

import oracle.jdbc.dcn.DatabaseChangeEvent;

@Component
public class NotificationListener {

	private final NotificationService notificationService;

	@Autowired
	public NotificationListener(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@EventListener
	public void handleDatabaseChangeEvent(DatabaseChangeEvent event) {
		System.out.println("event = " + event);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();
			System.out.println("principal = " + principal);
			String userId = null;

			if (principal instanceof OAuth2User) {
				OAuth2User oAuth2User = (OAuth2User) principal;
				userId = (String) oAuth2User.getAttribute("email");
			} else {
				userId = authentication.getName();
			}

			if (userId != null) {
				notificationService.notifyClients(userId);
			}
		}
	}
}
