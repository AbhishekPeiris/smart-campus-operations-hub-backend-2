package com.smartcampus.operationshub.auth.service;

public record VerifiedGoogleUser(
        String subject,
        String email,
        String fullName,
        String hostedDomain
) {
}
