package com.smartcampus.operationshub.ticket.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketAttachmentResponse {

    private String id;

    private String fileName;
    private String fileType;
    private String fileUrl;

    private String uploadedByUserId;
    private String uploadedByName;

    private LocalDateTime uploadedAt;
}