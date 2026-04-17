package com.smartcampus.operationshub.auth.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smartcampus.operationshub.auth.config.GoogleOAuthProperties;
import com.smartcampus.operationshub.auth.dto.GoogleLoginRequest;
import com.smartcampus.operationshub.auth.dto.GoogleOAuthConfigResponse;
import com.smartcampus.operationshub.auth.dto.LoginRequest;
import com.smartcampus.operationshub.auth.dto.LoginResponse;
import com.smartcampus.operationshub.auth.dto.RegisterUserRequest;
import com.smartcampus.operationshub.common.enums.UserRole;
import com.smartcampus.operationshub.common.exception.BadRequestException;
import com.smartcampus.operationshub.security.CustomUserDetailsService;
import com.smartcampus.operationshub.security.JwtService;
import com.smartcampus.operationshub.user.model.UserAccount;
import com.smartcampus.operationshub.user.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final GoogleOAuthProperties googleOAuthProperties;

    @Override
    public void registerUser(RegisterUserRequest request) {
        if (userAccountRepository.existsByUniversityEmailAddress(request.getUniversityEmailAddress())) {
            throw new BadRequestException("A user with this email already exists");
        }

        UserAccount userAccount = UserAccount.builder()
                .fullName(request.getFullName())
                .universityEmailAddress(request.getUniversityEmailAddress())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .contactNumber(request.getContactNumber())
                .role(request.getRole())
                .accountEnabled(true)
                .build();

        userAccountRepository.save(userAccount);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUniversityEmailAddress(),
                        request.getPassword()));

        UserAccount userAccount = userAccountRepository.findByUniversityEmailAddress(request.getUniversityEmailAddress())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUniversityEmailAddress());
        String token = jwtService.generateToken(userDetails);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(userAccount.getId())
                .fullName(userAccount.getFullName())
                .universityEmailAddress(userAccount.getUniversityEmailAddress())
                .role(userAccount.getRole())
                .build();
    }

    @Override
    public LoginResponse loginWithGoogle(GoogleLoginRequest request) {
        VerifiedGoogleUser verifiedGoogleUser = googleTokenVerifierService.verify(request.getIdToken());
        UserAccount userAccount = resolveGoogleUser(verifiedGoogleUser);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userAccount.getUniversityEmailAddress());
        String token = jwtService.generateToken(userDetails);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(userAccount.getId())
                .fullName(userAccount.getFullName())
                .universityEmailAddress(userAccount.getUniversityEmailAddress())
                .role(userAccount.getRole())
                .build();
    }

    @Override
    public GoogleOAuthConfigResponse getGoogleOAuthConfig() {
        return GoogleOAuthConfigResponse.builder()
                .provider("google")
                .enabled(StringUtils.hasText(googleOAuthProperties.getGoogleClientId()))
                .clientId(googleOAuthProperties.getGoogleClientId())
                .build();
    }

    private UserAccount resolveGoogleUser(VerifiedGoogleUser verifiedGoogleUser) {
        Optional<UserAccount> userByGoogleSubject = userAccountRepository
                .findByGoogleSubjectId(verifiedGoogleUser.subject());
        if (userByGoogleSubject.isPresent()) {
            return synchronizeGoogleUser(userByGoogleSubject.get(), verifiedGoogleUser);
        }

        Optional<UserAccount> userByEmail = userAccountRepository
                .findByUniversityEmailAddress(verifiedGoogleUser.email());
        if (userByEmail.isPresent()) {
            UserAccount existingUser = userByEmail.get();
            if (StringUtils.hasText(existingUser.getGoogleSubjectId())
                    && !verifiedGoogleUser.subject().equals(existingUser.getGoogleSubjectId())) {
                throw new BadRequestException("This email is already linked to a different Google account");
            }

            return synchronizeGoogleUser(existingUser, verifiedGoogleUser);
        }

        return registerGoogleUser(verifiedGoogleUser);
    }

    private UserAccount synchronizeGoogleUser(UserAccount userAccount, VerifiedGoogleUser verifiedGoogleUser) {
        if (!Boolean.TRUE.equals(userAccount.getAccountEnabled())) {
            throw new BadRequestException("User account is disabled");
        }

        boolean updated = false;
        if (!verifiedGoogleUser.subject().equals(userAccount.getGoogleSubjectId())) {
            userAccount.setGoogleSubjectId(verifiedGoogleUser.subject());
            updated = true;
        }

        if (!StringUtils.hasText(userAccount.getPasswordHash())) {
            userAccount.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
            updated = true;
        }

        if (!verifiedGoogleUser.email().equalsIgnoreCase(userAccount.getUniversityEmailAddress())) {
            ensureEmailIsAvailableForGoogleUser(verifiedGoogleUser.email(), userAccount.getId());
            userAccount.setUniversityEmailAddress(verifiedGoogleUser.email());
            updated = true;
        }

        if (StringUtils.hasText(verifiedGoogleUser.fullName())
                && !verifiedGoogleUser.fullName().equals(userAccount.getFullName())) {
            userAccount.setFullName(verifiedGoogleUser.fullName());
            updated = true;
        }

        if (updated) {
            return userAccountRepository.save(userAccount);
        }

        return userAccount;
    }

    private void ensureEmailIsAvailableForGoogleUser(String email, String currentUserId) {
        userAccountRepository.findByUniversityEmailAddress(email)
                .filter(existingUser -> !existingUser.getId().equals(currentUserId))
                .ifPresent(existingUser -> {
                    throw new BadRequestException("Google account email is already linked to another user");
                });
    }

    private UserAccount registerGoogleUser(VerifiedGoogleUser verifiedGoogleUser) {
        if (!StringUtils.hasText(verifiedGoogleUser.subject())) {
            throw new BadRequestException("Google account subject is missing");
        }

        String email = verifiedGoogleUser.email();
        String fullName = verifiedGoogleUser.fullName();
        String derivedName = StringUtils.hasText(fullName) ? fullName : email.substring(0, email.indexOf("@"));

        UserAccount userAccount = UserAccount.builder()
                .fullName(derivedName)
                .universityEmailAddress(email)
                .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                .contactNumber(null)
                .role(UserRole.USER)
                .googleSubjectId(verifiedGoogleUser.subject())
                .accountEnabled(true)
                .build();

        return userAccountRepository.save(userAccount);
    }
}
