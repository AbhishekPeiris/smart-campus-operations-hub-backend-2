package com.smartcampus.operationshub.booking.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.smartcampus.operationshub.booking.model.ResourceBooking;
import com.smartcampus.operationshub.common.enums.BookingStatus;

@Repository
public interface ResourceBookingRepository extends MongoRepository<ResourceBooking, String> {

    List<ResourceBooking> findByRequestedByUserIdOrderByCreatedAtDesc(String requestedByUserId);

    List<ResourceBooking> findByRequestedByUserIdAndCurrentStatusOrderByCreatedAtDesc(
            String requestedByUserId,
            BookingStatus currentStatus);
}
