package com.smartcampus.operationshub.ticket.dto.response;

import java.time.LocalDateTime;

import com.smartcampus.operationshub.common.enums.TicketStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TechnicianUpdateLogResponse {

    private String id;

    private String technicianUserId;
    private String technicianName;

    private TicketStatus previousStatus;
    private TicketStatus newStatus;

    private String updateMessage;

    private LocalDateTime updatedAt;
}