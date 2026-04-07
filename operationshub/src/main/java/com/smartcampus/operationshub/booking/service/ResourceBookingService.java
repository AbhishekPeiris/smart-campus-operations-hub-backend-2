package com.smartcampus.operationshub.booking.service;

import java.time.LocalDate;

import com.smartcampus.operationshub.booking.dto.request.CancelBookingRequest;
import com.smartcampus.operationshub.booking.dto.request.CreateBookingRequest;
import com.smartcampus.operationshub.booking.dto.request.ReviewBookingRequest;
import com.smartcampus.operationshub.booking.dto.response.BookingConflictCheckResponse;
import com.smartcampus.operationshub.booking.dto.response.BookingResponse;
import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.common.enums.BookingStatus;

public interface ResourceBookingService {

    BookingResponse createBooking(CreateBookingRequest request);

    BookingResponse reviewBooking(String bookingId, ReviewBookingRequest request);

    BookingResponse approveBooking(String bookingId, String reason);

    BookingResponse rejectBooking(String bookingId, String reason);

    BookingResponse cancelBooking(String bookingId, CancelBookingRequest request);

    BookingResponse getBookingById(String bookingId);

    PaginatedResponse<BookingResponse> getMyBookings(BookingStatus status, LocalDate bookingDate, int page, int size);

    PaginatedResponse<BookingResponse> getAllBookings(
            BookingStatus status,
            String resourceId,
            String requestedByUserId,
            LocalDate bookingDate,
            int page,
            int size);

        PaginatedResponse<BookingResponse> getBookingsByResource(
            String resourceId,
            LocalDate bookingDate,
            BookingStatus status,
            int page,
            int size);

        BookingConflictCheckResponse checkBookingConflict(
            String resourceId,
            LocalDate bookingDate,
            String startTime,
            String endTime,
            String excludeBookingId);
}
