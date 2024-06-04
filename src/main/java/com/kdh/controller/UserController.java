package com.kdh.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdh.domain.CommentVo;
import com.kdh.domain.FileVo;
import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.ProfileVo;
import com.kdh.domain.SearchResultVo;
import com.kdh.domain.TagResultVo;
import com.kdh.domain.UserVo;
import com.kdh.service.SignInService;
import com.kdh.service.UserService;
import com.kdh.util.PostProcessor;
import com.kdh.util.TimeAgo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private SignInService signInService;

	@GetMapping("/")
	public ModelAndView main(Authentication authentication, ProfileVo profile,
			@AuthenticationPrincipal OAuth2User oAuth2User) {
		ModelAndView mv = new ModelAndView("/pages/main");
		List<PostVo> posts;
		List<FileVo> allFiles = new ArrayList<>();
		List<NotificationVo> newNotis = new ArrayList<>();
		UserVo loggedInUserVo = null;

		if (oAuth2User != null) {
			// OAuth2User 객체에서 사용자 정보 가져오기
			String user_Id = (String) oAuth2User.getAttribute("email");
			loggedInUserVo = userService.loadUser(user_Id);
			// 이하 로직에 필요한 작업 수행
			Long user_idx = loggedInUserVo.getUser_idx();
			posts = userService.viewPost(user_Id);
			List<NotificationVo> notis = userService.getNotisCheckYn(user_Id);
			profile = userService.findProfileByUserIdx(user_idx);
			loggedInUserVo.setProfile(profile);
			mv.addObject("loggedInUser", loggedInUserVo);


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
				newNotis.add(noti);
				log.info("notizz = {}", noti);
			}
			log.info("posts = {}", posts);
			log.info("notis = {}", newNotis);
			mv.addObject("posts", posts);
			mv.addObject("noti", newNotis);
		} else {
			if (authentication != null && authentication.isAuthenticated()) {
				String user_Id = authentication.getName();
				loggedInUserVo = userService.loadUser(user_Id);
				Long user_idx = loggedInUserVo.getUser_idx();
				posts = userService.viewPost(user_Id);
				List<NotificationVo> notis = userService.getNotisCheckYn(user_Id);
				profile = userService.findProfileByUserIdx(user_idx);
				loggedInUserVo.setProfile(profile);
				mv.addObject("loggedInUser", loggedInUserVo);
			

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
					newNotis.add(noti);
					log.info("notizz = {}", noti);
				}
				log.info("posts = {}", posts);
				log.info("notis = {}", newNotis);
				mv.addObject("posts", posts);
				mv.addObject("noti", notis);
			} else {
				posts = userService.viewPostByLikes();
				mv.addObject("posts", posts);
				log.info("posts = {}", posts);
			}
		}

		PostProcessor processor = new PostProcessor(userService);
		for (PostVo post : posts) {
			processor.processPost(post, allFiles);
			String post_id = post.getPost_id();
			UserVo postUserVo = userService.loadUser(post_id);
			Long user_idx = postUserVo.getUser_idx();
			profile = userService.findProfileByUserIdx(user_idx);
			log.info("profile = {}", profile);
			post.setProfile(profile);
		}

		Boolean isLoggedIn = (authentication != null && authentication.isAuthenticated());
		mv.addObject("loggedIn", isLoggedIn);
		log.info("loggedIn = {}", isLoggedIn);
		return mv;
	}

	@GetMapping("/login")
	public ModelAndView login() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/pages/login");
		return mv;
	}

	@GetMapping("/loginFail")
	public ModelAndView loginFail() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/pages/loginFail");
		return mv;
	}

	@GetMapping("/logout")
	public ModelAndView logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
		ModelAndView mv = new ModelAndView();
		mv.setViewName("redirect:/login");
		return mv;
	}

	@GetMapping("/signin")
	public ModelAndView signin() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/pages/signin");
		return mv;
	}

	@PostMapping("/signin")
	public ModelAndView signinProgress(UserVo userVo) {
		ModelAndView mv = new ModelAndView();
		signInService.signin(userVo);
		mv.setViewName("redirect:/login");
		return mv;
	}

	@GetMapping("/write/{user_Id}")
	public ModelAndView writeform(@PathVariable("user_Id") String user_Id, UserVo userVo) {
		ModelAndView mv = new ModelAndView();
		userVo = userService.viewProfile(user_Id);
		mv.addObject("user", userVo);
		mv.setViewName("/pages/write");
		return mv;
	}

	@PostMapping("/write")
	public ModelAndView write(PostVo postVo, MultipartHttpServletRequest multiFiles, Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User,
			@RequestParam("tags") String tagsJson) {
		ModelAndView mv = new ModelAndView();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			List<String> tags = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {
			});
			userService.insertPost(postVo, multiFiles, tags);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		mv.setViewName("redirect:/");
		return mv;
	}

	@GetMapping("/UpdatePost/{post_idx}")
	public ModelAndView updatePost(@PathVariable("post_idx") Long post_idx, PostVo post)
			throws JsonProcessingException {
		ModelAndView mv = new ModelAndView();
		post = userService.view(post_idx);
		List<FileVo> allFiles = new ArrayList<>();
		PostProcessor processor = new PostProcessor(userService);
		processor.processPost(post, allFiles);
		ObjectMapper objectMapper = new ObjectMapper();
		List<String> tagNames = post.getTagList().stream().map(tag -> tag.getTag_name()).collect(Collectors.toList());
		String tagListJson = objectMapper.writeValueAsString(tagNames);
		mv.addObject("tagListJson", tagListJson);
		mv.addObject("post", post);
		mv.setViewName("/pages/updatepost");
		return mv;
	}

	@PutMapping("/UpdatePost/{post_idx}")
	public ModelAndView PostUpdate(@PathVariable("post_idx") Long post_idx, MultipartHttpServletRequest multiFiles,
			PostVo postVo, @RequestParam("tags") String tagsJson) {
		ModelAndView mv = new ModelAndView();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			postVo.setPost_idx(post_idx);
			List<String> tags = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {
			});
			userService.updatePost(postVo, multiFiles, tags);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		mv.setViewName("redirect:/");
		return mv;
	}

	@DeleteMapping("/DeletePost/{post_idx}")
	public ResponseEntity<?> PostDelete(@PathVariable("post_idx") Long post_idx) {
		try {
			userService.deletePost(post_idx);
			return ResponseEntity.ok().body("{\"message\":\"게시물 삭제 성공\"}");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Post 삭제에 실패했습니다.");
		}
	}

	@PostMapping("/updateProfile")
	public ResponseEntity<?> updateProfile(@RequestParam("file") MultipartFile file,
			@SessionAttribute("userVo") UserVo user) {
		try {
			userService.saveProfile(file, user); // 파일 저장 로직 (별도 구현 필요)
			return ResponseEntity.ok().body("프로필 사진이 변경되었습니다.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("프로필 사진 변경 중 오류가 발생했습니다.");
		}
	}

	@PostMapping("/CommentInsert")
	public ResponseEntity<?> insertComment(@RequestBody CommentVo vo, @SessionAttribute("userVo") UserVo user) {
		try {
			vo.setFrom_id(user.getUser_id());
			vo.setFrom_name(user.getUser_name());
			userService.insertComment(vo);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("알림 확인에 실패했습니다.");
		}
	}

	@GetMapping("/LoadComments")
	@ResponseBody
	public ModelAndView loadComments(@RequestParam("post_idx") Long post_idx) {
		// 특정 게시물에 대한 댓글 조회
		List<CommentVo> comments = userService.findCommentsByPostIdx(post_idx);
		comments.forEach(comment -> {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			comment.setCommentTimeAgo(calculateTimeAgo(comment.getUpdated_date(), formatter));
		});
		ModelAndView mv = new ModelAndView("/layout/comments");
		mv.addObject("comments", comments);
		return mv;
	}

	private String calculateTimeAgo(String dateStr, DateTimeFormatter formatter) {
		LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
		return TimeAgo.calculateTimeAgo(dateTime);
	}

	@GetMapping("/SearchResult")
	public ModelAndView searchResult(@RequestParam("keyword") String keyword, Model model) {
		ModelAndView mv = new ModelAndView("/layout/searchBox");
		List<SearchResultVo> lists = userService.findSearchResultList(keyword);
		mv.addObject("results", lists != null ? lists : Collections.emptyList());
		if (keyword != null) {
			mv.addObject("isSearched", true);
		}
		return mv;
	}

	@GetMapping("/TagResult/{tag_name}")
	public ModelAndView tagResult(Authentication authentication, ProfileVo profile, @PathVariable("tag_name") String tag_name, @AuthenticationPrincipal OAuth2User oAuth2User) {
		ModelAndView mv = new ModelAndView("/pages/tag");
		List<TagResultVo> trs = userService.viewPostByTag(tag_name);
		List<FileVo> allFiles = new ArrayList<>();
		List<NotificationVo> noti;
		UserVo loggedInUserVo = null;
		if (oAuth2User != null) {
			String user_Id = (String) oAuth2User.getAttribute("email");
			loggedInUserVo = userService.loadUser(user_Id);
			Long user_idx = loggedInUserVo.getUser_idx();
			noti = userService.getNotisCheckYn(user_Id);
			profile = userService.findProfileByUserIdx(user_idx);
			loggedInUserVo.setProfile(profile);
			mv.addObject("loggedInUser", loggedInUserVo);
			mv.addObject("noti", noti);
		}
		PostProcessor processor = new PostProcessor(userService);
		for (TagResultVo tr : trs) {
			processor.processTag(tr, allFiles);
			String post_id = tr.getPost_id();
			UserVo postUserVo = userService.loadUser(post_id);
			Long user_idx = postUserVo.getUser_idx();
			profile = userService.findProfileByUserIdx(user_idx);
			log.info("profile = {}", profile);
			tr.setProfile(profile);
		}
		Boolean isLoggedIn = (authentication != null && authentication.isAuthenticated());
		mv.addObject("loggedIn", isLoggedIn);
		mv.addObject("trs", trs);
		log.info("loggedIn = {}", isLoggedIn);
		return mv;
	}

	@ResponseBody
    @GetMapping("/checkDuplicateUsername")
    public boolean checkDuplicateUsername(@RequestParam("user_id") String user_id) {
        return userService.isUserIdExists(user_id);
    }
	
}