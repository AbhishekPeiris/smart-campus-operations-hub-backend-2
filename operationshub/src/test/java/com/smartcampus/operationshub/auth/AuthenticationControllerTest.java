package com.smartcampus.operationshub.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.operationshub.auth.controller.AuthenticationController;
import com.smartcampus.operationshub.auth.dto.GoogleLoginRequest;
import com.smartcampus.operationshub.auth.dto.GoogleOAuthConfigResponse;
import com.smartcampus.operationshub.auth.dto.LoginRequest;
import com.smartcampus.operationshub.auth.dto.LoginResponse;
import com.smartcampus.operationshub.auth.service.AuthenticationService;
import com.smartcampus.operationshub.common.enums.UserRole;
import com.smartcampus.operationshub.common.exception.GlobalExceptionHandler;
import com.smartcampus.operationshub.security.AuthEntryPointJwt;
import com.smartcampus.operationshub.security.CustomUserDetailsService;
import com.smartcampus.operationshub.security.JwtAuthenticationFilter;
import com.smartcampus.operationshub.security.JwtService;

@WebMvcTest(value = AuthenticationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(GlobalExceptionHandler.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    @DisplayName("Should accept login endpoint request")
    void shouldAcceptLoginRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUniversityEmailAddress("user@smartcampus.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should delegate login to authentication service")
    void shouldDelegateLoginToService() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUniversityEmailAddress("user@smartcampus.com");
        request.setPassword("password123");

        LoginResponse response = LoginResponse.builder()
                .accessToken("token-value")
                .tokenType("Bearer")
                .userId("user-001")
                .fullName("Test User")
                .universityEmailAddress("user@smartcampus.com")
                .role(UserRole.USER)
                .build();

        Mockito.when(authenticationService.login(Mockito.any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 200 when login request is valid")
    void shouldReturnOkWhenLoginRequestValid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUniversityEmailAddress("user@smartcampus.com");
        request.setPassword("password123");

        LoginResponse response = LoginResponse.builder()
                .accessToken("token-value")
                .tokenType("Bearer")
                .userId("user-001")
                .fullName("Test User")
                .universityEmailAddress("user@smartcampus.com")
                .role(UserRole.USER)
                .build();

        Mockito.when(authenticationService.login(Mockito.any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return Google OAuth config for the frontend")
    void shouldReturnGoogleOAuthConfig() throws Exception {
        Mockito.when(authenticationService.getGoogleOAuthConfig()).thenReturn(
                GoogleOAuthConfigResponse.builder()
                        .provider("google")
                        .enabled(true)
                        .clientId("frontend-client-id")
                        .build());

        mockMvc.perform(get("/api/v1/auth/oauth/google/config"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should accept Google login request")
    void shouldAcceptGoogleLoginRequest() throws Exception {
        GoogleLoginRequest request = new GoogleLoginRequest();
        request.setIdToken("google-id-token");

        LoginResponse response = LoginResponse.builder()
                .accessToken("token-value")
                .tokenType("Bearer")
                .userId("user-001")
                .fullName("Google User")
                .universityEmailAddress("user@smartcampus.com")
                .role(UserRole.USER)
                .build();

        Mockito.when(authenticationService.loginWithGoogle(Mockito.any(GoogleLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
