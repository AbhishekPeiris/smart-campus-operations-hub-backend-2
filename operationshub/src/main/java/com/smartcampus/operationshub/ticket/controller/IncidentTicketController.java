package com.smartcampus.operationshub.ticket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.operationshub.common.dto.ApiSuccessResponse;
import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.ticket.dto.request.AddResolutionNotesRequest;
import com.smartcampus.operationshub.ticket.dto.request.AssignTechnicianRequest;
import com.smartcampus.operationshub.ticket.dto.request.CreateIncidentTicketRequest;
import com.smartcampus.operationshub.ticket.dto.request.RejectTicketRequest;
import com.smartcampus.operationshub.ticket.dto.request.UpdateIncidentTicketRequest;
import com.smartcampus.operationshub.ticket.dto.request.UpdateTicketStatusRequest;
import com.smartcampus.operationshub.ticket.dto.response.IncidentTicketResponse;
import com.smartcampus.operationshub.ticket.dto.response.TicketDetailsResponse;
import com.smartcampus.operationshub.ticket.service.IncidentTicketService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class IncidentTicketController {

    private final IncidentTicketService incidentTicketService;

    @PostMapping
    public ApiSuccessResponse<IncidentTicketResponse> createTicket(
            @Valid @RequestBody CreateIncidentTicketRequest request,
            @RequestParam String userId
    ) {
        return ApiSuccessResponse.<IncidentTicketResponse>builder()
                .success(true)
                .message("Ticket created")
                .data(incidentTicketService.createTicket(request, userId))
                .build();
    }

    @PutMapping("/{ticketId}")
    public ApiSuccessResponse<IncidentTicketResponse> updateTicket(
            @PathVariable String ticketId,
            @Valid @RequestBody UpdateIncidentTicketRequest request
    ) {
        return ApiSuccessResponse.<IncidentTicketResponse>builder()
                .success(true)
                .message("Ticket updated")
                .data(incidentTicketService.updateTicket(ticketId, request))
                .build();
    }

    @GetMapping("/{ticketId}")
    public ApiSuccessResponse<TicketDetailsResponse> getTicket(@PathVariable String ticketId) {
        return ApiSuccessResponse.<TicketDetailsResponse>builder()
                .success(true)
                .message("Ticket details retrieved")
                .data(incidentTicketService.getTicketById(ticketId))
                .build();
    }

    @GetMapping
    public ApiSuccessResponse<PaginatedResponse<IncidentTicketResponse>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiSuccessResponse.<PaginatedResponse<IncidentTicketResponse>>builder()
                .success(true)
                .message("Tickets retrieved")
                .data(incidentTicketService.getAllTickets(page, size))
                .build();
    }

    @PatchMapping("/{ticketId}/assign")
    public ApiSuccessResponse<IncidentTicketResponse> assignTechnician(
            @PathVariable String ticketId,
            @Valid @RequestBody AssignTechnicianRequest request
    ) {
        return ApiSuccessResponse.<IncidentTicketResponse>builder()
                .success(true)
                .message("Technician assigned")
                .data(incidentTicketService.assignTechnician(ticketId, request))
                .build();
    }

    @PatchMapping("/{ticketId}/status")
    public ApiSuccessResponse<IncidentTicketResponse> updateStatus(
            @PathVariable String ticketId,
            @Valid @RequestBody UpdateTicketStatusRequest request
    ) {
        return ApiSuccessResponse.<IncidentTicketResponse>builder()
                .success(true)
                .message("Status updated")
                .data(incidentTicketService.updateTicketStatus(ticketId, request))
                .build();
    }

    @PatchMapping("/{ticketId}/reject")
    public ApiSuccessResponse<IncidentTicketResponse> rejectTicket(
            @PathVariable String ticketId,
            @Valid @RequestBody RejectTicketRequest request
    ) {
        return ApiSuccessResponse.<IncidentTicketResponse>builder()
                .success(true)
                .message("Ticket rejected")
                .data(incidentTicketService.rejectTicket(ticketId, request))
                .build();
    }

    @PatchMapping("/{ticketId}/resolve")
    public ApiSuccessResponse<IncidentTicketResponse> resolveTicket(
            @PathVariable String ticketId,
            @Valid @RequestBody AddResolutionNotesRequest request
    ) {
        return ApiSuccessResponse.<IncidentTicketResponse>builder()
                .success(true)
                .message("Ticket resolved")
                .data(incidentTicketService.addResolutionNotes(ticketId, request))
                .build();
    }
}