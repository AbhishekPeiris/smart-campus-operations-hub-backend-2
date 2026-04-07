package com.smartcampus.operationshub.common.util;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.smartcampus.operationshub.common.exception.BadRequestException;

public class FileValidationUtil {

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "application/pdf"
    );

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    public static void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Invalid file type");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException("File size exceeds limit (5MB)");
        }
    }
}