package com.smartcampus.operationshub.ticket.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.operationshub.common.dto.ApiSuccessResponse;
import com.smartcampus.operationshub.ticket.dto.response.TicketAttachmentResponse;
import com.smartcampus.operationshub.ticket.service.TicketAttachmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tickets/attachments")
@RequiredArgsConstructor
public class TicketAttachmentController {

    private final TicketAttachmentService ticketAttachmentService;

    @PostMapping
    public ApiSuccessResponse<TicketAttachmentResponse> uploadAttachment(
            @RequestParam String ticketId,
            @RequestParam String fileName,
            @RequestParam String fileType,
            @RequestParam String fileUrl,
            @RequestParam String userId
    ) {
        return ApiSuccessResponse.<TicketAttachmentResponse>builder()
                .success(true)
                .message("Attachment uploaded")
                .data(ticketAttachmentService.uploadAttachment(ticketId, fileName, fileType, fileUrl, userId))
                .build();
    }

    @GetMapping("/{ticketId}")
    public ApiSuccessResponse<List<TicketAttachmentResponse>> getAttachments(@PathVariable String ticketId) {
        return ApiSuccessResponse.<List<TicketAttachmentResponse>>builder()
                .success(true)
                .message("Attachments retrieved")
                .data(ticketAttachmentService.getAttachmentsByTicket(ticketId))
                .build();
    }

    @DeleteMapping("/{attachmentId}")
    public ApiSuccessResponse<String> deleteAttachment(@PathVariable String attachmentId) {
        ticketAttachmentService.deleteAttachment(attachmentId);
        return ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Attachment deleted")
                .data(null)
                .build();
    }
}