package com.smartcampus.operationshub.auth.service;

import com.smartcampus.operationshub.auth.dto.LoginRequest;
import com.smartcampus.operationshub.auth.dto.LoginResponse;
import com.smartcampus.operationshub.auth.dto.RegisterUserRequest;

public interface AuthenticationService {

    /**
     * Register a new user
     */
    void registerUser(RegisterUserRequest request);

    /**
     * Login user and return JWT response
     */
    LoginResponse login(LoginRequest request);
}