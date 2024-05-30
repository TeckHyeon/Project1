package com.kdh.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.kdh.service.CustomOAuth2UserService;
import com.kdh.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;
	private final CustomOAuth2UserService customOAuth2UserService;

	public SecurityConfig(CustomUserDetailsService customUserDetailsService,
			CustomOAuth2UserService customOAuth2UserService) {
		this.customUserDetailsService = customUserDetailsService;
		this.customOAuth2UserService = customOAuth2UserService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf((csrfConfig) -> csrfConfig.ignoringRequestMatchers("/ws/**"))
				.authorizeHttpRequests(
						authorizeRequests -> authorizeRequests.requestMatchers("/", "/login", "/loginFail", "/signin","/notify",
								"/profile/*", "/post_file/**", "/js/**", "/nav", "/css/**", "/images/**",
								"/resources/templates/layout/**", "/resources/templates/section/**", "/post_file/**",
								"/profile_file/**", "/user_profile/**").permitAll().anyRequest().authenticated())
				.formLogin(formLogin -> formLogin.loginPage("/login").defaultSuccessUrl("/", true)
						.failureUrl("/loginFail").permitAll())
				.oauth2Login(oauth2Login -> oauth2Login.loginPage("/login").defaultSuccessUrl("/", true)
						.failureUrl("/loginFail")
						.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)))
				.logout(logout -> logout.logoutSuccessUrl("/login").permitAll())
				.userDetailsService(customUserDetailsService);

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return customUserDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
