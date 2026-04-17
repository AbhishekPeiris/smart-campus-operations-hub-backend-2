package com.smartcampus.operationshub.auth.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smartcampus.operationshub.auth.config.GoogleOAuthProperties;
import com.smartcampus.operationshub.common.exception.BadRequestException;

@Service
public class GoogleTokenVerifierService {

    private final GoogleOAuthProperties googleOAuthProperties;
    private final JwtDecoder googleIdTokenDecoder;

    public GoogleTokenVerifierService(
            GoogleOAuthProperties googleOAuthProperties,
            @Qualifier("googleIdTokenDecoder") JwtDecoder googleIdTokenDecoder
    ) {
        this.googleOAuthProperties = googleOAuthProperties;
        this.googleIdTokenDecoder = googleIdTokenDecoder;
    }

    public VerifiedGoogleUser verify(String idToken) {
        if (!StringUtils.hasText(googleOAuthProperties.getGoogleClientId())) {
            throw new BadRequestException("Google OAuth is not configured on the server");
        }

        Jwt jwt;
        try {
            jwt = googleIdTokenDecoder.decode(idToken);
        } catch (BadJwtException ex) {
            throw new BadRequestException(ex.getMessage());
        } catch (RuntimeException ex) {
            throw new BadRequestException("Invalid Google ID token");
        }

        String email = jwt.getClaimAsString("email");
        String fullName = jwt.getClaimAsString("name");
        String subject = jwt.getSubject();
        String hostedDomain = jwt.getClaimAsString("hd");

        if (!Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified"))) {
            throw new BadRequestException("Google account email is not verified");
        }
        if (!StringUtils.hasText(email)) {
            throw new BadRequestException("Google account email is missing");
        }
        if (!StringUtils.hasText(subject)) {
            throw new BadRequestException("Google account subject is missing");
        }

        String requiredHostedDomain = googleOAuthProperties.getGoogleHostedDomain();
        if (StringUtils.hasText(requiredHostedDomain)
                && !requiredHostedDomain.equalsIgnoreCase(hostedDomain)) {
            throw new BadRequestException("Google account is not part of the allowed hosted domain");
        }

        return new VerifiedGoogleUser(subject, email, fullName, hostedDomain);
    }
}
