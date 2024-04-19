package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikesVo {
	public LikesVo(int updatedLikes) {
		// TODO Auto-generated constructor stub
	}
	private int likes_idx;
	private int post_idx;
	private int user_idx;
}
