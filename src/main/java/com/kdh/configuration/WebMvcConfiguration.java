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
				.excludePathPatterns("/signin").excludePathPatterns("/login").excludePathPatterns("/main").excludePathPatterns("/post_file/**").excludePathPatterns("/js/**").excludePathPatterns("/nav")
                .excludePathPatterns("/resources/templayes/layout/**"); // 정적 리소스 경로 제외
	}
	
	@Bean
	public StandardServletMultipartResolver multipartResolver() {
	    return new StandardServletMultipartResolver();
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	
		registry.addResourceHandler("/images/**").addResourceLocations("file:///C:/images/");
        registry.addResourceHandler("/resources/**").addResourceLocations("/public", "classpath:/static/"); // 정적 리소스 경로 추가
	}
}

