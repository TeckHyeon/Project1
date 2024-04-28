package com.kdh.common;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.kdh.domain.PostVo;
import com.kdh.domain.ProfileVo;

@Component
public class ProfileFiles {

    @Value("${file.upload-dir}")
    private String uploadDir; // application.properties에서 설정한 업로드 경로

    public List<ProfileVo> parseProfileInfo(int user_idx, MultipartFile file) throws Exception {
        List<ProfileVo> fileList = new ArrayList<>();

        if (file.isEmpty() || ObjectUtils.isEmpty(file.getContentType())) {
            return fileList;
        }

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime current = ZonedDateTime.now();
        String absolutePath = uploadDir + "profile_file/" + current.format(format);
        String path = "/profile_file/" + current.format(format);
        File directory = new File(absolutePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String contentType = file.getContentType();
        String originalFileExtension = null;
        if (contentType.contains("image/jpeg")) {
            originalFileExtension = ".jpg";
        } else if (contentType.contains("image/png")) {
            originalFileExtension = ".png";
        } else if (contentType.contains("image/gif")) {
            originalFileExtension = ".gif";
        }

        if (originalFileExtension != null) {
            String newFileName = Long.toString(System.nanoTime()) + originalFileExtension;

            ProfileVo profileFile = new ProfileVo();
            profileFile.setUser_idx(user_idx);
            profileFile.setFile_size(Long.toString(file.getSize()));
            profileFile.setOriginal_name(file.getOriginalFilename());
            profileFile.setFile_path(path + "/" + newFileName);
            fileList.add(profileFile);

            File newFile = new File(absolutePath + "/" + newFileName);
            file.transferTo(newFile);
        }

        return fileList;
    }

	public static void addProfileFilesToPost(PostVo post, ProfileVo profile) {
		 if (profile != null) {
	            post.setProfile(profile);
	        }
	}
}

