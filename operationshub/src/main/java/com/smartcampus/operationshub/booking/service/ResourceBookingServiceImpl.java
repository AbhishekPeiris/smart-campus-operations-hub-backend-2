package com.smartcampus.operationshub.booking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smartcampus.operationshub.booking.dto.request.CancelBookingRequest;
import com.smartcampus.operationshub.booking.dto.request.CreateBookingRequest;
import com.smartcampus.operationshub.booking.dto.request.ReviewBookingRequest;
import com.smartcampus.operationshub.booking.dto.response.BookingConflictCheckResponse;
import com.smartcampus.operationshub.booking.dto.response.BookingConflictItemResponse;
import com.smartcampus.operationshub.booking.dto.response.BookingResponse;
import com.smartcampus.operationshub.booking.model.ResourceBooking;
import com.smartcampus.operationshub.booking.repository.ResourceBookingRepository;
import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.common.enums.BookingStatus;
import com.smartcampus.operationshub.common.enums.NotificationType;
import com.smartcampus.operationshub.common.enums.ResourceStatus;
import com.smartcampus.operationshub.common.enums.UserRole;
import com.smartcampus.operationshub.common.exception.BadRequestException;
import com.smartcampus.operationshub.common.exception.ForbiddenOperationException;
import com.smartcampus.operationshub.common.exception.ResourceNotFoundException;
import com.smartcampus.operationshub.notification.service.NotificationService;
import com.smartcampus.operationshub.resource.model.CampusResource;
import com.smartcampus.operationshub.resource.repository.CampusResourceRepository;
import com.smartcampus.operationshub.security.CurrentUserContext;
import com.smartcampus.operationshub.user.model.UserAccount;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResourceBookingServiceImpl implements ResourceBookingService {

    private final ResourceBookingRepository resourceBookingRepository;
    private final CampusResourceRepository campusResourceRepository;
    private final CurrentUserContext currentUserContext;
    private final NotificationService notificationService;
    private final MongoTemplate mongoTemplate;

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
        UserAccount currentUser = currentUserContext.getCurrentUser();
        CampusResource resource = findResourceById(request.getResourceId());

        if (resource.getStatus() != ResourceStatus.ACTIVE) {
            throw new BadRequestException("Bookings are only allowed for ACTIVE resources");
        }

        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateAttendeeCapacity(request.getExpectedAttendees(), resource.getCapacity());

        if (hasOverlap(resource.getId(), request.getBookingDate(), request.getStartTime(), request.getEndTime(),
                EnumSet.of(BookingStatus.PENDING, BookingStatus.APPROVED), null)) {
            throw new BadRequestException("Requested booking time conflicts with an existing booking");
        }

        ResourceBooking booking = ResourceBooking.builder()
                .resourceId(resource.getId())
                .resourceName(resource.getResourceName())
                .resourceType(resource.getResourceType())
                .resourceLocation(resource.getLocation())
                .requestedByUserId(currentUser.getId())
                .requestedByName(currentUser.getFullName())
                .bookingDate(request.getBookingDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .purpose(request.getPurpose())
                .expectedAttendees(request.getExpectedAttendees())
                .currentStatus(BookingStatus.PENDING)
                .build();

        return toResponse(resourceBookingRepository.save(booking));
    }

    @Override
    public BookingResponse reviewBooking(String bookingId, ReviewBookingRequest request) {
        UserAccount admin = currentUserContext.getCurrentUser();
        ResourceBooking booking = findBookingById(bookingId);

        if (booking.getCurrentStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only PENDING bookings can be reviewed");
        }

        if (request.getDecision() != BookingStatus.APPROVED && request.getDecision() != BookingStatus.REJECTED) {
            throw new BadRequestException("Review decision must be APPROVED or REJECTED");
        }

        if (request.getDecision() == BookingStatus.REJECTED && !StringUtils.hasText(request.getReason())) {
            throw new BadRequestException("Rejection reason is required");
        }

        if (request.getDecision() == BookingStatus.APPROVED) {
            if (hasOverlap(
                    booking.getResourceId(),
                    booking.getBookingDate(),
                    booking.getStartTime(),
                    booking.getEndTime(),
                    EnumSet.of(BookingStatus.APPROVED),
                    booking.getId())) {
                throw new BadRequestException("Booking cannot be approved due to a scheduling conflict");
            }
        }

        booking.setCurrentStatus(request.getDecision());
        booking.setDecisionReason(StringUtils.hasText(request.getReason()) ? request.getReason().trim() : null);
        booking.setReviewedByUserId(admin.getId());
        booking.setReviewedByName(admin.getFullName());

        ResourceBooking saved = resourceBookingRepository.save(booking);

        if (saved.getCurrentStatus() == BookingStatus.APPROVED) {
            notificationService.notifyUser(
                    saved.getRequestedByUserId(),
                    NotificationType.BOOKING_APPROVED,
                    "Booking Approved",
                    "Your booking for " + saved.getResourceName() + " on " + saved.getBookingDate() + " has been approved.",
                    "BOOKING",
                    saved.getId());
        } else {
            notificationService.notifyUser(
                    saved.getRequestedByUserId(),
                    NotificationType.BOOKING_REJECTED,
                    "Booking Rejected",
                    "Your booking for " + saved.getResourceName() + " on " + saved.getBookingDate() + " was rejected."
                            + (saved.getDecisionReason() == null ? "" : " Reason: " + saved.getDecisionReason()),
                    "BOOKING",
                    saved.getId());
        }

        return toResponse(saved);
    }

    @Override
    public BookingResponse approveBooking(String bookingId, String reason) {
        ReviewBookingRequest request = new ReviewBookingRequest();
        request.setDecision(BookingStatus.APPROVED);
        request.setReason(reason);
        return reviewBooking(bookingId, request);
    }

    @Override
    public BookingResponse rejectBooking(String bookingId, String reason) {
        ReviewBookingRequest request = new ReviewBookingRequest();
        request.setDecision(BookingStatus.REJECTED);
        request.setReason(reason);
        return reviewBooking(bookingId, request);
    }

    @Override
    public BookingResponse cancelBooking(String bookingId, CancelBookingRequest request) {
        UserAccount currentUser = currentUserContext.getCurrentUser();
        ResourceBooking booking = findBookingById(bookingId);

        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isOwner = currentUser.getId().equals(booking.getRequestedByUserId());

        if (!isAdmin && !isOwner) {
            throw new ForbiddenOperationException("You can only cancel your own bookings");
        }

        if (booking.getCurrentStatus() != BookingStatus.APPROVED) {
            throw new BadRequestException("Only APPROVED bookings can be cancelled");
        }

        booking.setCurrentStatus(BookingStatus.CANCELLED);
        booking.setDecisionReason(StringUtils.hasText(request.getReason()) ? request.getReason().trim() : null);
        booking.setCancelledByUserId(currentUser.getId());
        booking.setCancelledByName(currentUser.getFullName());

        return toResponse(resourceBookingRepository.save(booking));
    }

    @Override
    public BookingResponse getBookingById(String bookingId) {
        UserAccount currentUser = currentUserContext.getCurrentUser();
        ResourceBooking booking = findBookingById(bookingId);

        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isOwner = currentUser.getId().equals(booking.getRequestedByUserId());

        if (!isAdmin && !isOwner) {
            throw new ForbiddenOperationException("You do not have access to this booking");
        }

        return toResponse(booking);
    }

    @Override
    public PaginatedResponse<BookingResponse> getMyBookings(
            BookingStatus status,
            LocalDate bookingDate,
            int page,
            int size) {

        String userId = currentUserContext.getCurrentUserId();

        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("requested_by_user_id").is(userId));

        if (status != null) {
            criteriaList.add(Criteria.where("current_status").is(status));
        }
        if (bookingDate != null) {
            criteriaList.add(Criteria.where("booking_date").is(bookingDate));
        }

        return findWithPagination(criteriaList, page, size);
    }

    @Override
    public PaginatedResponse<BookingResponse> getAllBookings(
            BookingStatus status,
            String resourceId,
            String requestedByUserId,
            LocalDate bookingDate,
            int page,
            int size) {

        if (currentUserContext.getCurrentUser().getRole() != UserRole.ADMIN) {
            throw new ForbiddenOperationException("Only ADMIN can view all bookings");
        }

        List<Criteria> criteriaList = new ArrayList<>();

        if (status != null) {
            criteriaList.add(Criteria.where("current_status").is(status));
        }
        if (StringUtils.hasText(resourceId)) {
            criteriaList.add(Criteria.where("resource_id").is(resourceId.trim()));
        }
        if (StringUtils.hasText(requestedByUserId)) {
            criteriaList.add(Criteria.where("requested_by_user_id").is(requestedByUserId.trim()));
        }
        if (bookingDate != null) {
            criteriaList.add(Criteria.where("booking_date").is(bookingDate));
        }

        return findWithPagination(criteriaList, page, size);
    }

    @Override
    public PaginatedResponse<BookingResponse> getBookingsByResource(
            String resourceId,
            LocalDate bookingDate,
            BookingStatus status,
            int page,
            int size) {
        findResourceById(resourceId);

        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("resource_id").is(resourceId));

        if (bookingDate != null) {
            criteriaList.add(Criteria.where("booking_date").is(bookingDate));
        }
        if (status != null) {
            criteriaList.add(Criteria.where("current_status").is(status));
        }

        return findWithPagination(criteriaList, page, size);
    }

    @Override
    public BookingConflictCheckResponse checkBookingConflict(
            String resourceId,
            LocalDate bookingDate,
            String startTime,
            String endTime,
            String excludeBookingId) {
        findResourceById(resourceId);
        validateTimeRange(startTime, endTime);

        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("resource_id").is(resourceId));
        criteriaList.add(Criteria.where("booking_date").is(bookingDate));
        criteriaList.add(Criteria.where("current_status").in(EnumSet.of(BookingStatus.PENDING, BookingStatus.APPROVED)));
        criteriaList.add(Criteria.where("start_time").lt(endTime));
        criteriaList.add(Criteria.where("end_time").gt(startTime));

        if (StringUtils.hasText(excludeBookingId)) {
            criteriaList.add(Criteria.where("_id").ne(excludeBookingId));
        }

        Query query = new Query(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        List<ResourceBooking> conflicting = mongoTemplate.find(query, ResourceBooking.class);

        return BookingConflictCheckResponse.builder()
                .conflict(!conflicting.isEmpty())
                .conflictingBookings(conflicting.stream().map(this::toConflictItem).toList())
                .build();
    }

    private PaginatedResponse<BookingResponse> findWithPagination(List<Criteria> criteriaList, int page, int size) {
        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, ResourceBooking.class);
        query.with(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<BookingResponse> content = mongoTemplate.find(query, ResourceBooking.class).stream()
                .map(this::toResponse)
                .toList();

        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);

        return PaginatedResponse.<BookingResponse>builder()
                .content(content)
                .currentPage(page)
                .totalPages(totalPages)
                .totalElements(total)
                .pageSize(size)
                .hasNext(page + 1 < totalPages)
                .hasPrevious(page > 0)
                .build();
    }

    private boolean hasOverlap(
            String resourceId,
            LocalDate bookingDate,
            String startTime,
            String endTime,
            EnumSet<BookingStatus> statuses,
            String excludeBookingId) {

        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("resource_id").is(resourceId));
        criteriaList.add(Criteria.where("booking_date").is(bookingDate));
        criteriaList.add(Criteria.where("current_status").in(statuses));
        criteriaList.add(Criteria.where("start_time").lt(endTime));
        criteriaList.add(Criteria.where("end_time").gt(startTime));

        if (StringUtils.hasText(excludeBookingId)) {
            criteriaList.add(Criteria.where("_id").ne(excludeBookingId));
        }

        Query query = new Query(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        return mongoTemplate.exists(query, ResourceBooking.class);
    }

    private void validateTimeRange(String startTime, String endTime) {
        LocalTime start = parseTime(startTime, "start time");
        LocalTime end = parseTime(endTime, "end time");

        if (!end.isAfter(start)) {
            throw new BadRequestException("End time must be after start time");
        }
    }

    private LocalTime parseTime(String value, String fieldName) {
        try {
            return LocalTime.parse(value);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid " + fieldName + ": " + value);
        }
    }

    private void validateAttendeeCapacity(Integer expectedAttendees, Integer resourceCapacity) {
        if (expectedAttendees == null || resourceCapacity == null) {
            return;
        }

        if (expectedAttendees > resourceCapacity) {
            throw new BadRequestException("Expected attendees exceed resource capacity");
        }
    }

    private CampusResource findResourceById(String resourceId) {
        return campusResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));
    }

    private ResourceBooking findBookingById(String bookingId) {
        return resourceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    private BookingResponse toResponse(ResourceBooking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .resourceId(booking.getResourceId())
                .resourceName(booking.getResourceName())
                .resourceType(booking.getResourceType())
                .resourceLocation(booking.getResourceLocation())
                .requestedByUserId(booking.getRequestedByUserId())
                .requestedByName(booking.getRequestedByName())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .purpose(booking.getPurpose())
                .expectedAttendees(booking.getExpectedAttendees())
                .currentStatus(booking.getCurrentStatus())
                .decisionReason(booking.getDecisionReason())
                .reviewedByUserId(booking.getReviewedByUserId())
                .reviewedByName(booking.getReviewedByName())
                .cancelledByUserId(booking.getCancelledByUserId())
                .cancelledByName(booking.getCancelledByName())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    private BookingConflictItemResponse toConflictItem(ResourceBooking booking) {
        return BookingConflictItemResponse.builder()
                .bookingId(booking.getId())
                .resourceId(booking.getResourceId())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getCurrentStatus())
                .requestedByName(booking.getRequestedByName())
                .build();
    }
}
