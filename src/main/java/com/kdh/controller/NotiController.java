package com.kdh.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.kdh.common.PostFiles;
import com.kdh.domain.CommentVo;
import com.kdh.domain.FileVo;
import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.PostnotiVo;
import com.kdh.domain.UserVo;
import com.kdh.service.UserService;
import com.kdh.util.TimeAgo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class NotiController {

	@Autowired
	private UserService userService;

	@PostMapping("/AddNoti")
	public ResponseEntity<?> addNoti(@RequestParam("post_id") String post_id, @RequestParam("user_id") String user_id,
			@RequestParam("post_idx") int post_idx, @RequestParam("message") int message, NotificationVo notiVo,
			PostnotiVo postnotiVo) {
		try {
			// post_id와 user_id가 같은지 확인합니다. 같다면 본인의 게시물에 알림을 추가하는 것이므로 요청을 거절합니다.
			if (post_id.equals(user_id)) {
				return ResponseEntity.ok().body("본인의 게시물은 알림이 등록되지 않습니다.");
			} else {
				// userService를 통해 알림을 추가하는 로직을 실행합니다.
				notiVo.setFrom_id(user_id);
				notiVo.setTo_id(post_id);
				notiVo.setMessage(message);

				if (message != 4) {
					postnotiVo.setFrom_id(user_id);
					postnotiVo.setTo_id(post_id);
					postnotiVo.setPost_idx(post_idx);
					userService.insertNoti(notiVo, postnotiVo);
				} else {
					userService.insertNoti(notiVo, postnotiVo);
				}
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
		
		 List<CommentVo> comments = userService.findCommentsByPostIdx(post_idx);
		    comments.forEach(comment -> {
		    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		        comment.setCommentTimeAgo(calculateTimeAgo(comment.getUpdated_date(), formatter));
		    });
		
		
		
		modelAndView.addObject("comments", comments);
		modelAndView.addObject("post", post);
		modelAndView.addObject("noti_idx", noti);
		log.info("comments = {}", comments);
		log.info("noti = {}", noti);
		log.info("post = {}", post);
		log.info("filesForPost = {}", filesForPost);
		modelAndView.setViewName("layout/postdetail :: postdetail");
		return modelAndView;
	}
    private String calculateTimeAgo(String dateStr, DateTimeFormatter formatter) {
        LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
        return TimeAgo.calculateTimeAgo(dateTime);
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