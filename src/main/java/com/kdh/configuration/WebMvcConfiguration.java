package com.kdh.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kdh.interceptor.LoginCheckInterceptor;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginCheckInterceptor()).addPathPatterns("/**").excludePathPatterns("/loginFail")
				.excludePathPatterns("/login").excludePathPatterns("/loginCheck").excludePathPatterns("/")
				.excludePathPatterns("/signin").excludePathPatterns("/profile/*").excludePathPatterns("/post_file/**")
				.excludePathPatterns("/js/**").excludePathPatterns("/nav").excludePathPatterns("/css/**").excludePathPatterns("/images/**")
				.excludePathPatterns("/resources/templates/layout/**") // 오타 수정
				.excludePathPatterns("/post_file/**") // 오타 수정
				.excludePathPatterns("/profile_file/**").excludePathPatterns("/user_profile/**"); // 오타 수정
	}

	@Bean
	public StandardServletMultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/public/", "classpath:/static/");
		registry.addResourceHandler("/dev/**").addResourceLocations("file:///C:/dev/");// 정적 리소스 경로 추가
	}
}
