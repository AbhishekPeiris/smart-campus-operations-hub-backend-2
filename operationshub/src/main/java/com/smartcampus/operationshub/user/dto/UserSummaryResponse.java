package com.smartcampus.operationshub.user.dto;

import com.smartcampus.operationshub.common.enums.UserRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryResponse {

    private String id;
    private String fullName;
    private String universityEmailAddress;
    private UserRole role;
}