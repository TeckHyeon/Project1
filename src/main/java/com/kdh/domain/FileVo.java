package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVo {
	private Long postfile_Idx;
	private Long post_Idx;
	private String post_Id;
	private String original_Name;
	private String file_Path;
	private String file_Size;
	private String created_Date;
	private String deleted_yn;
}
