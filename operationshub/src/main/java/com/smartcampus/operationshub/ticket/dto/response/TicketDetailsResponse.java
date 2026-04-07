package com.smartcampus.operationshub.ticket.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.smartcampus.operationshub.common.enums.IncidentCategory;
import com.smartcampus.operationshub.common.enums.ResourceType;
import com.smartcampus.operationshub.common.enums.TicketPriorityLevel;
import com.smartcampus.operationshub.common.enums.TicketStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDetailsResponse {

    private String id;
    private String ticketCode;

    private IncidentCategory incidentCategory;
    private String ticketTitle;
    private String description;

    private TicketPriorityLevel priorityLevel;
    private TicketStatus status;

    private String createdByUserId;
    private String createdByName;

    private String preferredContactName;
    private String preferredContactEmailAddress;
    private String preferredContactPhoneNumber;

    // Assignment
    private String assignedTechnicianId;
    private String assignedTechnicianName;

    // Resource / Location
    private String resourceIdentifier;
    private String resourceName;
    private ResourceType resourceType;

    private String locationIdentifier;
    private String locationName;

    // Resolution
    private String resolutionNotes;
    private String rejectionReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 🔥 Nested data (important for UI)
    private List<TicketCommentResponse> comments;
    private List<TicketAttachmentResponse> attachments;
    private List<TechnicianUpdateLogResponse> technicianUpdates;
}