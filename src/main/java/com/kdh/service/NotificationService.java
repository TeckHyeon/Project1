package com.kdh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostnotiVo;
import com.kdh.mapper.UserMapper;

@Service
public class NotificationService {

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private UserMapper userMapper;
	
	public void createNotification(String notification) {
		// 알림 생성 로직 (DB 저장 등)

		// WebSocket을 통해 클라이언트에게 알림 전송
		template.convertAndSend("/topic/notifications", notification);
	}

	public void addNotification(NotificationVo notiVo, PostnotiVo postnotiVo) {
		try {
            System.out.println("알림 확인 : " + notiVo);
            Long notiIdx = userMapper.selectMaxNotificationIndex();
            notiVo.setNotification_idx(notiIdx);
            userMapper.insertNoti(notiVo);

            if (notiVo.getMessage() != 4) {
                postnotiVo.setNotification_idx(notiIdx);
                userMapper.insertPostNoti(postnotiVo);
            } else {
                // 이 부분에 필요한 처리를 추가합니다.
            }

            // 예외가 발생하지 않았으므로 WebSocket을 통해 알림을 전달합니다.
            template.convertAndSend("/topic/notifications", "New notification added!");
        } catch (Exception e) {
            // 예외가 발생하면 적절히 처리합니다.
            e.printStackTrace(); // 예외 로그 출력
        }
		
	}
}