package com.kdh.util;

import com.kdh.domain.UserVo;

import jakarta.servlet.http.HttpSession;

public class SessionManager {

	private static final String USER_VO_SESSION_KEY = "userVo";

    public static UserVo getUserVo(HttpSession session) {
        return (UserVo) session.getAttribute(USER_VO_SESSION_KEY);
    }

    public static void setUserVo(HttpSession session, UserVo userVo) {
        session.setAttribute(USER_VO_SESSION_KEY, userVo);
    }
    public static boolean isLoggedIn(HttpSession session) {
        return getUserVo(session) != null;
    }
}
