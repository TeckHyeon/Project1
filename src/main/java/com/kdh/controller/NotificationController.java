package com.kdh.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kdh.domain.FileVo;
import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.PostnotiVo;
import com.kdh.domain.ProfileVo;
import com.kdh.domain.UserVo;
import com.kdh.service.NotificationService;
import com.kdh.service.UserService;
import com.kdh.util.PostProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class NotificationController {

	@Autowired
	private UserService userService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private NotificationService notificationService;

	@PostMapping("/AddNoti")
	public ResponseEntity<?> addNoti(@RequestBody NotificationVo notiVo, PostnotiVo postnotiVo,
			Authentication authentication) {
		try {
			// post_id와 user_id가 같은지 확인합니다. 같다면 본인의 게시물에 알림을 추가하는 것이므로 요청을 거절합니다.
			String post_id = notiVo.getTo_id();
			String user_id = notiVo.getFrom_id();
			Long post_idx = notiVo.getPost_idx();
			Long message = notiVo.getMessage();
			if (post_id.equals(user_id)) {
				return ResponseEntity.ok().body("본인의 게시물은 알림이 등록되지 않습니다.");
			} else {
				// userService를 통해 알림을 추가하는 로직을 실행합니다.
				postnotiVo.setFrom_id(user_id);
				postnotiVo.setTo_id(post_id);
				postnotiVo.setPost_idx(post_idx);
				System.out.println("notivo = " + notiVo);
				notificationService.addNotification(notiVo, postnotiVo); // 알림 추가 메서드 호출
				messagingTemplate.convertAndSend("/topic/notification", notiVo);
				return ResponseEntity.ok().build();
			}
		} catch (Exception e) {
			// 오류가 발생하면 HTTP 상태 코드 400과 함께 오류 메시지를 반환합니다.
			return ResponseEntity.badRequest().body("알림 추가에 실패했습니다.");
		}
	}

	@GetMapping("/notiRefresh")
	public ModelAndView getNotificationFragment(ProfileVo profile, Authentication authentication,
			@AuthenticationPrincipal OAuth2User oAuth2User) {
		ModelAndView mv = new ModelAndView("/layout/notifragment");
		List<FileVo> allFiles = new ArrayList<>();
		List<NotificationVo> notis;
		UserVo loggedInUserVo = null;

		if (oAuth2User != null) {
			String user_Id = (String) oAuth2User.getAttribute("email");
			loggedInUserVo = userService.loadUser(user_Id);
			Long user_idx = loggedInUserVo.getUser_idx();
			notis = userService.getNotisCheckYn(user_Id);
			profile = userService.findProfileByUserIdx(user_idx);
			loggedInUserVo.setProfile(profile);
			mv.addObject("loggedInUser", loggedInUserVo);
			mv.addObject("noti", notis);
			for (NotificationVo noti : notis) {
				Long post_idx = noti.getPost_idx();
				PostVo post = userService.findPostbyIdx(post_idx);
				PostProcessor processor = new PostProcessor(userService);
				processor.processPost(post, allFiles);
				String post_id = post.getPost_id();
				UserVo postUserVo = userService.loadUser(post_id);
				Long noti_userIdx = postUserVo.getUser_idx();
				profile = userService.findProfileByUserIdx(noti_userIdx);
				post.setProfile(profile);
				noti.setPostVo(post);
				log.info("notizz = {}", noti);
			}
		} else {
			if (authentication != null && authentication.isAuthenticated()) {
				String user_Id = authentication.getName();
				loggedInUserVo = userService.loadUser(user_Id);
				Long user_idx = loggedInUserVo.getUser_idx();
				notis = userService.getNotisCheckYn(user_Id);
				profile = userService.findProfileByUserIdx(user_idx);
				loggedInUserVo.setProfile(profile);
				mv.addObject("loggedInUser", loggedInUserVo);
				mv.addObject("noti", notis);
				for (NotificationVo noti : notis) {
					Long post_idx = noti.getPost_idx();
					PostVo post = userService.findPostbyIdx(post_idx);
					PostProcessor processor = new PostProcessor(userService);
					processor.processPost(post, allFiles);
					String post_id = post.getPost_id();
					UserVo postUserVo = userService.loadUser(post_id);
					Long noti_userIdx = postUserVo.getUser_idx();
					profile = userService.findProfileByUserIdx(noti_userIdx);
					post.setProfile(profile);
					noti.setPostVo(post);
					log.info("notizz = {}", noti);
				}
			}
		}

		return mv;
	}

	@ResponseBody
	@GetMapping("/notify")
	public ResponseEntity<Void> handleNotification(@RequestParam("notificationIdx") Long notificationIdx,
			@RequestParam("to_id") String to_id) {
		System.out.println("Notification received for IDX: " + notificationIdx);
		System.out.println("Notification received for to_id: " + to_id);
		notificationService.notifyClients(to_id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/getPostDetail")
	public ModelAndView getPostDetail(@RequestParam HashMap<String, Object> map,
			Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User) {
		ModelAndView modelAndView = new ModelAndView("/layout/postnotidetail");
		log.info("HashMap = {}", map);
		NotificationVo notification = userService.getNotificationDetails(map);
		UserVo loggedInUserVo = null;
		if (oAuth2User != null) {
			String user_Id = (String) oAuth2User.getAttribute("email");
			loggedInUserVo = userService.loadUser(user_Id);
		} else {
			if (authentication != null && authentication.isAuthenticated()) {
				String user_Id = authentication.getName();
				loggedInUserVo = userService.loadUser(user_Id);
			}
		}
		modelAndView.addObject("notification", notification);
		log.info("getPostDetail Noti = {}",notification);
		modelAndView.addObject("user", loggedInUserVo);
		return modelAndView;
	}
	@PostMapping("/CheckNoti")
	public ResponseEntity<?> checkNoti(@RequestParam("notification_idx") Long notification_idx) {
		try {
			userService.updateNoti(notification_idx);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("알림 확인에 실패했습니다.");
		}
	}
}