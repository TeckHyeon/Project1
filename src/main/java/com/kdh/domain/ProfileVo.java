package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileVo {
	private Long file_idx;
	private Long user_idx;
	private String original_name;
	private String file_path;
	private String file_size;
	private String upload_date;
	private String deleted_yn;
}
