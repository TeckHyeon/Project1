package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikesVo {
	private Long likes_idx;
	private Long post_idx;
	private Long user_idx;
}
