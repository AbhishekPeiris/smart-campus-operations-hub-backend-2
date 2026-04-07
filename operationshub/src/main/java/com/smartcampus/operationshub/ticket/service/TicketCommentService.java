package com.smartcampus.operationshub.ticket.service;

import java.util.List;

import com.smartcampus.operationshub.ticket.dto.request.AddTicketCommentRequest;
import com.smartcampus.operationshub.ticket.dto.request.UpdateTicketCommentRequest;
import com.smartcampus.operationshub.ticket.dto.response.TicketCommentResponse;

public interface TicketCommentService {

    /**
     * Add comment
     */
    TicketCommentResponse addComment(String ticketId, AddTicketCommentRequest request, String userId);

    /**
     * Update comment
     */
    TicketCommentResponse updateComment(String commentId, UpdateTicketCommentRequest request);

    /**
     * Delete comment
     */
    void deleteComment(String commentId);

    /**
     * Get all comments for a ticket
     */
    List<TicketCommentResponse> getCommentsByTicket(String ticketId);
}