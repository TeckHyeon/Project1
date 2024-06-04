package com.kdh.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kdh.domain.UserVo;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OAuth2LoginController {

	@GetMapping("/oauth2/loginSuccess")
	public ModelAndView loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("redirect:/");
		mv.addObject("user", oAuth2User);
		return mv;
	}

	@GetMapping("/oauth2/additional-info")
	public ModelAndView additionalInfoForm(HttpServletRequest request) {
		UserVo user = (UserVo) request.getSession().getAttribute("pendingUser");
		ModelAndView mv = new ModelAndView();
		mv.addObject("user", user);
		mv.setViewName("/layout/addtional-info");
		return mv;
	}

	@PatchMapping("/oauth2/additional-info")
	public ModelAndView processAdditionalInfo() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/layout/addtional-info");
		return mv;
	}

	@DeleteMapping("/oauth2/additional-info")
	public ModelAndView deleteInfo() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/layout/addtional-info");
		return mv;
	}
}
