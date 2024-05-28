package com.kdh.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kdh.domain.UserVo;
import com.kdh.util.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserVo userVo = userService.loadUser(username);
        System.out.println("로그인한 userVo = "+userVo);
        if (userVo == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(userVo);
    }
}