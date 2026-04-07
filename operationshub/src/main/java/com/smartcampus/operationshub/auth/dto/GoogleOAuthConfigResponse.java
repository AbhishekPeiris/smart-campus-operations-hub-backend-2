package com.smartcampus.operationshub.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleOAuthConfigResponse {

    private String provider;
    private boolean enabled;
    private String clientId;
}
