package com.smartcampus.operationshub.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "application.security.oauth")
public class GoogleOAuthProperties {

    private String googleClientId = "";
    private String googleHostedDomain = "";
}
