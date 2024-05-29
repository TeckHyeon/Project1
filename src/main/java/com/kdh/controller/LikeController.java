package com.kdh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kdh.domain.LikesVo;
import com.kdh.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class LikeController {

	@Autowired
	private UserService userService;

	@PostMapping("/LikeAdd")
	public ResponseEntity<?> addLike(@RequestBody LikesVo like, Authentication authentication) {
		   if (authentication == null || !authentication.isAuthenticated()) {
		        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
		    }
		try {
			Long post_idx = like.getPost_idx();
			userService.insertLike(like, post_idx);
			log.info("post_idx = {}", post_idx);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Like 추가에 실패했습니다.");
		}
	}

	@DeleteMapping("/LikeDelete")
	public ResponseEntity<?> deleteLike(@RequestBody LikesVo like, Authentication authentication) {
		   if (authentication == null || !authentication.isAuthenticated()) {
		        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
		    }
		try {
			userService.deleteLike(like);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Like 삭제에 실패했습니다.");
		}
	}

	@GetMapping("/CheckLike")
	public ResponseEntity<?> checkLike(@RequestParam("post_idx") Long post_idx,
			@RequestParam("user_idx") Long user_idx, Authentication authentication) {
		int checkLike = userService.checkLike(user_idx, post_idx);
		try {
			if (checkLike != 0) {
				boolean isLiked = true;
				return ResponseEntity.ok(isLiked);
			} else {
				boolean isLiked = false;
				return ResponseEntity.ok(isLiked);
			}

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Like 상태 확인에 실패했습니다.");
		}
	}

	@PostMapping("/LoadLikes")
	@ResponseBody
	public Long loadLikes(@RequestParam("post_idx") Long post_idx, Authentication authentication) {
		
		// postId를 기반으로 좋아요 수를 업데이트하고, 업데이트된 좋아요 수를 반환하는 로직 구현
		Long loadlikes = userService.countLike(post_idx);
		// 업데이트된 좋아요 수를 int로 직접 반환
		return loadlikes;
	}

}