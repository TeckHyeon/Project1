package com.kdh.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.kdh.common.PostFiles;
import com.kdh.domain.FileVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.UserVo;
import com.kdh.service.UserService;
import com.kdh.util.SessionManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public ModelAndView main(HttpSession session, UserVo userVo) {
		ModelAndView mv = new ModelAndView("/pages/main");
		List<PostVo> posts;
		List<FileVo> allFiles = new ArrayList<>();
		if (SessionManager.isLoggedIn(session)) {
			userVo = SessionManager.getUserVo(session);
			posts = userService.viewPost(userVo.getUser_id());
			mv.addObject("user", userVo);
			log.info("posts = {}", posts);
		} else {
			posts = userService.viewPostByLikes();
			log.info("posts = {}", posts);
		}
		for (PostVo post : posts) {
			List<FileVo> filesForPost = userService.viewPostFileList(post.getPost_idx());
			PostFiles.addFilesToPostAndAllFilesList(filesForPost, post, allFiles);
		}
		mv.addObject("posts", posts);
		mv.addObject("files", allFiles);
		PostFiles.logPostAndFileInformation(userVo, allFiles, posts, log);
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
	public ModelAndView write(PostVo postVo, MultipartHttpServletRequest multiFiles) {
		ModelAndView mv = new ModelAndView();
		userService.insertPost(postVo, multiFiles);
		mv.setViewName("redirect:/");
		return mv;
	}

	@GetMapping("/profile/{user_Id}")
	public ModelAndView profile(@PathVariable("user_Id") String user_Id, HttpSession session, UserVo userVo)
			throws Exception {
		ModelAndView mv = new ModelAndView();
		List<PostVo> posts;
		List<FileVo> allFiles = new ArrayList<>();
		if (SessionManager.isLoggedIn(session)) {
			userVo = SessionManager.getUserVo(session);
			posts = userService.viewPost(userVo.getUser_id());
			mv.addObject("user", userVo);
			log.info("posts = {}", posts);
		} else {
			posts = userService.viewPostByLikes();
			log.info("posts = {}", posts);
		}
		for (PostVo post : posts) {
			List<FileVo> filesForPost = userService.viewPostFileList(post.getPost_idx());
			PostFiles.addFilesToPostAndAllFilesList(filesForPost, post, allFiles);
		}
		mv.addObject("posts", posts);
		mv.addObject("files", allFiles);
		PostFiles.logPostAndFileInformation(userVo, allFiles, posts, log);
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

	@GetMapping("/UpdatePost/{post_idx}")
	public ModelAndView updatePost(@PathVariable("post_idx") Long post_idx, PostVo postVo) {
		ModelAndView mv = new ModelAndView();
		postVo = userService.view(post_idx);

		List<FileVo> filesForPost = userService.viewPostFileList(post_idx);
		log.info("file = {}", post_idx);
		mv.addObject("postVo", postVo);
		mv.addObject("file", filesForPost);
		mv.setViewName("/pages/updatepost");
		return mv;
	}
	@PutMapping("/UpdatePost/{post_idx}")
	public ModelAndView PostUpdate(@PathVariable("post_idx") Long post_idx, MultipartHttpServletRequest multiFiles, PostVo postVo) {
		ModelAndView mv = new ModelAndView();
		postVo.setPost_idx(post_idx);
		userService.updatePost(postVo, multiFiles);
		mv.setViewName("redirect:/");
		return mv;
	}

}