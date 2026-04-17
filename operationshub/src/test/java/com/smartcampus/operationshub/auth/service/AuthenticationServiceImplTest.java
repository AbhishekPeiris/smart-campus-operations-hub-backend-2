package com.smartcampus.operationshub.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.smartcampus.operationshub.auth.config.GoogleOAuthProperties;
import com.smartcampus.operationshub.auth.dto.GoogleLoginRequest;
import com.smartcampus.operationshub.auth.dto.GoogleOAuthConfigResponse;
import com.smartcampus.operationshub.auth.dto.LoginResponse;
import com.smartcampus.operationshub.common.enums.UserRole;
import com.smartcampus.operationshub.common.exception.BadRequestException;
import com.smartcampus.operationshub.security.CustomUserDetailsService;
import com.smartcampus.operationshub.security.JwtService;
import com.smartcampus.operationshub.user.model.UserAccount;
import com.smartcampus.operationshub.user.repository.UserAccountRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private GoogleTokenVerifierService googleTokenVerifierService;

    private GoogleOAuthProperties googleOAuthProperties;
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        googleOAuthProperties = new GoogleOAuthProperties();
        googleOAuthProperties.setGoogleClientId("google-client-id");
        authenticationService = new AuthenticationServiceImpl(
                userAccountRepository,
                passwordEncoder,
                authenticationManager,
                jwtService,
                customUserDetailsService,
                googleTokenVerifierService,
                googleOAuthProperties);
    }

    @Test
    @DisplayName("Should register a new Google user and return JWT response")
    void shouldRegisterGoogleUserWhenNoExistingAccountMatches() {
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setIdToken("valid-google-token");

        VerifiedGoogleUser verifiedGoogleUser = new VerifiedGoogleUser(
                "google-sub-001",
                "user@smartcampus.com",
                "Test User",
                "smartcampus.com");

        UserDetails userDetails = new User(
                "user@smartcampus.com",
                "encoded-password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(googleTokenVerifierService.verify("valid-google-token")).thenReturn(verifiedGoogleUser);
        when(userAccountRepository.findByGoogleSubjectId("google-sub-001")).thenReturn(Optional.empty());
        when(userAccountRepository.findByUniversityEmailAddress("user@smartcampus.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded-google-password");
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> {
            UserAccount savedUser = invocation.getArgument(0);
            savedUser.setId("user-001");
            return savedUser;
        });
        when(customUserDetailsService.loadUserByUsername("user@smartcampus.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        LoginResponse response = authenticationService.loginWithGoogle(request);

        ArgumentCaptor<UserAccount> savedUserCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountRepository).save(savedUserCaptor.capture());

        assertEquals("user-001", response.getUserId());
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("user@smartcampus.com", response.getUniversityEmailAddress());
        assertEquals(UserRole.USER, response.getRole());
        assertEquals("google-sub-001", savedUserCaptor.getValue().getGoogleSubjectId());
        assertEquals("encoded-google-password", savedUserCaptor.getValue().getPasswordHash());
    }

    @Test
    @DisplayName("Should link an existing local account to the Google subject")
    void shouldLinkExistingLocalAccountToGoogleSubject() {
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setIdToken("valid-google-token");

        VerifiedGoogleUser verifiedGoogleUser = new VerifiedGoogleUser(
                "google-sub-002",
                "existing@smartcampus.com",
                "Updated Name",
                "smartcampus.com");

        UserAccount existingUser = UserAccount.builder()
                .id("user-002")
                .fullName("Existing User")
                .universityEmailAddress("existing@smartcampus.com")
                .passwordHash(null)
                .role(UserRole.ADMIN)
                .accountEnabled(true)
                .build();

        UserDetails userDetails = new User(
                "existing@smartcampus.com",
                "encoded-password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(googleTokenVerifierService.verify("valid-google-token")).thenReturn(verifiedGoogleUser);
        when(userAccountRepository.findByGoogleSubjectId("google-sub-002")).thenReturn(Optional.empty());
        when(userAccountRepository.findByUniversityEmailAddress("existing@smartcampus.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded-local-placeholder");
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customUserDetailsService.loadUserByUsername("existing@smartcampus.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        LoginResponse response = authenticationService.loginWithGoogle(request);

        ArgumentCaptor<UserAccount> savedUserCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountRepository).save(savedUserCaptor.capture());

        assertEquals("user-002", response.getUserId());
        assertEquals(UserRole.ADMIN, response.getRole());
        assertEquals("google-sub-002", savedUserCaptor.getValue().getGoogleSubjectId());
        assertEquals("Updated Name", savedUserCaptor.getValue().getFullName());
        assertEquals("encoded-local-placeholder", savedUserCaptor.getValue().getPasswordHash());
    }

    @Test
    @DisplayName("Should reject Google login when the email is linked to a different Google subject")
    void shouldRejectGoogleLoginWhenEmailAlreadyLinkedToDifferentGoogleSubject() {
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setIdToken("valid-google-token");

        VerifiedGoogleUser verifiedGoogleUser = new VerifiedGoogleUser(
                "google-sub-003",
                "existing@smartcampus.com",
                "Existing User",
                "smartcampus.com");

        UserAccount existingUser = UserAccount.builder()
                .id("user-003")
                .fullName("Existing User")
                .universityEmailAddress("existing@smartcampus.com")
                .googleSubjectId("google-sub-already-linked")
                .passwordHash("encoded-password")
                .role(UserRole.USER)
                .accountEnabled(true)
                .build();

        when(googleTokenVerifierService.verify("valid-google-token")).thenReturn(verifiedGoogleUser);
        when(userAccountRepository.findByGoogleSubjectId("google-sub-003")).thenReturn(Optional.empty());
        when(userAccountRepository.findByUniversityEmailAddress("existing@smartcampus.com"))
                .thenReturn(Optional.of(existingUser));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authenticationService.loginWithGoogle(request));

        assertEquals("This email is already linked to a different Google account", exception.getMessage());
    }

    @Test
    @DisplayName("Should expose Google OAuth config for the frontend")
    void shouldExposeGoogleOAuthConfig() {
        googleOAuthProperties.setGoogleClientId("frontend-client-id");

        GoogleOAuthConfigResponse response = authenticationService.getGoogleOAuthConfig();

        assertEquals("google", response.getProvider());
        assertEquals(true, response.isEnabled());
        assertEquals("frontend-client-id", response.getClientId());
    }
}
