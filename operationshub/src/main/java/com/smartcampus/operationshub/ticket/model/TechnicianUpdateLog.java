package com.smartcampus.operationshub.ticket.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.smartcampus.operationshub.common.enums.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "technician_update_logs")
public class TechnicianUpdateLog {

    @Id
    private String id;

    @Indexed
    @Field("ticket_id")
    private String ticketId;

    @Indexed
    @Field("technician_user_id")
    private String technicianUserId;

    @Field("technician_name")
    private String technicianName;

    @Field("previous_status")
    private TicketStatus previousStatus;

    @Field("new_status")
    private TicketStatus newStatus;

    @Field("update_message")
    private String updateMessage;

    @Field("resolution_notes_snapshot")
    private String resolutionNotesSnapshot;

    @CreatedDate
    @Indexed
    @Field("updated_at")
    private LocalDateTime updatedAt;
}