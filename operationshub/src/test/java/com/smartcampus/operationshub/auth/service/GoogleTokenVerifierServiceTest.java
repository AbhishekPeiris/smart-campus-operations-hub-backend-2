package com.smartcampus.operationshub.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import com.smartcampus.operationshub.auth.config.GoogleOAuthProperties;
import com.smartcampus.operationshub.common.exception.BadRequestException;

@ExtendWith(MockitoExtension.class)
class GoogleTokenVerifierServiceTest {

    @Mock
    private JwtDecoder googleIdTokenDecoder;

    private GoogleOAuthProperties googleOAuthProperties;
    private GoogleTokenVerifierService googleTokenVerifierService;

    @BeforeEach
    void setUp() {
        googleOAuthProperties = new GoogleOAuthProperties();
        googleOAuthProperties.setGoogleClientId("google-client-id");
        googleTokenVerifierService = new GoogleTokenVerifierService(googleOAuthProperties, googleIdTokenDecoder);
    }

    @Test
    @DisplayName("Should validate a Google token and extract the user profile")
    void shouldValidateGoogleTokenAndExtractProfile() {
        Jwt jwt = new Jwt(
                "valid-token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "RS256"),
                Map.of(
                        "sub", "google-sub-123",
                        "email", "user@smartcampus.com",
                        "name", "Google User",
                        "email_verified", true,
                        "hd", "smartcampus.com"));

        when(googleIdTokenDecoder.decode("valid-token")).thenReturn(jwt);

        VerifiedGoogleUser verifiedGoogleUser = googleTokenVerifierService.verify("valid-token");

        assertEquals("google-sub-123", verifiedGoogleUser.subject());
        assertEquals("user@smartcampus.com", verifiedGoogleUser.email());
        assertEquals("Google User", verifiedGoogleUser.fullName());
        assertEquals("smartcampus.com", verifiedGoogleUser.hostedDomain());
    }

    @Test
    @DisplayName("Should reject Google sign-in when OAuth is not configured")
    void shouldRejectWhenGoogleOAuthIsNotConfigured() {
        googleOAuthProperties.setGoogleClientId("");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> googleTokenVerifierService.verify("token"));

        assertEquals("Google OAuth is not configured on the server", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject Google sign-in when hosted domain does not match")
    void shouldRejectWhenHostedDomainDoesNotMatch() {
        googleOAuthProperties.setGoogleHostedDomain("smartcampus.com");

        Jwt jwt = new Jwt(
                "valid-token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "RS256"),
                Map.of(
                        "sub", "google-sub-123",
                        "email", "user@gmail.com",
                        "name", "Google User",
                        "email_verified", true,
                        "hd", "gmail.com"));

        when(googleIdTokenDecoder.decode("valid-token")).thenReturn(jwt);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> googleTokenVerifierService.verify("valid-token"));

        assertEquals("Google account is not part of the allowed hosted domain", exception.getMessage());
    }

    @Test
    @DisplayName("Should surface invalid token errors from the decoder")
    void shouldSurfaceInvalidTokenErrors() {
        when(googleIdTokenDecoder.decode("invalid-token")).thenThrow(new BadJwtException("Google token audience is invalid"));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> googleTokenVerifierService.verify("invalid-token"));

        assertEquals("Google token audience is invalid", exception.getMessage());
    }
}
