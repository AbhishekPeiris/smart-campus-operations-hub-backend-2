package com.smartcampus.operationshub.ticket.service;

import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.ticket.dto.request.AddResolutionNotesRequest;
import com.smartcampus.operationshub.ticket.dto.request.AssignTechnicianRequest;
import com.smartcampus.operationshub.ticket.dto.request.CreateIncidentTicketRequest;
import com.smartcampus.operationshub.ticket.dto.request.RejectTicketRequest;
import com.smartcampus.operationshub.ticket.dto.request.UpdateIncidentTicketRequest;
import com.smartcampus.operationshub.ticket.dto.request.UpdateTicketStatusRequest;
import com.smartcampus.operationshub.ticket.dto.response.IncidentTicketResponse;
import com.smartcampus.operationshub.ticket.dto.response.TicketDetailsResponse;

public interface IncidentTicketService {

    /**
     * Create new ticket
     */
    IncidentTicketResponse createTicket(CreateIncidentTicketRequest request, String createdByUserId);

    /**
     * Update ticket (only creator before assignment)
     */
    IncidentTicketResponse updateTicket(String ticketId, UpdateIncidentTicketRequest request);

    /**
     * Get ticket by ID (full details)
     */
    TicketDetailsResponse getTicketById(String ticketId);

    /**
     * Get all tickets (paginated)
     */
    PaginatedResponse<IncidentTicketResponse> getAllTickets(int page, int size);

    /**
     * Assign technician (ADMIN)
     */
    IncidentTicketResponse assignTechnician(String ticketId, AssignTechnicianRequest request);

    /**
     * Update ticket status (TECHNICIAN)
     */
    IncidentTicketResponse updateTicketStatus(String ticketId, UpdateTicketStatusRequest request);

    /**
     * Reject ticket (ADMIN)
     */
    IncidentTicketResponse rejectTicket(String ticketId, RejectTicketRequest request);

    /**
     * Add resolution notes (TECHNICIAN)
     */
    IncidentTicketResponse addResolutionNotes(String ticketId, AddResolutionNotesRequest request);
}