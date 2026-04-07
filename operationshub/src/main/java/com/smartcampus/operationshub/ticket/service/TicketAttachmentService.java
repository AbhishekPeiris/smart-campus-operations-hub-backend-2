package com.smartcampus.operationshub.ticket.service;

import java.util.List;

import com.smartcampus.operationshub.ticket.dto.response.TicketAttachmentResponse;

public interface TicketAttachmentService {

    /**
     * Upload attachment to ticket
     */
    TicketAttachmentResponse uploadAttachment(String ticketId, String fileName, String fileType, String fileUrl, String uploadedBy);

    /**
     * Get all attachments for a ticket
     */
    List<TicketAttachmentResponse> getAttachmentsByTicket(String ticketId);

    /**
     * Delete attachment
     */
    void deleteAttachment(String attachmentId);
}