package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowVo {
	private Long follow_idx;
	private String following_id;
	private String follower_id;
	private String follow_date;
	private UserVo user = new UserVo();
	private ProfileVo profile = new ProfileVo();
}
