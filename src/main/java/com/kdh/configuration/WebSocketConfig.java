package com.kdh.configuration;

import java.security.Principal;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// WebSocket 핸드셰이크 중에 CSRF 토큰을 쿼리 매개변수로 전달
		 registry.addEndpoint("/ws")
         .setAllowedOriginPatterns("http://example.*") // 허용할 origin 패턴을 설정
         .setHandshakeHandler(new DefaultHandshakeHandler())
         .withSockJS()
         .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1.5.1/dist/sockjs.min.js")
         .setStreamBytesLimit(512 * 1024)
         .setHttpMessageCacheSize(1000)
         .setDisconnectDelay(30 * 1000);
	}
	
}
