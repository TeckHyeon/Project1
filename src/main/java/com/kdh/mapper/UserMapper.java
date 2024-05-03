package com.kdh.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kdh.domain.CommentVo;
import com.kdh.domain.FileVo;
import com.kdh.domain.FollowVo;
import com.kdh.domain.LikesVo;
import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostTagsVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.PostnotiVo;
import com.kdh.domain.ProfileVo;
import com.kdh.domain.SearchResultVo;
import com.kdh.domain.TagsVo;
import com.kdh.domain.UserVo;

@Mapper
public interface UserMapper {

	int selectMemberInfoYn(@Param("user_id") String user_id,@Param("user_pw") String user_pw);

	UserVo loadUser(String user_id);

	void signin(UserVo userVo);

    List<PostVo> viewPost(String user_id);

	List<PostVo> viewPostByLikes();

	void updateinfo(UserVo userVo);

	UserVo viewProfile(String user_Id);

	UserVo getUserById(String user_id);

	void updatepw(UserVo userVo);

	void insertPost(PostVo postVo);

	List<FileVo> viewPostFileList(Long post_Idx);

	void insertFilelist(List<FileVo> list);

	Long selectPostSeqNextVal();

	void insertFile(FileVo fileVo);

	PostVo view(Long post_idx);

	void updatePost(PostVo postVo);

	void deleteFiles(Long post_idx);

	void insertLike(LikesVo like);

	void deleteLike(LikesVo like);

	int checkLike(Long user_idx, Long post_idx);

	void updatePostLikes(Long post_idx);

	List<PostVo> viewPostById(String user_id);

	Long countLike(@Param("post_idx") Long post_idx);

	PostVo getPostByIdx(Long post_idx);

	void insertNoti(NotificationVo notiVo);

	List<NotificationVo> getNotis(String user_Id);

	void updateNoti(@Param("notification_idx") Long notification_idx);

	Long selectMaxNotificationIndex();

	void insertPostNoti(PostnotiVo postnotiVo);

	PostnotiVo findPostNotibyIdx(@Param("notification_idx") Long noti);

	PostVo findPostbyIdx(Long post_idx);

	void insertComment(CommentVo vo);

	List<CommentVo> getCommentList(Long post_idx);

	void insertProfile(ProfileVo profileVo);

	ProfileVo findProfileByUserIdx(Long user_idx);

	void deleteFile(Long post_idx);

	void updateProfile(ProfileVo profileVo);

	List<FollowVo> findFollowingByUserId(String user_id);

	List<FollowVo> findFollowerByUserId(String user_id);

	List<CommentVo> findCommentsByPostIdx(Long post_idx);

	Long countFollow(@Param("follower_id") String follower_id,@Param("following_id")  String following_id);

	void insertFollow(FollowVo follow);

	void deleteFollow(FollowVo follow);

	List<PostVo> viewLikePostsByIdx(Long user_idx);

	void insertTag(TagsVo tag);

	Long selectTagIdxMax();

	void insertPostTag(PostTagsVo postTag);

	Long findTagIdxByTagName(String tagName);

	List<TagsVo> findTagsNameByPostIdx(Long post_idx);

	void deletePost(Long post_idx);

	void deletePostFile(Long post_idx);

	List<FileVo> findFilesByPostIdx(Long post_idx);

	void deletePostTags(Long post_idx);

	List<SearchResultVo> findSearchResultList(String keyword);

}
