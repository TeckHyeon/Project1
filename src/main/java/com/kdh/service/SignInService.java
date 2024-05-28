package com.kdh.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kdh.domain.UserVo;
import com.kdh.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class SignInService {
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	
	
	public void signin(UserVo userVo) {
		if (!userVo.getUser_id().equals("") && !userVo.getUser_name().equals("")) {
			System.out.println("userVo = "+userVo);
            userVo.setUser_pw(passwordEncoder.encode(userVo.getUser_pw()));
			userMapper.signin(userVo);
		}
	}

}
