package com.smartcampus.operationshub.ticket.dto.response;

import java.time.LocalDateTime;

import com.smartcampus.operationshub.common.enums.TicketPriorityLevel;
import com.smartcampus.operationshub.common.enums.TicketStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IncidentTicketResponse {

    private String id;
    private String ticketCode; // e.g., INC-0001

    private String ticketTitle;

    private TicketStatus status;
    private TicketPriorityLevel priorityLevel;

    private String createdByUserId;
    private String createdByName;

    private String assignedTechnicianId;
    private String assignedTechnicianName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}