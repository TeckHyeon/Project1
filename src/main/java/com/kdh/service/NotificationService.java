package com.kdh.service;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.kdh.domain.NotificationVo;

@Service
public class NotificationService {
    
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    public void addEmitter(String userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
    }
    
    public void removeEmitter(String userId, SseEmitter emitter) {
        emitters.remove(userId);
    }
    
    public void sendNotification(String userId, NotificationVo notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(notification, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
    }
}
