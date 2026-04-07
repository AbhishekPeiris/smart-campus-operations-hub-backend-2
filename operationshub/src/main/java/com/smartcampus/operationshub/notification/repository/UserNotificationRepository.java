package com.smartcampus.operationshub.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.smartcampus.operationshub.notification.model.UserNotification;

@Repository
public interface UserNotificationRepository extends MongoRepository<UserNotification, String> {

    Page<UserNotification> findByRecipientUserIdOrderByCreatedAtDesc(String recipientUserId, Pageable pageable);

    Page<UserNotification> findByRecipientUserIdAndReadFalseOrderByCreatedAtDesc(String recipientUserId, Pageable pageable);

    long countByRecipientUserIdAndReadFalse(String recipientUserId);
}
