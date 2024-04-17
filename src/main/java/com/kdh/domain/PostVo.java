package com.kdh.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVo {
    private Long post_idx;
    private String post_id;
    private String post_name;
    private String post_title;
    private String post_contents;
    private int post_likes;
    private String post_created_date;
    private String post_updated_date;
    private String deleted_yn;
    private List<FileVo> fileList = new ArrayList<>();
    
}