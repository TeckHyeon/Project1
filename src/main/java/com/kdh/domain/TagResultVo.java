package com.kdh.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagResultVo {
	  private Long post_idx;
	    private String post_id;
	    private String post_name;
	    private String post_title;
	    private String post_contents;
	    private int post_likes;
	    private String post_created_date;
	    private String post_updated_date;
	    private String deleted_yn;
	    private String timeago;
	    private String tag_name;
	    private ProfileVo profile = new ProfileVo();
	    private List<FileVo> fileList = new ArrayList<>();
	    private List<CommentVo> commentList = new ArrayList<>();
	    private List<TagsVo> tagList = new ArrayList<>();
}
