package com.smartcampus.operationshub.notification.dto.response;

import java.time.LocalDateTime;

import com.smartcampus.operationshub.common.enums.NotificationType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {

    private String id;
    private NotificationType type;
    private String title;
    private String message;
    private String referenceType;
    private String referenceId;
    private boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
