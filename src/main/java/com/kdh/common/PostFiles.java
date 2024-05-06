package com.kdh.common;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.kdh.domain.FileVo;
import com.kdh.domain.PostVo;
import com.kdh.domain.TagResultVo;
import com.kdh.domain.UserVo;

@Component
public class PostFiles {

    @Value("${file.upload-dir}")
    private String uploadDir; // application.properties에서 설정한 업로드 경로

    public List<FileVo> parseFileInfo(Long post_Idx, MultipartHttpServletRequest multiFiles)
            throws Exception {

        if (ObjectUtils.isEmpty(multiFiles)) {
            return null;
        }

        List<FileVo> fileList = new ArrayList<>();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime current = ZonedDateTime.now();
        // 파일 저장 경로 설정에 커스텀 프로퍼티 값 사용
        String absolutePath = uploadDir + "post_file/" + current.format(format); 
        String path = "/post_file/" + current.format(format);
        File file = new File(absolutePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        Iterator<String> iterator = multiFiles.getFileNames();
        String newFileName;
        String originalFileExtension;
        String contentType;

        while (iterator.hasNext()) {
            String name = iterator.next();
            List<MultipartFile> list = multiFiles.getFiles(name);

            for (MultipartFile mFile : list) {
                if (!mFile.isEmpty()) {
                    contentType = mFile.getContentType();
                    if (ObjectUtils.isEmpty(contentType)) {
                        break;
                    } else {
                        if (contentType.contains("image/jpeg")) {
                            originalFileExtension = ".jpg";
                        } else if (contentType.contains("image/png")) {
                            originalFileExtension = ".png";
                        }else if (contentType.contains("image/svg")) {
                            originalFileExtension = ".svg";
                        } else if (contentType.contains("image/gif")) {
                            originalFileExtension = ".gif";
                        } else {
                            break;
                        }
                    }

                    newFileName = Long.toString(System.nanoTime()) + originalFileExtension;

                    FileVo postFile = new FileVo();
                    postFile.setPost_Idx(post_Idx);
                    postFile.setFile_Size(Long.toString(mFile.getSize()));
                    postFile.setOriginal_Name(mFile.getOriginalFilename());
                    postFile.setFile_Path(path + "/" + newFileName); // 저장 경로 수정
                    fileList.add(postFile);

                    file = new File(absolutePath + "/" + newFileName);
                    mFile.transferTo(file);
                }
            }
        }

        return fileList;
    }

    public static void addFilesToPostAndAllFilesList(List<FileVo> filesForPost, PostVo post, List<FileVo> allFiles) {
        if (filesForPost != null) {
            allFiles.addAll(filesForPost);
            post.setFileList(filesForPost);
        }
    }

    public static void logPostAndFileInformation(UserVo userVo, List<FileVo> allFiles, List<PostVo> posts, Logger log) {
        if (userVo != null) {
            log.info("vo = {}", userVo);
        }
        log.info("files = {}", allFiles);
        log.info("posts = {}", posts);
    }

	public static void addFilesToPostAndAllFilesList(List<FileVo> filesForPost, TagResultVo tr, List<FileVo> allFiles) {
        if (filesForPost != null) {
            allFiles.addAll(filesForPost);
            tr.setFileList(filesForPost);
        }
	}
}
