package com.smartcampus.operationshub.booking.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.smartcampus.operationshub.common.enums.BookingStatus;
import com.smartcampus.operationshub.common.enums.ResourceType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponse {

    private String id;
    private String resourceId;
    private String resourceName;
    private ResourceType resourceType;
    private String resourceLocation;
    private String requestedByUserId;
    private String requestedByName;
    private LocalDate bookingDate;
    private String startTime;
    private String endTime;
    private String purpose;
    private Integer expectedAttendees;
    private BookingStatus currentStatus;
    private String decisionReason;
    private String reviewedByUserId;
    private String reviewedByName;
    private String cancelledByUserId;
    private String cancelledByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
