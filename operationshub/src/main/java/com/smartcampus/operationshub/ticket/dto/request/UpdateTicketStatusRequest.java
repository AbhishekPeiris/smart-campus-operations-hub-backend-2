package com.smartcampus.operationshub.ticket.dto.request;

import com.smartcampus.operationshub.common.enums.TicketStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTicketStatusRequest {

    @NotNull(message = "New ticket status is required")
    private TicketStatus newStatus;

    // Optional message (used in logs)
    private String updateMessage;
}