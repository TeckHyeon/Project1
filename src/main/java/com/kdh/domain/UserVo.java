package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {
	private Long user_idx;
	private String user_id;
	private String user_pw;
	private String user_name;
	private String user_email;
	private String user_phone;
	private String user_created_date;
	private String user_updated_date;
	private String deleted_yn;
	private String user_comments;
	private ProfileVo profile = new ProfileVo();
}
