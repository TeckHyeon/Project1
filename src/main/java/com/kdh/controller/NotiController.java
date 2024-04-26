package com.kdh.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.kdh.common.PostFiles;
import com.kdh.domain.FileVo;
import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.PostnotiVo;
import com.kdh.domain.UserVo;
import com.kdh.service.NotificationService;
import com.kdh.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class NotiController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private NotificationService notificationService;

    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@SessionAttribute("userVo") UserVo user) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 무한대로 설정
        notificationService.addEmitter(user.getUser_id(), emitter);
        emitter.onCompletion(() -> notificationService.removeEmitter(user.getUser_id(), emitter));
        emitter.onTimeout(() -> notificationService.removeEmitter(user.getUser_id(), emitter));
        return emitter;
    }
    
	@PostMapping("/AddNoti")
	public ResponseEntity<?> addNoti(@RequestParam("post_id") String post_id, @RequestParam("user_id") String user_id,
			@RequestParam("post_idx") int post_idx, NotificationVo notiVo, PostnotiVo postnotiVo) {
		try {
			// post_id와 user_id가 같은지 확인합니다. 같다면 본인의 게시물에 알림을 추가하는 것이므로 요청을 거절합니다.
			if (post_id.equals(user_id)) {
				return ResponseEntity.ok().body("본인의 게시물은 알림이 등록되지 않습니다.");
			} else {
				// userService를 통해 알림을 추가하는 로직을 실행합니다.
				notiVo.setFrom_id(user_id);
				notiVo.setTo_id(post_id);
				postnotiVo.setFrom_id(user_id);
				postnotiVo.setTo_id(post_id);
				postnotiVo.setPost_idx(post_idx);
				userService.insertNoti(notiVo, postnotiVo);
				// 로깅을 통해 post_id 값을 기록합니다.
				log.info("post_idx = {}", post_id);
				// 성공적으로 처리되면 HTTP 상태 코드 200을 반환합니다.
				return ResponseEntity.ok().build();
			}
		} catch (Exception e) {
			// 오류가 발생하면 HTTP 상태 코드 400과 함께 오류 메시지를 반환합니다.
			return ResponseEntity.badRequest().body("알림 추가에 실패했습니다.");
		}
	}

	@PostMapping("/findPostNotibyIdx")
	public ModelAndView findPostNotibyIdx(@RequestParam("noti") int noti, PostnotiVo vo, PostVo post) {
		ModelAndView modelAndView = new ModelAndView("layout/postdetail");
		vo = userService.findPostNotibyIdx(noti);
		int post_idx = vo.getPost_idx();
		post = userService.findPostbyIdx(post_idx);

		List<FileVo> allFiles = new ArrayList<>();
		List<FileVo> filesForPost = userService.viewPostFileList(post.getPost_idx());
		PostFiles.addFilesToPostAndAllFilesList(filesForPost, post, allFiles);

		modelAndView.addObject("post", post);
		modelAndView.addObject("notification_idx", noti);
		log.info("post = {}", post);
		log.info("filesForPost = {}", filesForPost);
		modelAndView.setViewName("layout/postdetail :: postdetail");
		return modelAndView;
	}

	@PostMapping("/CheckNoti")
	public ResponseEntity<?> checkNoti(@RequestParam("notification_idx") int notification_idx) {
		try {
			userService.updateNoti(notification_idx);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("알림 확인에 실패했습니다.");
		}
	}

	@GetMapping("/notiRefresh")
	public ModelAndView getNotificationFragment(@SessionAttribute("userVo") UserVo user) {
		ModelAndView modelAndView = new ModelAndView("layout/notifragment");
		String userId = user.getUser_id();
		List<NotificationVo> noti = userService.getNotis(userId);
		modelAndView.addObject("noti", noti);
		modelAndView.setViewName("layout/notifragment :: noti");
		return modelAndView;
	}
}