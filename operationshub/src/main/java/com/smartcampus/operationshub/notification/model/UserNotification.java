package com.smartcampus.operationshub.notification.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.smartcampus.operationshub.common.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_notifications")
public class UserNotification {

    @Id
    private String id;

    @Indexed
    @Field("recipient_user_id")
    private String recipientUserId;

    @Field("type")
    private NotificationType type;

    @Field("title")
    private String title;

    @Field("message")
    private String message;

    @Field("reference_type")
    private String referenceType;

    @Field("reference_id")
    private String referenceId;

    @Indexed
    @Field("is_read")
    @Builder.Default
    private boolean read = false;

    @Field("read_at")
    private LocalDateTime readAt;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
}
