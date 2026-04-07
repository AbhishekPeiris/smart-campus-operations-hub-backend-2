package com.smartcampus.operationshub.ticket.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartcampus.operationshub.common.enums.NotificationType;
import com.smartcampus.operationshub.common.exception.BadRequestException;
import com.smartcampus.operationshub.common.exception.ForbiddenOperationException;
import com.smartcampus.operationshub.common.exception.ResourceNotFoundException;
import com.smartcampus.operationshub.notification.service.NotificationService;
import com.smartcampus.operationshub.ticket.model.IncidentTicket;
import com.smartcampus.operationshub.ticket.dto.request.AddTicketCommentRequest;
import com.smartcampus.operationshub.ticket.dto.request.UpdateTicketCommentRequest;
import com.smartcampus.operationshub.ticket.dto.response.TicketCommentResponse;
import com.smartcampus.operationshub.ticket.mapper.TicketMapper;
import com.smartcampus.operationshub.ticket.model.TicketComment;
import com.smartcampus.operationshub.ticket.repository.IncidentTicketRepository;
import com.smartcampus.operationshub.ticket.repository.TicketCommentRepository;
import com.smartcampus.operationshub.user.model.UserAccount;
import com.smartcampus.operationshub.user.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketCommentServiceImpl implements TicketCommentService {

    private final TicketCommentRepository ticketCommentRepository;
    private final IncidentTicketRepository incidentTicketRepository;
    private final UserAccountRepository userAccountRepository;
    private final NotificationService notificationService;

    @Override
    public TicketCommentResponse addComment(String ticketId, AddTicketCommentRequest request, String userId) {
        IncidentTicket ticket = incidentTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        UserAccount userAccount = findUserById(userId);

        TicketComment comment = TicketComment.builder()
                .ticketId(ticketId)
                .commentText(request.getCommentText())
                .commentOwnerUserId(userAccount.getId())
                .commentOwnerName(userAccount.getFullName())
                .commentOwnerRole(userAccount.getRole())
                .build();

        TicketComment savedComment = ticketCommentRepository.save(comment);

        if (!userAccount.getId().equals(ticket.getReportedByUserId())) {
            notificationService.notifyUser(
                    ticket.getReportedByUserId(),
                    NotificationType.TICKET_COMMENT_ADDED,
                    "New Comment on Ticket",
                    userAccount.getFullName() + " added a new comment on your ticket " + ticket.getTicketReferenceNumber() + ".",
                    "TICKET",
                    ticketId);
        }

        return TicketMapper.toTicketCommentResponse(savedComment);
    }

    @Override
    public TicketCommentResponse updateComment(
            String commentId,
            UpdateTicketCommentRequest request,
            String requesterUserId,
            boolean isAdmin) {
        TicketComment comment = findCommentById(commentId);

        if (!isAdmin && !comment.getCommentOwnerUserId().equals(requesterUserId)) {
            throw new ForbiddenOperationException("You can only edit your own comments");
        }

        comment.setCommentText(request.getCommentText());
        comment.setEdited(true);
        return TicketMapper.toTicketCommentResponse(ticketCommentRepository.save(comment));
    }

    @Override
    public void deleteComment(String commentId, String requesterUserId, boolean isAdmin) {
        TicketComment comment = findCommentById(commentId);

        if (!isAdmin && !comment.getCommentOwnerUserId().equals(requesterUserId)) {
            throw new ForbiddenOperationException("You can only delete your own comments");
        }

        ticketCommentRepository.deleteById(comment.getId());
    }

    @Override
    public List<TicketCommentResponse> getCommentsByTicket(String ticketId) {
        incidentTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        return ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
                .map(TicketMapper::toTicketCommentResponse)
                .toList();
    }

    private TicketComment findCommentById(String commentId) {
        return ticketCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
    }

    private UserAccount findUserById(String userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}