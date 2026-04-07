package com.smartcampus.operationshub.ticket.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketCommentResponse {

    private String id;

    private String commentText;

    private String commentedByUserId;
    private String commentedByName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}