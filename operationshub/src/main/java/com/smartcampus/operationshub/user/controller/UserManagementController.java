package com.smartcampus.operationshub.user.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.operationshub.common.dto.ApiSuccessResponse;
import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.user.dto.UserProfileResponse;
import com.smartcampus.operationshub.user.dto.UserSummaryResponse;
import com.smartcampus.operationshub.user.service.UserAccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserAccountService userAccountService;

    @GetMapping("/{id}")
    public ApiSuccessResponse<UserProfileResponse> getUserProfile(@PathVariable String id) {
        return ApiSuccessResponse.<UserProfileResponse>builder()
                .success(true)
                .message("User profile retrieved")
                .data(userAccountService.getUserProfile(id))
                .build();
    }

    @GetMapping
    public ApiSuccessResponse<PaginatedResponse<UserSummaryResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiSuccessResponse.<PaginatedResponse<UserSummaryResponse>>builder()
                .success(true)
                .message("Users retrieved")
                .data(userAccountService.getAllUsers(page, size))
                .build();
    }

    @GetMapping("/role/{role}")
    public ApiSuccessResponse<List<UserSummaryResponse>> getUsersByRole(@PathVariable String role) {
        return ApiSuccessResponse.<List<UserSummaryResponse>>builder()
                .success(true)
                .message("Users by role retrieved")
                .data(userAccountService.getUsersByRole(role))
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiSuccessResponse<String> updateUserStatus(
            @PathVariable String id,
            @RequestParam boolean enabled
    ) {
        userAccountService.updateUserStatus(id, enabled);
        return ApiSuccessResponse.<String>builder()
                .success(true)
                .message("User status updated")
                .data(null)
                .build();
    }
}