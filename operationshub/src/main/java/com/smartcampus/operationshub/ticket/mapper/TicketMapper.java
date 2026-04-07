package com.smartcampus.operationshub.ticket.mapper;

import com.smartcampus.operationshub.ticket.dto.response.*;
import com.smartcampus.operationshub.ticket.model.IncidentTicket;
import com.smartcampus.operationshub.ticket.model.TicketAttachment;
import com.smartcampus.operationshub.ticket.model.TicketComment;
import com.smartcampus.operationshub.ticket.model.TechnicianUpdateLog;

import java.util.List;
import java.util.stream.Collectors;

public class TicketMapper {

    public static IncidentTicketResponse toIncidentTicketResponse(IncidentTicket ticket) {
        return IncidentTicketResponse.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketReferenceNumber())
                .ticketTitle(ticket.getTicketTitle())
                .status(ticket.getCurrentStatus())
                .priorityLevel(ticket.getPriorityLevel())
                .createdByUserId(ticket.getReportedByUserId())
                .createdByName(ticket.getReportedByName())
                .assignedTechnicianId(ticket.getAssignedTechnicianUserId())
                .assignedTechnicianName(ticket.getAssignedTechnicianName())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

    public static TicketDetailsResponse toTicketDetailsResponse(
            IncidentTicket ticket,
            List<TicketCommentResponse> comments,
            List<TicketAttachmentResponse> attachments,
            List<TechnicianUpdateLogResponse> updates
    ) {
        return TicketDetailsResponse.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketReferenceNumber())
                .incidentCategory(ticket.getIncidentCategory())
                .ticketTitle(ticket.getTicketTitle())
                .description(ticket.getDescription())
                .priorityLevel(ticket.getPriorityLevel())
                .status(ticket.getCurrentStatus())
                .createdByUserId(ticket.getReportedByUserId())
                .createdByName(ticket.getReportedByName())
                .preferredContactName(ticket.getPreferredContactName())
                .preferredContactEmailAddress(ticket.getPreferredContactEmailAddress())
                .preferredContactPhoneNumber(ticket.getPreferredContactPhoneNumber())
                .assignedTechnicianId(ticket.getAssignedTechnicianUserId())
                .assignedTechnicianName(ticket.getAssignedTechnicianName())
                .resourceIdentifier(ticket.getResourceIdentifier())
                .resourceName(ticket.getResourceName())
                .resourceType(ticket.getResourceType())
                .locationIdentifier(ticket.getLocationIdentifier())
                .locationName(ticket.getLocationName())
                .resolutionNotes(ticket.getResolutionNotes())
                .rejectionReason(ticket.getRejectionReason())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .comments(comments)
                .attachments(attachments)
                .technicianUpdates(updates)
                .build();
    }

    public static List<IncidentTicketResponse> toIncidentTicketList(List<IncidentTicket> tickets) {
        return tickets.stream()
                .map(TicketMapper::toIncidentTicketResponse)
                .collect(Collectors.toList());
    }

    public static TicketCommentResponse toTicketCommentResponse(TicketComment comment) {
        return TicketCommentResponse.builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .commentedByUserId(comment.getCommentOwnerUserId())
                .commentedByName(comment.getCommentOwnerName())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public static TicketAttachmentResponse toTicketAttachmentResponse(TicketAttachment attachment) {
        return TicketAttachmentResponse.builder()
                .id(attachment.getId())
                .fileName(attachment.getOriginalFileName())
                .fileType(attachment.getFileContentType())
                .fileUrl(attachment.getFileAccessUrl())
                .uploadedByUserId(attachment.getUploadedByUserId())
                .uploadedByName(attachment.getUploadedByName())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }

    public static TechnicianUpdateLogResponse toTechnicianUpdateLogResponse(TechnicianUpdateLog log) {
        return TechnicianUpdateLogResponse.builder()
                .id(log.getId())
                .technicianUserId(log.getTechnicianUserId())
                .technicianName(log.getTechnicianName())
                .previousStatus(log.getPreviousStatus())
                .newStatus(log.getNewStatus())
                .updateMessage(log.getUpdateMessage())
                .updatedAt(log.getUpdatedAt())
                .build();
    }
}