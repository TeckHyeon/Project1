package com.kdh.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.kdh.common.PostFiles;
import com.kdh.common.ProfileFiles;
import com.kdh.domain.CommentVo;
import com.kdh.domain.FileVo;
import com.kdh.domain.FollowVo;
import com.kdh.domain.LikesVo;
import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.PostnotiVo;
import com.kdh.domain.ProfileVo;
import com.kdh.domain.UserVo;
import com.kdh.mapper.UserMapper;

@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PostFiles postFiles;
	
	@Autowired
	private ProfileFiles profileFiles;

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

	public PostVo view(Long post_idx) {
		// TODO Auto-generated method stub
		return userMapper.view(post_idx);
	}

	public void updatePost(PostVo postVo, MultipartHttpServletRequest multiFiles) {
		userMapper.updatePost(postVo);
		userMapper.deleteFile(postVo.getPost_idx());
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

	public void insertLike(LikesVo like, int post_idx) {
		userMapper.insertLike(like);
		userMapper.updatePostLikes(post_idx);
	}

	public void deleteLike(LikesVo like) {
		int post_idx = like.getPost_idx();
		userMapper.deleteLike(like);
		userMapper.updatePostLikes(post_idx);
	}

	public int checkLike(int user_idx, int post_idx) {
		int checkLike = userMapper.checkLike(user_idx, post_idx);
		return checkLike;
	}

	public List<PostVo> viewPostById(String user_id) {
		// TODO Auto-generated method stub
		return userMapper.viewPostById(user_id);
	}

	public int countLike(int post_idx) {
		// TODO Auto-generated method stub
		return userMapper.countLike(post_idx);
	}

	public void insertNoti(NotificationVo notiVo, PostnotiVo postnotiVo) {
		int notiIdx = userMapper.selectMaxNotificationIndex();
		notiVo.setNotification_idx(notiIdx);
		userMapper.insertNoti(notiVo);
		
		if(notiVo.getMessage() != 1) {
		
		} else {
			postnotiVo.setNotification_idx(notiIdx);
			userMapper.insertPostNoti(postnotiVo);	
		}
		
	}

	public List<NotificationVo> getNotis(String user_Id) {
		// TODO Auto-generated method stub
		return userMapper.getNotis(user_Id);
	}

	public void updateNoti(int notification_idx) {
		userMapper.updateNoti(notification_idx);
	}

	public PostnotiVo findPostNotibyIdx(int noti) {
		// TODO Auto-generated method stub
		return userMapper.findPostNotibyIdx(noti);
	}

	public PostVo findPostbyIdx(int post_idx) {
		// TODO Auto-generated method stub
		return userMapper.findPostbyIdx(post_idx);
	}

	public void insertComment(CommentVo vo) {
		userMapper.insertComment(vo);
	}

	public List<CommentVo> getCommentList(Long post_idx) {
		// TODO Auto-generated method stub
		return userMapper.getCommentList(post_idx);
	}

	public void saveProfile(MultipartFile file, UserVo user) {
	    int userIdx = user.getUser_idx(); // 시퀀스 값 조회 메소드 호출
	    
	    try {
	        // 파일 정보 파싱
	        ProfileVo profileVo = profileFiles.parseProfileInfo(userIdx, file);
	        if (profileVo != null) {
	            // 기존 프로필 사진이 있는지 확인
	            ProfileVo existingProfile = userMapper.findProfileByUserIdx(userIdx);
	            
	            if (existingProfile != null) {
	                // 기존 프로필 사진이 있으면 업데이트 또는 삭제 후 삽입
	            	Long file_idx = existingProfile.getFile_idx();
	            	profileVo.setFile_idx(file_idx);
	                userMapper.updateProfile(profileVo); // 프로필 사진 정보 업데이트 메소드
	                // 필요에 따라 기존 파일을 서버에서 삭제하는 로직도 추가할 수 있습니다.
	            } else {
	                // 기존 프로필 사진이 없으면 새로 삽입
	                userMapper.insertProfile(profileVo);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	public ProfileVo findProfileByUserIdx(int user_idx) {
		// TODO Auto-generated method stub
		return userMapper.findProfileByUserIdx(user_idx);
	}

	public List<FollowVo> findFollowingByUserId(String user_id) {
		// TODO Auto-generated method stub
		return userMapper.findFollowingByUserId(user_id);
	}

	public List<FollowVo> findFollowerByUserId(String user_id) {
		// TODO Auto-generated method stub
		return userMapper.findFollowerByUserId(user_id);
	}

}
