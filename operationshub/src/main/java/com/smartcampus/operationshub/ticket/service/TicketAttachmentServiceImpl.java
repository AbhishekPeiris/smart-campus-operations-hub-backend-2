package com.smartcampus.operationshub.ticket.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smartcampus.operationshub.common.exception.BadRequestException;
import com.smartcampus.operationshub.common.exception.ResourceNotFoundException;
import com.smartcampus.operationshub.ticket.dto.response.TicketAttachmentResponse;
import com.smartcampus.operationshub.ticket.mapper.TicketMapper;
import com.smartcampus.operationshub.ticket.model.TicketAttachment;
import com.smartcampus.operationshub.ticket.repository.IncidentTicketRepository;
import com.smartcampus.operationshub.ticket.repository.TicketAttachmentRepository;
import com.smartcampus.operationshub.user.model.UserAccount;
import com.smartcampus.operationshub.user.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketAttachmentServiceImpl implements TicketAttachmentService {

    private final TicketAttachmentRepository ticketAttachmentRepository;
    private final IncidentTicketRepository incidentTicketRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public TicketAttachmentResponse uploadAttachment(String ticketId, String fileName, String fileType, String fileUrl, String uploadedBy) {
        incidentTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        if (!StringUtils.hasText(fileName) || !StringUtils.hasText(fileType) || !StringUtils.hasText(fileUrl)) {
            throw new BadRequestException("Attachment file name, file type, and file URL are required");
        }

        UserAccount userAccount = findUserById(uploadedBy);

        TicketAttachment attachment = TicketAttachment.builder()
                .ticketId(ticketId)
                .originalFileName(fileName)
                .storedFileName(UUID.randomUUID() + "_" + StringUtils.cleanPath(fileName))
                .fileContentType(fileType)
                .fileAccessUrl(fileUrl)
                .uploadedByUserId(userAccount.getId())
                .uploadedByName(userAccount.getFullName())
                .build();

        return TicketMapper.toTicketAttachmentResponse(ticketAttachmentRepository.save(attachment));
    }

    @Override
    public List<TicketAttachmentResponse> getAttachmentsByTicket(String ticketId) {
        incidentTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        return ticketAttachmentRepository.findByTicketId(ticketId).stream()
                .map(TicketMapper::toTicketAttachmentResponse)
                .toList();
    }

    @Override
    public void deleteAttachment(String attachmentId) {
        if (!ticketAttachmentRepository.existsById(attachmentId)) {
            throw new ResourceNotFoundException("Attachment not found with id: " + attachmentId);
        }

        ticketAttachmentRepository.deleteById(attachmentId);
    }

    private UserAccount findUserById(String userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}