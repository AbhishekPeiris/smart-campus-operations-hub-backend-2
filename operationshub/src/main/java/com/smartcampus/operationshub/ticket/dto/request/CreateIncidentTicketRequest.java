package com.smartcampus.operationshub.ticket.dto.request;

import com.smartcampus.operationshub.common.enums.IncidentCategory;
import com.smartcampus.operationshub.common.enums.ResourceType;
import com.smartcampus.operationshub.common.enums.TicketPriorityLevel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateIncidentTicketRequest {

    @NotNull(message = "Incident category is required")
    private IncidentCategory incidentCategory;

    @NotBlank(message = "Ticket title is required")
    @Size(min = 5, max = 120, message = "Title must be between 5 and 120 characters")
    private String ticketTitle;

    @NotBlank(message = "Description is required")
    @Size(min = 15, max = 2000, message = "Description must be between 15 and 2000 characters")
    private String description;

    @NotNull(message = "Priority level is required")
    private TicketPriorityLevel priorityLevel;

    @NotBlank(message = "Preferred contact name is required")
    private String preferredContactName;

    @Email(message = "Invalid email format")
    private String preferredContactEmailAddress;

    @Pattern(regexp = "^[0-9+\\-]{7,15}$", message = "Invalid phone number")
    private String preferredContactPhoneNumber;

    // Either resource OR location (you validate in service)
    private String resourceIdentifier;
    private String resourceName;
    private ResourceType resourceType;

    private String locationIdentifier;
    private String locationName;
}