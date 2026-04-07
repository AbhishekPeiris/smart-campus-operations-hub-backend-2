package com.smartcampus.operationshub.ticket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.smartcampus.operationshub.common.enums.TicketPriorityLevel;
import com.smartcampus.operationshub.common.enums.TicketStatus;
import com.smartcampus.operationshub.ticket.model.IncidentTicket;

@Repository
public interface IncidentTicketRepository extends MongoRepository<IncidentTicket, String> {

    Optional<IncidentTicket> findByTicketReferenceNumber(String ticketReferenceNumber);

    List<IncidentTicket> findByReportedByUserId(String userId);

    List<IncidentTicket> findByAssignedTechnicianUserId(String technicianUserId);

    List<IncidentTicket> findByCurrentStatus(TicketStatus status);

    List<IncidentTicket> findByPriorityLevel(TicketPriorityLevel priorityLevel);

    List<IncidentTicket> findByCurrentStatusAndPriorityLevel(TicketStatus status, TicketPriorityLevel priorityLevel);

    List<IncidentTicket> findByAssignedTechnicianUserIdAndCurrentStatus(String technicianUserId, TicketStatus status);

    List<IncidentTicket> findByReportedByUserIdAndCurrentStatus(String userId, TicketStatus status);

    boolean existsByTicketReferenceNumber(String ticketReferenceNumber);
}