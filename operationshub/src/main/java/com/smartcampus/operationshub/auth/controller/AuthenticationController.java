package com.smartcampus.operationshub.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.operationshub.auth.dto.LoginRequest;
import com.smartcampus.operationshub.auth.dto.LoginResponse;
import com.smartcampus.operationshub.auth.dto.GoogleLoginRequest;
import com.smartcampus.operationshub.auth.dto.GoogleOAuthConfigResponse;
import com.smartcampus.operationshub.auth.dto.RegisterUserRequest;
import com.smartcampus.operationshub.auth.service.AuthenticationService;
import com.smartcampus.operationshub.common.dto.ApiSuccessResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApiSuccessResponse<String> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        authenticationService.registerUser(request);
        return ApiSuccessResponse.<String>builder()
                .success(true)
                .message("User registered successfully")
                .data(null)
                .build();
    }

    @PostMapping("/login")
    public ApiSuccessResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authenticationService.login(request);
        return ApiSuccessResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build();
    }

    @PostMapping("/google")
    public ApiSuccessResponse<LoginResponse> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        LoginResponse response = authenticationService.loginWithGoogle(request);
        return ApiSuccessResponse.<LoginResponse>builder()
                .success(true)
                .message("Google login successful")
                .data(response)
                .build();
    }

    @GetMapping("/oauth/google/config")
    public ApiSuccessResponse<GoogleOAuthConfigResponse> getGoogleOAuthConfig() {
        return ApiSuccessResponse.<GoogleOAuthConfigResponse>builder()
                .success(true)
                .message("Google OAuth configuration retrieved")
                .data(authenticationService.getGoogleOAuthConfig())
                .build();
    }
}