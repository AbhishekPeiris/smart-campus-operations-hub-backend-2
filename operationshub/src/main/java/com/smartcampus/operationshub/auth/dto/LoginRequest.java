package com.smartcampus.operationshub.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String universityEmailAddress;

    @NotBlank(message = "Password is required")
    private String password;
}