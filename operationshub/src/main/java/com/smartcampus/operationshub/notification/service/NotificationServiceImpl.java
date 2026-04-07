package com.smartcampus.operationshub.notification.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.common.enums.NotificationType;
import com.smartcampus.operationshub.common.exception.ResourceNotFoundException;
import com.smartcampus.operationshub.notification.dto.response.NotificationResponse;
import com.smartcampus.operationshub.notification.model.UserNotification;
import com.smartcampus.operationshub.notification.repository.UserNotificationRepository;
import com.smartcampus.operationshub.security.CurrentUserContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final CurrentUserContext currentUserContext;

    @Override
    public void notifyUser(
            String recipientUserId,
            NotificationType type,
            String title,
            String message,
            String referenceType,
            String referenceId) {
        UserNotification notification = UserNotification.builder()
                .recipientUserId(recipientUserId)
                .type(type)
                .title(title)
                .message(message)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .build();

        userNotificationRepository.save(notification);
    }

    @Override
    public PaginatedResponse<NotificationResponse> getCurrentUserNotifications(int page, int size, boolean unreadOnly) {
        String userId = currentUserContext.getCurrentUserId();
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        var notificationPage = unreadOnly
                ? userNotificationRepository.findByRecipientUserIdAndReadFalseOrderByCreatedAtDesc(userId, pageable)
                : userNotificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId, pageable);

        return PaginatedResponse.<NotificationResponse>builder()
                .content(notificationPage.getContent().stream().map(this::toResponse).toList())
                .currentPage(notificationPage.getNumber())
                .totalPages(notificationPage.getTotalPages())
                .totalElements(notificationPage.getTotalElements())
                .pageSize(notificationPage.getSize())
                .hasNext(notificationPage.hasNext())
                .hasPrevious(notificationPage.hasPrevious())
                .build();
    }

    @Override
    public long getCurrentUserUnreadCount() {
        return userNotificationRepository.countByRecipientUserIdAndReadFalse(currentUserContext.getCurrentUserId());
    }

    @Override
    public NotificationResponse getCurrentUserNotificationById(String notificationId) {
        return toResponse(getCurrentUserNotification(notificationId));
    }

    @Override
    public void markAsRead(String notificationId) {
        UserNotification notification = getCurrentUserNotification(notificationId);

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            userNotificationRepository.save(notification);
        }
    }

    @Override
    public void markAllAsRead() {
        String userId = currentUserContext.getCurrentUserId();
        var notifications = userNotificationRepository.findByRecipientUserIdAndReadFalseOrderByCreatedAtDesc(
                userId,
                PageRequest.of(0, 1000)
        ).getContent();

        if (notifications.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        notifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(now);
        });

        userNotificationRepository.saveAll(notifications);
    }

    @Override
    public void deleteNotification(String notificationId) {
        UserNotification notification = getCurrentUserNotification(notificationId);
        userNotificationRepository.deleteById(notification.getId());
    }

    private UserNotification getCurrentUserNotification(String notificationId) {
        String userId = currentUserContext.getCurrentUserId();
        UserNotification notification = userNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!userId.equals(notification.getRecipientUserId())) {
            throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
        }

        return notification;
    }

    private NotificationResponse toResponse(UserNotification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceType(notification.getReferenceType())
                .referenceId(notification.getReferenceId())
                .read(notification.isRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
