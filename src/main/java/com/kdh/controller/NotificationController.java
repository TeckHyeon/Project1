package com.kdh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostnotiVo;
import com.kdh.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/AddNoti")
    public ResponseEntity<?> addNoti(@RequestParam("post_id") String post_id, @RequestParam("user_id") String user_id,
			@RequestParam("post_idx") Long post_idx, @RequestParam("message") Long message, NotificationVo notiVo,
			PostnotiVo postnotiVo, Authentication authentication) {
        try {
            // post_id와 user_id가 같은지 확인합니다. 같다면 본인의 게시물에 알림을 추가하는 것이므로 요청을 거절합니다.
            if (post_id.equals(user_id)) {
                return ResponseEntity.ok().body("본인의 게시물은 알림이 등록되지 않습니다.");
            } else {
                // userService를 통해 알림을 추가하는 로직을 실행합니다.
                
                notiVo.setFrom_id(user_id);
                notiVo.setTo_id(post_id);
                notiVo.setMessage(message);

                notificationService.addNotification(notiVo, postnotiVo); // 알림 추가 메서드 호출

                // WebSocket을 통해 새 알림을 전달합니다.
                messagingTemplate.convertAndSend("/topic/notifications", "New notification added!");

                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            // 오류가 발생하면 HTTP 상태 코드 400과 함께 오류 메시지를 반환합니다.
            return ResponseEntity.badRequest().body("알림 추가에 실패했습니다.");
        }
    }
}