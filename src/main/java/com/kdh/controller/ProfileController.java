package com.kdh.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.kdh.domain.FileVo;
import com.kdh.domain.FollowVo;
import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.ProfileVo;
import com.kdh.domain.UserVo;
import com.kdh.service.UserService;
import com.kdh.util.PostProcessor;
import com.kdh.util.SessionManager;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ProfileController {

	@Autowired
	private UserService userService;

	@GetMapping("/profileSection/{user_Id}")
	public ModelAndView profileSection(@PathVariable("user_Id") String user_Id, HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView();
		UserVo loggedInUserVo = null; // 로그인한 사용자 정보

		// 세션에서 로그인한 사용자 정보 가져오기
		if (SessionManager.isLoggedIn(session)) {
			loggedInUserVo = SessionManager.getUserVo(session);
			mv.addObject("loggedInUser", loggedInUserVo);
		}

		// 프로필 페이지 주인의 사용자 정보
		UserVo profileUserVo = userService.loadUser(user_Id);
		mv.addObject("profileUser", profileUserVo);

		// 프로필 페이지 주인의 게시글 목록 가져오기
		List<PostVo> posts = userService.viewPostById(user_Id);
		mv.addObject("posts", posts);

		// 프로필 페이지 주인이 좋아요 누른 게시물 목록 가져오기
		Long user_idx = profileUserVo.getUser_idx();
		List<PostVo> likePosts = userService.viewLikePostsByIdx(user_idx);
		mv.addObject("likePosts", likePosts);

		// 로그인한 사용자가 프로필 페이지의 주인인 경우 알림 목록 추가
		if (loggedInUserVo != null && loggedInUserVo.getUser_id().equals(user_Id)) {
			List<NotificationVo> noti = userService.getNotis(user_Id);
			mv.addObject("noti", noti);
		}

		// 프로필 페이지 주인의 프로필 정보 가져오기
		ProfileVo profile = userService.findProfileByUserIdx(profileUserVo.getUser_idx());
		mv.addObject("profile", profile);

		// 게시글과 관련된 파일 처리
		List<FileVo> allFiles = new ArrayList<>();
		PostProcessor processor = new PostProcessor(userService);
		for (PostVo post : posts) {
			processor.processPost(post, allFiles);
			String post_id = post.getPost_id();
			UserVo postUserVo = userService.loadUser(post_id);
			Long postUser_Idx = postUserVo.getUser_idx();
			profile = userService.findProfileByUserIdx(postUser_Idx);
			log.info("profile = {}", profile);
			post.setProfile(profile);
		}
		for (PostVo post : likePosts) {
			processor.processPost(post, allFiles);
			String post_id = post.getPost_id();
			UserVo postUserVo = userService.loadUser(post_id);
			Long postUser_Idx = postUserVo.getUser_idx();
			profile = userService.findProfileByUserIdx(postUser_Idx);
			log.info("profile = {}", profile);
			post.setProfile(profile);
		}
		// 팔로잉 목록 가져오기 및 추가 정보 처리
		List<FollowVo> followingList = userService.findFollowingByUserId(user_Id);
		followingList.forEach(following -> {
			UserVo followingUser = userService.loadUser(following.getFollowing_id());
			if (followingUser != null) {
				following.setUser(followingUser);
				ProfileVo followingUserprofile = userService.findProfileByUserIdx(followingUser.getUser_idx());
				following.setProfile(followingUserprofile);
			}
		});
		mv.addObject("following", followingList);
		// 팔로우 목록 가져오기 및 추가 정보 처리
		List<FollowVo> followerList = userService.findFollowerByUserId(user_Id);
		followerList.forEach(follow -> {
			UserVo followUser = userService.loadUser(follow.getFollower_id());
			if (followUser != null) {
				follow.setUser(followUser);
				ProfileVo followUserprofile = userService.findProfileByUserIdx(followUser.getUser_idx());
				follow.setProfile(followUserprofile);
			}
		});
		mv.addObject("follower", followerList);

		// 로그인 여부 세팅
		Boolean isLoggedIn = (loggedInUserVo != null);
		mv.addObject("loggedIn", isLoggedIn);
		log.info("isLoggedIn = {}",isLoggedIn);
		
		// 뷰 이름 설정
		mv.setViewName("/section/profileSection");
		return mv;
	}
	@GetMapping("/profile/{user_Id}")
	public ModelAndView profile(@PathVariable("user_Id") String user_Id, HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView();
		UserVo loggedInUserVo = null; // 로그인한 사용자 정보
		
		// 세션에서 로그인한 사용자 정보 가져오기
		if (SessionManager.isLoggedIn(session)) {
			loggedInUserVo = SessionManager.getUserVo(session);
			mv.addObject("loggedInUser", loggedInUserVo);
		}
		
		// 프로필 페이지 주인의 사용자 정보
		UserVo profileUserVo = userService.loadUser(user_Id);
		mv.addObject("profileUser", profileUserVo);
		
		// 프로필 페이지 주인의 게시글 목록 가져오기
		List<PostVo> posts = userService.viewPostById(user_Id);
		mv.addObject("posts", posts);
		
		// 프로필 페이지 주인이 좋아요 누른 게시물 목록 가져오기
		Long user_idx = profileUserVo.getUser_idx();
		List<PostVo> likePosts = userService.viewLikePostsByIdx(user_idx);
		mv.addObject("likePosts", likePosts);
		
		// 로그인한 사용자가 프로필 페이지의 주인인 경우 알림 목록 추가
		if (loggedInUserVo != null && loggedInUserVo.getUser_id().equals(user_Id)) {
			List<NotificationVo> noti = userService.getNotis(user_Id);
			mv.addObject("noti", noti);
		}
		
		// 프로필 페이지 주인의 프로필 정보 가져오기
		ProfileVo profile = userService.findProfileByUserIdx(profileUserVo.getUser_idx());
		mv.addObject("profile", profile);
		
		// 게시글과 관련된 파일 처리
		List<FileVo> allFiles = new ArrayList<>();
		PostProcessor processor = new PostProcessor(userService);
		for (PostVo post : posts) {
			processor.processPost(post, allFiles);
			String post_id = post.getPost_id();
			UserVo postUserVo = userService.loadUser(post_id);
			Long postUser_Idx = postUserVo.getUser_idx();
			profile = userService.findProfileByUserIdx(postUser_Idx);
			log.info("profile = {}", profile);
			post.setProfile(profile);
		}
		for (PostVo post : likePosts) {
			processor.processPost(post, allFiles);
			String post_id = post.getPost_id();
			UserVo postUserVo = userService.loadUser(post_id);
			Long postUser_Idx = postUserVo.getUser_idx();
			profile = userService.findProfileByUserIdx(postUser_Idx);
			log.info("profile = {}", profile);
			post.setProfile(profile);
		}
		// 팔로잉 목록 가져오기 및 추가 정보 처리
		List<FollowVo> followingList = userService.findFollowingByUserId(user_Id);
		followingList.forEach(following -> {
			UserVo followingUser = userService.loadUser(following.getFollowing_id());
			if (followingUser != null) {
				following.setUser(followingUser);
				ProfileVo followingUserprofile = userService.findProfileByUserIdx(followingUser.getUser_idx());
				following.setProfile(followingUserprofile);
			}
		});
		mv.addObject("following", followingList);
		// 팔로우 목록 가져오기 및 추가 정보 처리
		List<FollowVo> followerList = userService.findFollowerByUserId(user_Id);
		followerList.forEach(follow -> {
			UserVo followUser = userService.loadUser(follow.getFollower_id());
			if (followUser != null) {
				follow.setUser(followUser);
				ProfileVo followUserprofile = userService.findProfileByUserIdx(followUser.getUser_idx());
				follow.setProfile(followUserprofile);
			}
		});
		mv.addObject("follower", followerList);
		
		// 로그인 여부 세팅
		Boolean isLoggedIn = (loggedInUserVo != null);
		mv.addObject("loggedIn", isLoggedIn);
		log.info("isLoggedIn = {}",isLoggedIn);
		
		// 뷰 이름 설정
		mv.setViewName("/pages/profile");
		return mv;
	}

	@GetMapping("/updateform/{user_Id}")
	public ModelAndView updateForm(@PathVariable("user_Id") String user_Id, UserVo userVo) {
		ModelAndView mv = new ModelAndView();
		userVo = userService.viewProfile(user_Id);
		mv.addObject("user", userVo);
		mv.setViewName("/pages/updateform");
		return mv;
	}

	@PutMapping("/updateinfo/{user_Id}")
	public ModelAndView updateinfo(@PathVariable("user_Id") String user_Id, UserVo userVo) {

		ModelAndView mv = new ModelAndView();
		userService.updateinfo(userVo);
		mv.setViewName("redirect:/profile/{user_Id}");
		return mv;
	}

	@PutMapping("/updatepw/{user_Id}")
	public ModelAndView updatepw(@PathVariable("user_Id") String user_Id, UserVo userVo) {
		ModelAndView mv = new ModelAndView();
		userService.updatepw(userVo);
		mv.setViewName("redirect:/profile/{user_Id}");
		return mv;
	}
	
}