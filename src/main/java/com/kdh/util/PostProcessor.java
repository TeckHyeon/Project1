package com.kdh.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.kdh.common.PostFiles;
import com.kdh.common.ProfileFiles;
import com.kdh.domain.CommentVo;
import com.kdh.domain.FileVo;
import com.kdh.domain.NotificationVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.ProfileVo;
import com.kdh.domain.TagResultVo;
import com.kdh.domain.TagsVo;
import com.kdh.domain.UserVo;
import com.kdh.service.UserService;

public class PostProcessor {

	private UserService userService;
	private DateTimeFormatter formatter;

	// 클래스 생성자
	public PostProcessor(UserService userService) {
		this.userService = userService;
		this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	}

	public void processPost(PostVo post, List<FileVo> allFiles) {
		post.setTimeago(calculateTimeAgo(post.getPost_updated_date(), formatter));
		UserVo user = userService.loadUser(post.getPost_id());
		List<FileVo> filesForPost = userService.viewPostFileList(post.getPost_idx());
		ProfileVo profile = userService.findProfileByUserIdx(user.getUser_idx());

		PostFiles.addFilesToPostAndAllFilesList(filesForPost, post, allFiles);
		ProfileFiles.addProfileFilesToPost(post, profile);

		List<CommentVo> comments = userService.getCommentList(post.getPost_idx());
		comments.forEach(comment -> comment.setCommentTimeAgo(calculateTimeAgo(comment.getCreated_date(), formatter)));
		post.setCommentList(comments);

		List<TagsVo> tags = userService.findTagsNameByPostIdx(post.getPost_idx());
		post.setTagList(tags);

	}

	private String calculateTimeAgo(String dateStr, DateTimeFormatter formatter) {
		LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
		return TimeAgo.calculateTimeAgo(dateTime);
	}

	public void processTag(TagResultVo tr, List<FileVo> allFiles) {
		tr.setTimeago(calculateTimeAgo(tr.getPost_updated_date(), formatter));
		UserVo user = userService.loadUser(tr.getPost_id());
		List<FileVo> filesForPost = userService.viewPostFileList(tr.getPost_idx());
		ProfileVo profile = userService.findProfileByUserIdx(user.getUser_idx());

		PostFiles.addFilesToPostAndAllFilesList(filesForPost, tr, allFiles);
		ProfileFiles.addProfileFilesToPost(tr, profile);

		List<CommentVo> comments = userService.getCommentList(tr.getPost_idx());
		comments.forEach(comment -> comment.setCommentTimeAgo(calculateTimeAgo(comment.getCreated_date(), formatter)));
		tr.setCommentList(comments);

		List<TagsVo> tags = userService.findTagsNameByPostIdx(tr.getPost_idx());
		tr.setTagList(tags);
	}
}