package com.kdh.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ProfileController {

	@Autowired
	private UserService userService;

	@GetMapping("/profile/{user_name}")
	public ModelAndView profile(@PathVariable("user_name") String user_name, Authentication authentication,
			@AuthenticationPrincipal OAuth2User oAuth2User, @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) throws Exception {
		ModelAndView mv = new ModelAndView();
		UserVo loggedInUserVo = null; // 로그인한 사용자 정보
		String user_Id = null;
		List<FileVo> loginFiles = new ArrayList<>();
		List<NotificationVo> newNotis = new ArrayList<>();
		ProfileVo loginProfile = null;
		if (oAuth2User != null) {
			user_Id = (String) oAuth2User.getAttribute("email");
			loggedInUserVo = userService.loadUser(user_Id);
			Long user_idx = loggedInUserVo.getUser_idx();
			loginProfile = userService.findProfileByUserIdx(user_idx);
			loggedInUserVo.setProfile(loginProfile);
			mv.addObject("loggedInUser", loggedInUserVo);
			List<NotificationVo> notis = userService.getNotisCheckYn(user_Id);
			for (NotificationVo noti : notis) {
				Long post_idx = noti.getPost_idx();
				PostVo post = userService.findPostbyIdx(post_idx);
				PostProcessor processor = new PostProcessor(userService);
				processor.processPost(post, loginFiles);
				String post_id = post.getPost_id();
				UserVo postUserVo = userService.loadUser(post_id);
				Long noti_userIdx = postUserVo.getUser_idx();
				loginProfile = userService.findProfileByUserIdx(noti_userIdx);
				post.setProfile(loginProfile);
				noti.setPostVo(post);
				newNotis.add(noti);
				log.info("notizz = {}", noti);
			}
			mv.addObject("noti", newNotis);
		} else {
			if (authentication != null && authentication.isAuthenticated()) {
				user_Id = authentication.getName();
				loggedInUserVo = userService.loadUser(user_Id);
				mv.addObject("loggedInUser", loggedInUserVo);
				Long user_idx = loggedInUserVo.getUser_idx();
				List<NotificationVo> notis = userService.getNotisCheckYn(user_Id);
				loginProfile = userService.findProfileByUserIdx(user_idx);
				loggedInUserVo.setProfile(loginProfile);
				
				for (NotificationVo noti : notis) {
					Long post_idx = noti.getPost_idx();
					PostVo post = userService.findPostbyIdx(post_idx);
					PostProcessor processor = new PostProcessor(userService);
					processor.processPost(post, loginFiles);
					String post_id = post.getPost_id();
					UserVo postUserVo = userService.loadUser(post_id);
					Long noti_userIdx = postUserVo.getUser_idx();
					loginProfile = userService.findProfileByUserIdx(noti_userIdx);
					post.setProfile(loginProfile);
					noti.setPostVo(post);
					newNotis.add(noti);
					log.info("notizz = {}", noti);
				}
				mv.addObject("noti", newNotis);
			}
		}

		// 프로필 페이지 주인의 사용자 정보
		UserVo profileUserVo = userService.loadUserByName(user_name);
		String profileUserId = profileUserVo.getUser_id();
		mv.addObject("profileUser", profileUserVo);
		
		// 프로필 페이지 주인의 게시글 목록 가져오기
		List<PostVo> postList = new ArrayList<>();
		List<PostVo> posts = userService.viewPostById(profileUserId);
		

		// 프로필 페이지 주인이 좋아요 누른 게시물 목록 가져오기
		Long user_idx = profileUserVo.getUser_idx();
		List<PostVo> likePostList = new ArrayList<>();
		List<PostVo> likePosts = userService.viewLikePostsByIdx(user_idx);
		mv.addObject("likePosts", likePostList);

		// 프로필 페이지 주인의 프로필 정보 가져오기
		ProfileVo profile = userService.findProfileByUserIdx(profileUserVo.getUser_idx());
		mv.addObject("profile", profile);

		if (profile != null) {
			loggedInUserVo.setProfile(profile);
			
		}

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
			postList.add(post);
		}
		for (PostVo post : likePosts) {
			processor.processPost(post, allFiles);
			String post_id = post.getPost_id();
			UserVo postUserVo = userService.loadUser(post_id);
			Long postUser_Idx = postUserVo.getUser_idx();
			profile = userService.findProfileByUserIdx(postUser_Idx);
			log.info("profile = {}", profile);
			post.setProfile(profile);
			likePostList.add(post);
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
		log.info("isLoggedIn = {}", isLoggedIn);

	    // Ajax 요청인지 확인하고 뷰 이름 설정
	    if ("XMLHttpRequest".equals(requestedWith)) {
	        mv.setViewName("/section/profileSection"); // Ajax 요청인 경우
	    } else {
	        mv.setViewName("/pages/profile"); // 일반 요청인 경우
	    }
		
		mv.addObject("posts", postList);
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