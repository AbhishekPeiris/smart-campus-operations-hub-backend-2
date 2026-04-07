package com.smartcampus.operationshub.booking.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.operationshub.booking.dto.request.CancelBookingRequest;
import com.smartcampus.operationshub.booking.dto.request.CreateBookingRequest;
import com.smartcampus.operationshub.booking.dto.request.ApproveBookingRequest;
import com.smartcampus.operationshub.booking.dto.request.RejectBookingRequest;
import com.smartcampus.operationshub.booking.dto.request.ReviewBookingRequest;
import com.smartcampus.operationshub.booking.dto.response.BookingConflictCheckResponse;
import com.smartcampus.operationshub.booking.dto.response.BookingResponse;
import com.smartcampus.operationshub.booking.service.ResourceBookingService;
import com.smartcampus.operationshub.common.dto.ApiSuccessResponse;
import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.common.enums.BookingStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class ResourceBookingController {

    private final ResourceBookingService resourceBookingService;

    @PostMapping
    public ApiSuccessResponse<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return ApiSuccessResponse.<BookingResponse>builder()
                .success(true)
                .message("Booking request created")
                .data(resourceBookingService.createBooking(request))
                .build();
    }

    @PatchMapping("/{bookingId}/review")
    public ApiSuccessResponse<BookingResponse> reviewBooking(
            @PathVariable String bookingId,
            @Valid @RequestBody ReviewBookingRequest request) {
        return ApiSuccessResponse.<BookingResponse>builder()
                .success(true)
                .message("Booking reviewed")
                .data(resourceBookingService.reviewBooking(bookingId, request))
                .build();
    }

    @PatchMapping("/{bookingId}/approve")
    public ApiSuccessResponse<BookingResponse> approveBooking(
            @PathVariable String bookingId,
            @RequestBody(required = false) ApproveBookingRequest request) {
        ApproveBookingRequest approveRequest = request == null ? new ApproveBookingRequest() : request;
        return ApiSuccessResponse.<BookingResponse>builder()
                .success(true)
                .message("Booking approved")
                .data(resourceBookingService.approveBooking(bookingId, approveRequest.getReason()))
                .build();
    }

    @PatchMapping("/{bookingId}/reject")
    public ApiSuccessResponse<BookingResponse> rejectBooking(
            @PathVariable String bookingId,
            @Valid @RequestBody RejectBookingRequest request) {
        return ApiSuccessResponse.<BookingResponse>builder()
                .success(true)
                .message("Booking rejected")
                .data(resourceBookingService.rejectBooking(bookingId, request.getReason()))
                .build();
    }

    @PatchMapping("/{bookingId}/cancel")
    public ApiSuccessResponse<BookingResponse> cancelBooking(
            @PathVariable String bookingId,
            @RequestBody(required = false) CancelBookingRequest request) {
        CancelBookingRequest cancelRequest = request == null ? new CancelBookingRequest() : request;
        return ApiSuccessResponse.<BookingResponse>builder()
                .success(true)
                .message("Booking cancelled")
                .data(resourceBookingService.cancelBooking(bookingId, cancelRequest))
                .build();
    }

    @GetMapping("/{bookingId}")
    public ApiSuccessResponse<BookingResponse> getBookingById(@PathVariable String bookingId) {
        return ApiSuccessResponse.<BookingResponse>builder()
                .success(true)
                .message("Booking retrieved")
                .data(resourceBookingService.getBookingById(bookingId))
                .build();
    }

    @GetMapping("/my")
    public ApiSuccessResponse<PaginatedResponse<BookingResponse>> getMyBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiSuccessResponse.<PaginatedResponse<BookingResponse>>builder()
                .success(true)
                .message("My bookings retrieved")
                .data(resourceBookingService.getMyBookings(status, bookingDate, page, size))
                .build();
    }

    @GetMapping
    public ApiSuccessResponse<PaginatedResponse<BookingResponse>> getAllBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) String requestedByUserId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiSuccessResponse.<PaginatedResponse<BookingResponse>>builder()
                .success(true)
                .message("Bookings retrieved")
                .data(resourceBookingService.getAllBookings(status, resourceId, requestedByUserId, bookingDate, page, size))
                .build();
    }

        @GetMapping("/resource/{resourceId}")
        public ApiSuccessResponse<PaginatedResponse<BookingResponse>> getBookingsByResource(
                        @PathVariable String resourceId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate,
                        @RequestParam(required = false) BookingStatus status,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return ApiSuccessResponse.<PaginatedResponse<BookingResponse>>builder()
                                .success(true)
                                .message("Resource bookings retrieved")
                                .data(resourceBookingService.getBookingsByResource(resourceId, bookingDate, status, page, size))
                                .build();
        }

        @GetMapping("/conflicts")
        public ApiSuccessResponse<BookingConflictCheckResponse> checkBookingConflict(
                        @RequestParam String resourceId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate,
                        @RequestParam String startTime,
                        @RequestParam String endTime,
                        @RequestParam(required = false) String excludeBookingId) {
                return ApiSuccessResponse.<BookingConflictCheckResponse>builder()
                                .success(true)
                                .message("Booking conflict check completed")
                                .data(resourceBookingService.checkBookingConflict(resourceId, bookingDate, startTime, endTime, excludeBookingId))
                                .build();
        }
}
