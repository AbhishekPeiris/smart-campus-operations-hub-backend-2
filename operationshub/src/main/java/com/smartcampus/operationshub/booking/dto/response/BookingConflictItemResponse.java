package com.smartcampus.operationshub.booking.dto.response;

import java.time.LocalDate;

import com.smartcampus.operationshub.common.enums.BookingStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingConflictItemResponse {

    private String bookingId;
    private String resourceId;
    private LocalDate bookingDate;
    private String startTime;
    private String endTime;
    private BookingStatus status;
    private String requestedByName;
}
