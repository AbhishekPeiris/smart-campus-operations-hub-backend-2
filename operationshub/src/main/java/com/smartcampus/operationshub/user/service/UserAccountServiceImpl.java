package com.smartcampus.operationshub.user.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.common.enums.UserRole;
import com.smartcampus.operationshub.common.exception.BadRequestException;
import com.smartcampus.operationshub.common.exception.ResourceNotFoundException;
import com.smartcampus.operationshub.user.dto.UserProfileResponse;
import com.smartcampus.operationshub.user.dto.UserSummaryResponse;
import com.smartcampus.operationshub.user.model.UserAccount;
import com.smartcampus.operationshub.user.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserProfileResponse getUserProfile(String userId) {
        return toUserProfileResponse(findUserById(userId));
    }

    @Override
    public PaginatedResponse<UserSummaryResponse> getAllUsers(int page, int size) {
        var userPage = userAccountRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        return PaginatedResponse.<UserSummaryResponse>builder()
                .content(userPage.getContent().stream().map(this::toUserSummaryResponse).toList())
                .currentPage(userPage.getNumber())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .pageSize(userPage.getSize())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
    }

    @Override
    public List<UserSummaryResponse> getUsersByRole(String role) {
        if (!UserRole.isValidRole(role)) {
            throw new BadRequestException("Invalid user role: " + role);
        }

        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        return userAccountRepository.findByRole(userRole).stream()
                .map(this::toUserSummaryResponse)
                .toList();
    }

    @Override
    public void updateUserStatus(String userId, boolean enabled) {
        UserAccount userAccount = findUserById(userId);
        userAccount.setAccountEnabled(enabled);
        userAccountRepository.save(userAccount);
    }

    private UserAccount findUserById(String userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private UserSummaryResponse toUserSummaryResponse(UserAccount userAccount) {
        return UserSummaryResponse.builder()
                .id(userAccount.getId())
                .fullName(userAccount.getFullName())
                .universityEmailAddress(userAccount.getUniversityEmailAddress())
                .role(userAccount.getRole())
                .build();
    }

    private UserProfileResponse toUserProfileResponse(UserAccount userAccount) {
        return UserProfileResponse.builder()
                .id(userAccount.getId())
                .fullName(userAccount.getFullName())
                .universityEmailAddress(userAccount.getUniversityEmailAddress())
                .contactNumber(userAccount.getContactNumber())
                .role(userAccount.getRole())
                .accountEnabled(userAccount.getAccountEnabled())
                .createdAt(userAccount.getCreatedAt())
                .updatedAt(userAccount.getUpdatedAt())
                .build();
    }
}