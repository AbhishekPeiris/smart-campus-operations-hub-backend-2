package com.smartcampus.operationshub.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smartcampus.operationshub.user.model.UserAccount;
import com.smartcampus.operationshub.user.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUniversityEmailAddress(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        String password = StringUtils.hasText(userAccount.getPasswordHash()) ? userAccount.getPasswordHash() : "oauth2-user";

        return new User(
                userAccount.getUniversityEmailAddress(),
            password,
                Boolean.TRUE.equals(userAccount.getAccountEnabled()),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_" + userAccount.getRole().name()))
        );
    }
}