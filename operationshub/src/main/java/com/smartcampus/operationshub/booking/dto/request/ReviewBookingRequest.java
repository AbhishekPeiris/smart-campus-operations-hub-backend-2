package com.smartcampus.operationshub.booking.dto.request;

import com.smartcampus.operationshub.common.enums.BookingStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewBookingRequest {

    @NotNull(message = "Decision status is required")
    private BookingStatus decision;

    private String reason;
}
