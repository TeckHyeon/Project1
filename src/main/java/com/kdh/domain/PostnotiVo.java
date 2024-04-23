package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostnotiVo {
	int postnoti_idx;
	String from_id;
	String to_id;
	int post_idx;
	int notification_idx;
}
