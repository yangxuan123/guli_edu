package com.haha.guli.oss.service;

import java.io.InputStream;

public interface FileService {

    /**
     * 文件上传至阿里云
     */
    String upload(InputStream inputStream, String module, String originalFilename);

    /**
     * 根据url删除头像
     * @param url
     */
    void removeFile(String url);
}
