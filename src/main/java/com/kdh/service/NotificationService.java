package com.kdh.service;

import java.util.List;

import javax.management.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostnotiVo;
import com.kdh.mapper.UserMapper;

@Service
public class NotificationService {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private UserMapper userMapper;

	public void sendNotification(Notification notification) {
		messagingTemplate.convertAndSend("/topic/notification", notification);
	}

	public void addNotification(NotificationVo notiVo, PostnotiVo postnotiVo) {
		try {
			System.out.println("알림 확인 : " + notiVo);
			System.out.println("postnotiVo : " + postnotiVo);
			Long notiIdx = userMapper.selectMaxNotificationIndex();
			notiVo.setNotification_idx(notiIdx);
			userMapper.insertNoti(notiVo);

			if (notiVo.getMessage() != 4) {
				postnotiVo.setNotification_idx(notiIdx);
				userMapper.insertPostNoti(postnotiVo);
			} else {
				// 이 부분에 필요한 처리를 추가합니다.
			}
		} catch (Exception e) {
			// 예외가 발생하면 적절히 처리합니다.
			e.printStackTrace(); // 예외 로그 출력
		}

	}

	public void notifyClients(String user_Id) {
		List<NotificationVo> notifications = userMapper.getNotis(user_Id);
		System.out.println("notifications = " + notifications);
		if (!notifications.isEmpty()) {
			messagingTemplate.convertAndSend("/topic/notification", notifications);
		}
	}

}