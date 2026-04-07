package com.smartcampus.operationshub.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartcampus.operationshub.auth.dto.LoginRequest;
import com.smartcampus.operationshub.auth.dto.LoginResponse;
import com.smartcampus.operationshub.auth.dto.RegisterUserRequest;
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
}