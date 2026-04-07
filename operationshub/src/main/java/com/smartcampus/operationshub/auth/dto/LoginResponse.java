package com.smartcampus.operationshub.auth.dto;

import com.smartcampus.operationshub.common.enums.UserRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String accessToken;
    private String tokenType;

    private String userId;
    private String fullName;
    private String universityEmailAddress;
    private UserRole role;
}