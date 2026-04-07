package com.smartcampus.operationshub.notification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.operationshub.common.dto.ApiSuccessResponse;
import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.notification.dto.response.NotificationResponse;
import com.smartcampus.operationshub.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiSuccessResponse<PaginatedResponse<NotificationResponse>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        return ApiSuccessResponse.<PaginatedResponse<NotificationResponse>>builder()
                .success(true)
                .message("Notifications retrieved")
                .data(notificationService.getCurrentUserNotifications(page, size, unreadOnly))
                .build();
    }

    @GetMapping("/unread-count")
    public ApiSuccessResponse<Long> getUnreadCount() {
        return ApiSuccessResponse.<Long>builder()
                .success(true)
                .message("Unread notification count retrieved")
                .data(notificationService.getCurrentUserUnreadCount())
                .build();
    }

    @GetMapping("/{notificationId}")
    public ApiSuccessResponse<NotificationResponse> getNotificationById(@PathVariable String notificationId) {
        return ApiSuccessResponse.<NotificationResponse>builder()
                .success(true)
                .message("Notification retrieved")
                .data(notificationService.getCurrentUserNotificationById(notificationId))
                .build();
    }

    @PatchMapping("/{notificationId}/read")
    public ApiSuccessResponse<String> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Notification marked as read")
                .data(null)
                .build();
    }

    @PatchMapping("/read-all")
    public ApiSuccessResponse<String> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiSuccessResponse.<String>builder()
                .success(true)
                .message("All notifications marked as read")
                .data(null)
                .build();
    }

    @DeleteMapping("/{notificationId}")
    public ApiSuccessResponse<String> deleteNotification(@PathVariable String notificationId) {
        notificationService.deleteNotification(notificationId);
        return ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Notification deleted")
                .data(null)
                .build();
    }
}
