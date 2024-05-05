package com.kdh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultVo {
	private String name;
	private String description;
	private String id;
	private String image;
	private Long count;
	private String type;
}
