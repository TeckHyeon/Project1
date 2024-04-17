package com.kdh.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.kdh.common.PostFiles;
import com.kdh.domain.FileVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.UserVo;
import com.kdh.mapper.UserMapper;

@Service
public class UserService {

	 @Autowired
	    private UserMapper userMapper;
	 
	 @Autowired
		private PostFiles postFiles;

	public List<PostVo> viewPost(String user_id) {
		// TODO Auto-generated method stub
		return userMapper.viewPost(user_id);
	}

	public List<PostVo> viewPostByLikes() {
		// TODO Auto-generated method stub
		return userMapper.viewPostByLikes();
	}

	public int selectMemberInfoYn(String user_id, String user_pw) {
		// TODO Auto-generated method stub
		return userMapper.selectMemberInfoYn(user_id, user_pw);
	}

	public UserVo loadUser(String user_id) {
		return userMapper.loadUser(user_id);
	}

	public void signin(UserVo userVo) {
		if (!userVo.getUser_id().equals("") && !userVo.getUser_name().equals("")) {

            userMapper.signin(userVo);
        }
	}

	public UserVo viewProfile(String user_Id) {
		return userMapper.viewProfile(user_Id);
	}

	public void updateinfo(UserVo userVo) {
		userMapper.updateinfo(userVo);
		
	}

	public void updatepw(UserVo userVo) {
		userMapper.updatepw(userVo);
		
	}

	public void insertPost(PostVo postVo, MultipartHttpServletRequest multiFiles) {
        // 시퀀스 값 조회 및 설정
        Long postIdx = userMapper.selectPostSeqNextVal(); // 시퀀스 값 조회 메소드 호출
        postVo.setPost_idx(postIdx); // 조회한 시퀀스 값을 PostVo에 설정

        // 게시글 정보를 DB에 삽입
        userMapper.insertPost(postVo);

        try {
            // 파일 정보 파싱 및 삽입
            List<FileVo> list = postFiles.parseFileInfo(postVo.getPost_idx(), multiFiles);
            if (!CollectionUtils.isEmpty(list)) {
                for (FileVo fileVo : list) {
                    userMapper.insertFile(fileVo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public List<FileVo> viewPostFileList(Long post_Idx) {
		// TODO Auto-generated method stub
		return userMapper.viewPostFileList(post_Idx);
	}

}
