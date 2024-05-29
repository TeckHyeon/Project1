package com.kdh.service;

import java.security.SecureRandom;
import java.util.Base64;

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
            System.out.println("userVo = " + userVo);
            userVo.setUser_pw(passwordEncoder.encode(userVo.getUser_pw()));
            userMapper.signin(userVo);
        }
    }

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24]; // 24 bytes * 8 bits = 192 bits
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public UserVo loadOrCreateUser(String userId, String email, String name, String phone) {
        UserVo user = userMapper.loadUser(userId);
        if (user == null) {
            user = new UserVo();
            user.setUser_id(userId);
            user.setUser_email(email);
            user.setUser_name(name);
            user.setUser_phone(phone);
            // 임의의 비밀번호 생성 및 설정
            String randomPassword = generateRandomPassword();
            user.setUser_pw(passwordEncoder.encode(randomPassword));
            System.out.println("서비스 user = "+user);
            userMapper.signin(user);
        }
        return user;
    }
}
