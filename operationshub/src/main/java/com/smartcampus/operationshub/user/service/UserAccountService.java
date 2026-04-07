package com.smartcampus.operationshub.user.service;

import java.util.List;

import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.user.dto.UserProfileResponse;
import com.smartcampus.operationshub.user.dto.UserSummaryResponse;

public interface UserAccountService {

    /**
     * Get user profile by ID
     */
    UserProfileResponse getUserProfile(String userId);

    /**
     * Get all users (paginated)
     */
    PaginatedResponse<UserSummaryResponse> getAllUsers(int page, int size);

    /**
     * Get users by role (e.g., TECHNICIAN)
     */
    List<UserSummaryResponse> getUsersByRole(String role);

    /**
     * Enable or disable user account
     */
    void updateUserStatus(String userId, boolean enabled);
}