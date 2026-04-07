package com.smartcampus.operationshub.ticket.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.smartcampus.operationshub.ticket.model.TechnicianUpdateLog;

@Repository
public interface TechnicianUpdateLogRepository extends MongoRepository<TechnicianUpdateLog, String> {

    List<TechnicianUpdateLog> findByTicketIdOrderByUpdatedAtDesc(String ticketId);

    List<TechnicianUpdateLog> findByTechnicianUserId(String technicianUserId);

    void deleteByTicketId(String ticketId);
}