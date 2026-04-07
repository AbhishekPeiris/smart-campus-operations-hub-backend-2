package com.smartcampus.operationshub.booking.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingConflictCheckResponse {

    private boolean conflict;
    private List<BookingConflictItemResponse> conflictingBookings;
}
