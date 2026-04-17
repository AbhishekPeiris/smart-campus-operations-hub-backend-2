package com.smartcampus.operationshub.auth.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.StringUtils;

@Configuration
public class GoogleOAuthConfig {

    private static final String GOOGLE_JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs";
    private static final List<String> VALID_GOOGLE_ISSUERS = List.of(
            "https://accounts.google.com",
            "accounts.google.com");

    @Bean("googleIdTokenDecoder")
    public JwtDecoder googleIdTokenDecoder(GoogleOAuthProperties googleOAuthProperties) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(GOOGLE_JWK_SET_URI).build();
        decoder.setJwtValidator(googleJwtValidator(googleOAuthProperties));
        return decoder;
    }

    private OAuth2TokenValidator<Jwt> googleJwtValidator(GoogleOAuthProperties googleOAuthProperties) {
        OAuth2TokenValidator<Jwt> timestampValidator = JwtValidators.createDefault();
        OAuth2TokenValidator<Jwt> issuerValidator = jwt -> {
            String issuer = jwt.getClaimAsString("iss");
            if (VALID_GOOGLE_ISSUERS.contains(issuer)) {
                return OAuth2TokenValidatorResult.success();
            }

            return OAuth2TokenValidatorResult.failure(new OAuth2Error(
                    "invalid_token",
                    "Google token issuer is invalid",
                    null));
        };

        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> {
            String configuredClientId = googleOAuthProperties.getGoogleClientId();
            if (!StringUtils.hasText(configuredClientId)) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error(
                        "invalid_token",
                        "Google OAuth is not configured on the server",
                        null));
            }

            if (jwt.getAudience().contains(configuredClientId)) {
                return OAuth2TokenValidatorResult.success();
            }

            return OAuth2TokenValidatorResult.failure(new OAuth2Error(
                    "invalid_token",
                    "Google token audience is invalid",
                    null));
        };

        return token -> {
            OAuth2TokenValidatorResult timestampResult = timestampValidator.validate(token);
            if (timestampResult.hasErrors()) {
                return timestampResult;
            }

            OAuth2TokenValidatorResult issuerResult = issuerValidator.validate(token);
            if (issuerResult.hasErrors()) {
                return issuerResult;
            }

            return audienceValidator.validate(token);
        };
    }
}
