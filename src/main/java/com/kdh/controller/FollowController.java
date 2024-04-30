package com.kdh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kdh.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class FollowController {

	@Autowired
	private UserService userService;

	@GetMapping("/CheckFollow")
	public ResponseEntity<?> checkFollow(@RequestParam("user_id") String follower_id,
			@RequestParam("login_user") String following_id) {
		int followCount = userService.countFollow(follower_id, following_id);
		try {

			if (followCount != 0) {
				boolean following = true;
				return ResponseEntity.ok(following);
			} else {
				boolean following = false;
				return ResponseEntity.ok(following);
			}

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("팔로우 상태 확인에 실패했습니다.");
		}
	}

	@RequestMapping("/InsertFollow/{user_id}/{login_user}")
	public ResponseEntity<?> insertFollow(@PathVariable("user_id") String follower_id, @PathVariable("login_user") String following_id) {
		try {
			userService.insertFollow(follower_id, following_id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("팔로우 추가에 실패했습니다.");
		}
	}

	@RequestMapping("/DeleteFollow/{user_id}/{login_user}")
	public ResponseEntity<?> deleteFollow(@PathVariable("user_id") String follower_id, @PathVariable("login_user") String following_id) {
		try {
			userService.deleteFollow(follower_id, following_id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("팔로우 삭제에 실패했습니다.");
		}
	}

}