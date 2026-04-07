package com.smartcampus.operationshub.ticket.dto.request;

import com.smartcampus.operationshub.common.enums.TicketPriorityLevel;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateIncidentTicketRequest {

    @Size(min = 5, max = 120, message = "Title must be between 5 and 120 characters")
    private String ticketTitle;

    @Size(min = 15, max = 2000, message = "Description must be between 15 and 2000 characters")
    private String description;

    private TicketPriorityLevel priorityLevel;

    private String preferredContactName;
    private String preferredContactEmailAddress;
    private String preferredContactPhoneNumber;
}