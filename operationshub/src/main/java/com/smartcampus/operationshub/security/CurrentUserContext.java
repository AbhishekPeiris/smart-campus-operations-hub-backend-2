package com.smartcampus.operationshub.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.smartcampus.operationshub.common.exception.ResourceNotFoundException;
import com.smartcampus.operationshub.user.model.UserAccount;
import com.smartcampus.operationshub.user.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserContext {

    private final UserAccountRepository userAccountRepository;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        return authentication.getName();
    }

    public UserAccount getCurrentUser() {
        String email = getCurrentUsername();

        if (email == null) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        return userAccountRepository.findByUniversityEmailAddress(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user record not found"));
    }

    public String getCurrentUserId() {
        return getCurrentUser().getId();
    }
}