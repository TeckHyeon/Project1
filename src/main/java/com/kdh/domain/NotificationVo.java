package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVo {
	int notification_idx;
	int user_idx;
	String check_yn;
	String message;
	String created_date;
}
