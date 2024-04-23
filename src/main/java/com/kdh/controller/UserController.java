package com.kdh.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.kdh.common.PostFiles;
import com.kdh.domain.FileVo;
import com.kdh.domain.LikesVo;
import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.PostnotiVo;
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
		List<NotificationVo> noti;
		if (SessionManager.isLoggedIn(session)) {
			userVo = SessionManager.getUserVo(session);
			String user_Id = userVo.getUser_id();
			posts = userService.viewPost(user_Id);
			noti = userService.getNotis(user_Id);
			mv.addObject("user", userVo);
			log.info("posts = {}", posts);
			mv.addObject("posts", posts);
			mv.addObject("noti", noti);
		} else {
			posts = userService.viewPostByLikes();
			mv.addObject("posts", posts);
			log.info("posts = {}", posts);
		}
		for (PostVo post : posts) {
			List<FileVo> filesForPost = userService.viewPostFileList(post.getPost_idx());
			PostFiles.addFilesToPostAndAllFilesList(filesForPost, post, allFiles);
		}
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
			posts = userService.viewPostById(userVo.getUser_id());
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
	
	@PostMapping("/LikeAdd")
	public ResponseEntity<?> addLike(@RequestBody LikesVo like) {
		try {
			int post_idx = like.getPost_idx();
			userService.insertLike(like, post_idx);
			log.info("post_idx = {}",post_idx);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Like 추가에 실패했습니다.");
		}
	}
	@PostMapping("/AddNoti")
	public ResponseEntity<?> addNoti(@RequestParam("post_id") String post_id, @RequestParam("user_id") String user_id, @RequestParam("post_idx") int post_idx , NotificationVo notiVo, PostnotiVo postnotiVo ) {
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


	@DeleteMapping("/LikeDelete")
	public ResponseEntity<?> deleteLike(@RequestBody LikesVo like) {
		try {
			userService.deleteLike(like);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Like 삭제에 실패했습니다.");
		}
	}

	@GetMapping("/CheckLike")
	public ResponseEntity<?> checkLike(@RequestParam("post_idx") int post_idx, @RequestParam("user_idx") int user_idx) {
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
	public int loadLikes(@RequestParam("post_idx") int post_idx) {
	    // postId를 기반으로 좋아요 수를 업데이트하고, 업데이트된 좋아요 수를 반환하는 로직 구현
	    int loadlikes = userService.countLike(post_idx);
	    // 업데이트된 좋아요 수를 int로 직접 반환
	    return loadlikes;
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
	@PostMapping("/CheckNoti")
	public ResponseEntity<?> checkNoti(@RequestParam("notification_idx") int notification_idx) {
		try {
			userService.updateNoti(notification_idx);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("알림 확인에 실패했습니다.");
		}
	}
	
	
	
}