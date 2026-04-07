package com.smartcampus.operationshub.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
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

    private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token={idToken}";

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${application.security.oauth.google-client-id:}")
    private String googleClientId;

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
        if (!StringUtils.hasText(googleClientId)) {
            throw new BadRequestException("Google OAuth is not configured on the server");
        }

        JsonNode tokenInfo = fetchGoogleTokenInfo(request.getIdToken());

        String audience = tokenInfo.path("aud").asText();
        String email = tokenInfo.path("email").asText();
        String fullName = tokenInfo.path("name").asText();
        boolean emailVerified = tokenInfo.path("email_verified").asBoolean(false);

        if (!googleClientId.equals(audience)) {
            throw new BadRequestException("Google token audience is invalid");
        }
        if (!emailVerified || !StringUtils.hasText(email)) {
            throw new BadRequestException("Google account email is not verified");
        }

        UserAccount userAccount = userAccountRepository.findByUniversityEmailAddress(email)
                .orElseGet(() -> registerGoogleUser(email, fullName));

        if (!Boolean.TRUE.equals(userAccount.getAccountEnabled())) {
            throw new BadRequestException("User account is disabled");
        }

        UserDetails userDetails = User.builder()
                .username(userAccount.getUniversityEmailAddress())
                .password("oauth2-user")
                .authorities(new SimpleGrantedAuthority("ROLE_" + userAccount.getRole().name()))
                .build();

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
                .enabled(StringUtils.hasText(googleClientId))
                .clientId(googleClientId)
                .build();
    }

    private JsonNode fetchGoogleTokenInfo(String idToken) {
        try {
            return RestClient.create()
                    .get()
                    .uri(GOOGLE_TOKEN_INFO_URL, idToken)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid Google ID token");
        }
    }

    private UserAccount registerGoogleUser(String email, String fullName) {
        String derivedName = StringUtils.hasText(fullName) ? fullName : email.substring(0, email.indexOf("@"));

        UserAccount userAccount = UserAccount.builder()
                .fullName(derivedName)
                .universityEmailAddress(email)
                .passwordHash(null)
                .contactNumber(null)
                .role(UserRole.USER)
                .accountEnabled(true)
                .build();

        return userAccountRepository.save(userAccount);
    }
}