package com.smartcampus.operationshub.user.dto;

import com.smartcampus.operationshub.common.enums.UserRole;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {

    @NotNull(message = "Role is required")
    private UserRole role;
}
