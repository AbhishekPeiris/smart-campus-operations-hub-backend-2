package com.smartcampus.operationshub.ticket.service;

import java.util.List;

import com.smartcampus.operationshub.common.enums.TicketStatus;
import com.smartcampus.operationshub.ticket.dto.response.TechnicianUpdateLogResponse;

public interface TechnicianTicketUpdateService {

    /**
     * Log technician update
     */
    void logUpdate(
            String ticketId,
            String technicianId,
            String technicianName,
            TicketStatus previousStatus,
            TicketStatus newStatus,
            String message
    );

    /**
     * Get all updates for a ticket
     */
    List<TechnicianUpdateLogResponse> getUpdatesByTicket(String ticketId);
}