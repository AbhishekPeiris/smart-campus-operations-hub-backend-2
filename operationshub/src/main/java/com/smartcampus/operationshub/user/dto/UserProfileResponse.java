package com.smartcampus.operationshub.user.dto;

import java.time.LocalDateTime;

import com.smartcampus.operationshub.common.enums.UserRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {

    private String id;
    private String fullName;
    private String universityEmailAddress;
    private String contactNumber;
    private UserRole role;
    private Boolean accountEnabled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}