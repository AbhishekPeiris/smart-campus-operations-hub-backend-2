package com.smartcampus.operationshub.notification.service;

import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.common.enums.NotificationType;
import com.smartcampus.operationshub.notification.dto.response.NotificationResponse;

public interface NotificationService {

    void notifyUser(
            String recipientUserId,
            NotificationType type,
            String title,
            String message,
            String referenceType,
            String referenceId);

    PaginatedResponse<NotificationResponse> getCurrentUserNotifications(int page, int size, boolean unreadOnly);

    long getCurrentUserUnreadCount();

    NotificationResponse getCurrentUserNotificationById(String notificationId);

    void markAsRead(String notificationId);

    void markAllAsRead();

    void deleteNotification(String notificationId);
}
