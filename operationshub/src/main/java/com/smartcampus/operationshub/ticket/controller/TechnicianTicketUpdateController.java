package com.smartcampus.operationshub.ticket.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.operationshub.common.dto.ApiSuccessResponse;
import com.smartcampus.operationshub.ticket.dto.response.TechnicianUpdateLogResponse;
import com.smartcampus.operationshub.ticket.service.TechnicianTicketUpdateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tickets/updates")
@RequiredArgsConstructor
public class TechnicianTicketUpdateController {

    private final TechnicianTicketUpdateService technicianTicketUpdateService;

    @GetMapping("/{ticketId}")
    public ApiSuccessResponse<List<TechnicianUpdateLogResponse>> getUpdates(@PathVariable String ticketId) {
        return ApiSuccessResponse.<List<TechnicianUpdateLogResponse>>builder()
                .success(true)
                .message("Technician updates retrieved")
                .data(technicianTicketUpdateService.getUpdatesByTicket(ticketId))
                .build();
    }
}