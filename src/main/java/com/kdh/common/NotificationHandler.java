package com.kdh.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class NotificationHandler extends TextWebSocketHandler {

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 연결 성공 시 작업
        System.out.println("WebSocket 연결 성공: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 클라이언트로부터의 메시지를 받으면 실행되는 부분
        String payload = message.getPayload();
        System.out.println("받은 메시지: " + payload);
        
        // 받은 메시지를 다시 클라이언트로 전송
        session.sendMessage(new TextMessage("받은 메시지: " + payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        // 연결 종료 시 작업
        System.out.println("WebSocket 연결 종료: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        // 에러 처리
        System.out.println("WebSocket 통신 중 에러 발생: " + exception.getMessage());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
