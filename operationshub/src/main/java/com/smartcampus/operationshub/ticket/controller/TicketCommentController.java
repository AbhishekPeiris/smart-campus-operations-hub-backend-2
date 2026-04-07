package com.smartcampus.operationshub.ticket.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.operationshub.common.dto.ApiSuccessResponse;
import com.smartcampus.operationshub.common.enums.UserRole;
import com.smartcampus.operationshub.security.CurrentUserContext;
import com.smartcampus.operationshub.ticket.dto.request.AddTicketCommentRequest;
import com.smartcampus.operationshub.ticket.dto.request.UpdateTicketCommentRequest;
import com.smartcampus.operationshub.ticket.dto.response.TicketCommentResponse;
import com.smartcampus.operationshub.ticket.service.TicketCommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tickets/comments")
@RequiredArgsConstructor
public class TicketCommentController {

    private final TicketCommentService ticketCommentService;
    private final CurrentUserContext currentUserContext;

    @PostMapping
    public ApiSuccessResponse<TicketCommentResponse> addComment(
            @RequestParam String ticketId,
            @RequestParam(required = false) String userId,
            @Valid @RequestBody AddTicketCommentRequest request
    ) {
        String requesterId = currentUserContext.getCurrentUserId();
        return ApiSuccessResponse.<TicketCommentResponse>builder()
                .success(true)
                .message("Comment added")
                .data(ticketCommentService.addComment(ticketId, request, requesterId))
                .build();
    }

    @PutMapping("/{commentId}")
    public ApiSuccessResponse<TicketCommentResponse> updateComment(
            @PathVariable String commentId,
            @Valid @RequestBody UpdateTicketCommentRequest request
    ) {
        var currentUser = currentUserContext.getCurrentUser();
        return ApiSuccessResponse.<TicketCommentResponse>builder()
                .success(true)
                .message("Comment updated")
                .data(ticketCommentService.updateComment(
                        commentId,
                        request,
                        currentUser.getId(),
                        currentUser.getRole() == UserRole.ADMIN))
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ApiSuccessResponse<String> deleteComment(@PathVariable String commentId) {
        var currentUser = currentUserContext.getCurrentUser();
        ticketCommentService.deleteComment(commentId, currentUser.getId(), currentUser.getRole() == UserRole.ADMIN);
        return ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Comment deleted")
                .data(null)
                .build();
    }

    @GetMapping("/{ticketId}")
    public ApiSuccessResponse<List<TicketCommentResponse>> getComments(@PathVariable String ticketId) {
        return ApiSuccessResponse.<List<TicketCommentResponse>>builder()
                .success(true)
                .message("Comments retrieved")
                .data(ticketCommentService.getCommentsByTicket(ticketId))
                .build();
    }
}