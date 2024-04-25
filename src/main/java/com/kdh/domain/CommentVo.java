package com.kdh.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVo {
	private Long comment_idx;
	private String comment_content;
	private String from_id;
	private String from_name;
	private Long to_post_idx;
	private String created_date;
	private String updated_date;
	private String commentTimeAgo;
}
