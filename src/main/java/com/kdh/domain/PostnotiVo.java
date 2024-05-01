package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostnotiVo {
	Long postnoti_idx;
	String from_id;
	String to_id;
	Long post_idx;
	Long notification_idx;
	
}
