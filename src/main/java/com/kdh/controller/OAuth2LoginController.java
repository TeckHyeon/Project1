package com.kdh.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OAuth2LoginController {

    @GetMapping("/oauth2/loginSuccess")
    public ModelAndView loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/");
        mv.addObject("user", oAuth2User);
        return mv;
    }
}
