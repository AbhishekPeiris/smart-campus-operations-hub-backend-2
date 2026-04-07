package com.smartcampus.operationshub.ticket.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.smartcampus.operationshub.common.enums.IncidentCategory;
import com.smartcampus.operationshub.common.enums.ResourceType;
import com.smartcampus.operationshub.common.enums.TicketPriorityLevel;
import com.smartcampus.operationshub.common.enums.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incident_tickets")
public class IncidentTicket {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("ticket_reference_number")
    private String ticketReferenceNumber;

    @Indexed
    @Field("reported_by_user_id")
    private String reportedByUserId;

    @Field("reported_by_name")
    private String reportedByName;

    @Field("incident_category")
    private IncidentCategory incidentCategory;

    @Field("ticket_title")
    private String ticketTitle;

    @Field("description")
    private String description;

    @Field("priority_level")
    private TicketPriorityLevel priorityLevel;

    @Builder.Default
    @Indexed
    @Field("current_status")
    private TicketStatus currentStatus = TicketStatus.OPEN;

    @Field("preferred_contact_name")
    private String preferredContactName;

    @Field("preferred_contact_email_address")
    private String preferredContactEmailAddress;

    @Field("preferred_contact_phone_number")
    private String preferredContactPhoneNumber;

    @Field("resource_identifier")
    private String resourceIdentifier;

    @Field("resource_name")
    private String resourceName;

    @Field("resource_type")
    private ResourceType resourceType;

    @Field("location_identifier")
    private String locationIdentifier;

    @Field("location_name")
    private String locationName;

    @Indexed
    @Field("assigned_technician_user_id")
    private String assignedTechnicianUserId;

    @Field("assigned_technician_name")
    private String assignedTechnicianName;

    @Field("resolution_notes")
    private String resolutionNotes;

    @Field("rejection_reason")
    private String rejectionReason;

    @Field("resolved_at")
    private LocalDateTime resolvedAt;

    @Field("closed_at")
    private LocalDateTime closedAt;

    @CreatedDate
    @Indexed
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}