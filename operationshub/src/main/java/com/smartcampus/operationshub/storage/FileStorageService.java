package com.smartcampus.operationshub.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeFile(MultipartFile file);

    void deleteFile(String fileName);
}