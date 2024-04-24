package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVo {
	int notification_idx;
	String to_id;
	String check_yn;
	int message;
	String created_date;
	String from_id;
	int post_idx;
	PostnotiVo postNotiVo;
}
