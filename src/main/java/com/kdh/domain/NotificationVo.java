package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVo {
	Long notification_idx;
	String to_id;
	String check_yn;
	Long message;
	String created_date;
	String from_id;
	Long post_idx;
	PostnotiVo postNotiVo;
}
