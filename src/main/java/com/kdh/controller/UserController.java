package com.kdh.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
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
import com.kdh.domain.FollowVo;
import com.kdh.domain.LikesVo;
import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.ProfileVo;
import com.kdh.domain.SearchResultVo;
import com.kdh.domain.TagResultVo;
import com.kdh.domain.UserVo;
import com.kdh.service.UserService;
import com.kdh.util.PostProcessor;
import com.kdh.util.SessionManager;
import com.kdh.util.TimeAgo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public ModelAndView main(HttpSession session, ProfileVo profile) {
		ModelAndView mv = new ModelAndView("/pages/main");
		List<PostVo> posts;
		List<FileVo> allFiles = new ArrayList<>();
		List<NotificationVo> noti;
		UserVo loggedInUserVo = null;
		if (SessionManager.isLoggedIn(session)) {
			loggedInUserVo = SessionManager.getUserVo(session);
			String user_Id = loggedInUserVo.getUser_id();
			Long user_idx = loggedInUserVo.getUser_idx();
			posts = userService.viewPost(user_Id);
			noti = userService.getNotis(user_Id);
			profile = userService.findProfileByUserIdx(user_idx);
			loggedInUserVo.setProfile(profile);
			mv.addObject("loggedInUser", loggedInUserVo);
			log.info("posts = {}", posts);
			mv.addObject("posts", posts);
			mv.addObject("noti", noti);
		} else {
			posts = userService.viewPostByLikes();
			mv.addObject("posts", posts);
			log.info("posts = {}", posts);
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
		Boolean isLoggedIn = (session.getAttribute("userVo") != null);
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

	@PostMapping("/loginCheck")
	public ModelAndView loginCheck(UserVo userVo, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		String user_id = userVo.getUser_id();
		String user_pw = userVo.getUser_pw();
		int count = userService.selectMemberInfoYn(user_id, user_pw);

		if (count == 1) {
			HttpSession session = request.getSession(true);
			userVo = userService.loadUser(user_id);
			session.setAttribute("user_id", userVo.getUser_id());
			session.setAttribute("user_name", userVo.getUser_name());
			session.setAttribute("userVo", userVo);
			log.info("vo = {}", userVo);
			mv.setViewName("redirect:/");
			return mv;
		} else {
			log.info("vo = {}", userVo);
			mv.setViewName("redirect:/loginFail");
			return mv;
		}
	}

	@GetMapping("/loginFail")
	public ModelAndView loginFail() throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/pages/loginFail");
		return mv;
	}

	@GetMapping("/logout")
	public ModelAndView logout(HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		session.invalidate();
		ModelAndView mv = new ModelAndView();
		mv.setViewName("redirect:/");
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
		try {
			userService.signin(userVo);
		} catch (DuplicateKeyException e) {
			mv.setViewName("redirect:/signin?error_code=-1");
			return mv;
		} catch (Exception e) {
			e.printStackTrace();
			mv.setViewName("redirect:/signin?error_code=-99");
			return mv;
		}
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
	public ModelAndView write(PostVo postVo, MultipartHttpServletRequest multiFiles,
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
	public List<CommentVo> loadComments(@RequestParam("post_idx") Long post_idx) {
		// 특정 게시물에 대한 댓글 조회
		List<CommentVo> comments = userService.findCommentsByPostIdx(post_idx);
		comments.forEach(comment -> {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			comment.setCommentTimeAgo(calculateTimeAgo(comment.getUpdated_date(), formatter));
		});
		return comments;
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
	public ModelAndView tagResult(HttpSession session, ProfileVo profile, @PathVariable("tag_name") String tag_name) {
		ModelAndView mv = new ModelAndView("/section/tagResult");
		List<TagResultVo> trs = userService.viewPostByTag(tag_name);
		List<FileVo> allFiles = new ArrayList<>();
		List<NotificationVo> noti;
		UserVo loggedInUserVo = null;
		if (SessionManager.isLoggedIn(session)) {
			loggedInUserVo = SessionManager.getUserVo(session);
			String user_Id = loggedInUserVo.getUser_id();
			Long user_idx = loggedInUserVo.getUser_idx();
			noti = userService.getNotis(user_Id);
			profile = userService.findProfileByUserIdx(user_idx);
			loggedInUserVo.setProfile(profile);
			mv.addObject("loggedInUser", loggedInUserVo);
			mv.addObject("noti", noti);
		}
		PostProcessor processor = new PostProcessor(userService);
		for (TagResultVo tr : trs) {
			processor.processPost(tr, allFiles);
			String post_id = tr.getPost_id();
			UserVo postUserVo = userService.loadUser(post_id);
			Long user_idx = postUserVo.getUser_idx();
			profile = userService.findProfileByUserIdx(user_idx);
			log.info("profile = {}", profile);
			tr.setProfile(profile);
		}
		Boolean isLoggedIn = (session.getAttribute("userVo") != null);
		mv.addObject("trs", trs);
		mv.addObject("loggedIn", isLoggedIn);
		log.info("loggedIn = {}", isLoggedIn);
		return mv;
	}

}