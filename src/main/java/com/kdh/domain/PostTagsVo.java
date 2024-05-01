package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostTagsVo {
	private Long post_tag_idx;
	private Long tag_idx;
	private Long post_idx;

}
