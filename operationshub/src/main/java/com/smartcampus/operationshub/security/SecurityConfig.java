package com.smartcampus.operationshub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthEntryPointJwt authEntryPointJwt;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/users/role/TECHNICIAN")
                        .hasAnyRole("ADMIN", "TECHNICIAN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/tickets").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tickets/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tickets/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/tickets/*/assign").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/tickets/*/reject").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/tickets/*/status").hasAnyRole("TECHNICIAN", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/tickets/*/resolve")
                        .hasAnyRole("TECHNICIAN", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/tickets/attachments")
                        .hasAnyRole("USER", "TECHNICIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tickets/attachments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tickets/attachments/**")
                        .hasAnyRole("ADMIN", "TECHNICIAN", "USER")

                        .requestMatchers(HttpMethod.POST, "/api/v1/tickets/comments")
                        .hasAnyRole("USER", "TECHNICIAN", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tickets/comments/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tickets/comments/**")
                        .hasAnyRole("USER", "TECHNICIAN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tickets/comments/**")
                        .hasAnyRole("USER", "TECHNICIAN", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/tickets/updates/**").authenticated()

                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}