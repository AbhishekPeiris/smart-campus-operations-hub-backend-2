package com.smartcampus.operationshub.booking.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.smartcampus.operationshub.common.enums.BookingStatus;
import com.smartcampus.operationshub.common.enums.ResourceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "resource_bookings")
public class ResourceBooking {

    @Id
    private String id;

    @Indexed
    @Field("resource_id")
    private String resourceId;

    @Field("resource_name")
    private String resourceName;

    @Field("resource_type")
    private ResourceType resourceType;

    @Field("resource_location")
    private String resourceLocation;

    @Indexed
    @Field("requested_by_user_id")
    private String requestedByUserId;

    @Field("requested_by_name")
    private String requestedByName;

    @Indexed
    @Field("booking_date")
    private LocalDate bookingDate;

    @Field("start_time")
    private String startTime;

    @Field("end_time")
    private String endTime;

    @Field("purpose")
    private String purpose;

    @Field("expected_attendees")
    private Integer expectedAttendees;

    @Indexed
    @Field("current_status")
    @Builder.Default
    private BookingStatus currentStatus = BookingStatus.PENDING;

    @Field("decision_reason")
    private String decisionReason;

    @Field("reviewed_by_user_id")
    private String reviewedByUserId;

    @Field("reviewed_by_name")
    private String reviewedByName;

    @Field("cancelled_by_user_id")
    private String cancelledByUserId;

    @Field("cancelled_by_name")
    private String cancelledByName;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
