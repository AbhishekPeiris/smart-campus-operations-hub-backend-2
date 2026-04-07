package com.smartcampus.operationshub.ticket.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.common.enums.NotificationType;
import com.smartcampus.operationshub.common.enums.TicketStatus;
import com.smartcampus.operationshub.common.exception.BadRequestException;
import com.smartcampus.operationshub.common.exception.ForbiddenOperationException;
import com.smartcampus.operationshub.common.exception.ResourceNotFoundException;
import com.smartcampus.operationshub.common.util.TicketReferenceGenerator;
import com.smartcampus.operationshub.notification.service.NotificationService;
import com.smartcampus.operationshub.ticket.dto.request.AddResolutionNotesRequest;
import com.smartcampus.operationshub.ticket.dto.request.AssignTechnicianRequest;
import com.smartcampus.operationshub.ticket.dto.request.CreateIncidentTicketRequest;
import com.smartcampus.operationshub.ticket.dto.request.RejectTicketRequest;
import com.smartcampus.operationshub.ticket.dto.request.UpdateIncidentTicketRequest;
import com.smartcampus.operationshub.ticket.dto.request.UpdateTicketStatusRequest;
import com.smartcampus.operationshub.ticket.dto.response.IncidentTicketResponse;
import com.smartcampus.operationshub.ticket.dto.response.TicketAttachmentResponse;
import com.smartcampus.operationshub.ticket.dto.response.TicketCommentResponse;
import com.smartcampus.operationshub.ticket.dto.response.TicketDetailsResponse;
import com.smartcampus.operationshub.ticket.dto.response.TechnicianUpdateLogResponse;
import com.smartcampus.operationshub.ticket.mapper.TicketMapper;
import com.smartcampus.operationshub.ticket.model.IncidentTicket;
import com.smartcampus.operationshub.ticket.model.TicketAttachment;
import com.smartcampus.operationshub.ticket.model.TicketComment;
import com.smartcampus.operationshub.ticket.model.TechnicianUpdateLog;
import com.smartcampus.operationshub.ticket.repository.IncidentTicketRepository;
import com.smartcampus.operationshub.ticket.repository.TechnicianUpdateLogRepository;
import com.smartcampus.operationshub.ticket.repository.TicketAttachmentRepository;
import com.smartcampus.operationshub.ticket.repository.TicketCommentRepository;
import com.smartcampus.operationshub.user.model.UserAccount;
import com.smartcampus.operationshub.user.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncidentTicketServiceImpl implements IncidentTicketService {

	private final IncidentTicketRepository incidentTicketRepository;
	private final TicketCommentRepository ticketCommentRepository;
	private final TicketAttachmentRepository ticketAttachmentRepository;
	private final TechnicianUpdateLogRepository technicianUpdateLogRepository;
	private final UserAccountRepository userAccountRepository;
	private final TechnicianTicketUpdateService technicianTicketUpdateService;
	private final NotificationService notificationService;

	@Override
	public IncidentTicketResponse createTicket(CreateIncidentTicketRequest request, String createdByUserId) {
		UserAccount creator = findUserById(createdByUserId);
		validateCreateRequest(request);

		IncidentTicket ticket = IncidentTicket.builder()
				.ticketReferenceNumber(TicketReferenceGenerator.generateTicketCode())
				.reportedByUserId(creator.getId())
				.reportedByName(creator.getFullName())
				.incidentCategory(request.getIncidentCategory())
				.ticketTitle(request.getTicketTitle())
				.description(request.getDescription())
				.priorityLevel(request.getPriorityLevel())
				.preferredContactName(request.getPreferredContactName())
				.preferredContactEmailAddress(request.getPreferredContactEmailAddress())
				.preferredContactPhoneNumber(request.getPreferredContactPhoneNumber())
				.resourceIdentifier(request.getResourceIdentifier())
				.resourceName(request.getResourceName())
				.resourceType(request.getResourceType())
				.locationIdentifier(request.getLocationIdentifier())
				.locationName(request.getLocationName())
				.currentStatus(TicketStatus.OPEN)
				.build();

		return TicketMapper.toIncidentTicketResponse(incidentTicketRepository.save(ticket));
	}

	@Override
	public IncidentTicketResponse updateTicket(String ticketId, UpdateIncidentTicketRequest request) {
		IncidentTicket ticket = findTicketById(ticketId);

		if (ticket.getAssignedTechnicianUserId() != null) {
			throw new ForbiddenOperationException("Assigned tickets cannot be edited");
		}

		if (request.getTicketTitle() != null) {
			ticket.setTicketTitle(request.getTicketTitle());
		}
		if (request.getDescription() != null) {
			ticket.setDescription(request.getDescription());
		}
		if (request.getPriorityLevel() != null) {
			ticket.setPriorityLevel(request.getPriorityLevel());
		}
		if (request.getPreferredContactName() != null) {
			ticket.setPreferredContactName(request.getPreferredContactName());
		}
		if (request.getPreferredContactEmailAddress() != null) {
			ticket.setPreferredContactEmailAddress(request.getPreferredContactEmailAddress());
		}
		if (request.getPreferredContactPhoneNumber() != null) {
			ticket.setPreferredContactPhoneNumber(request.getPreferredContactPhoneNumber());
		}

		return TicketMapper.toIncidentTicketResponse(incidentTicketRepository.save(ticket));
	}

	@Override
	public TicketDetailsResponse getTicketById(String ticketId) {
		IncidentTicket ticket = findTicketById(ticketId);

		List<TicketCommentResponse> comments = ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
				.map(TicketMapper::toTicketCommentResponse)
				.toList();
		List<TicketAttachmentResponse> attachments = ticketAttachmentRepository.findByTicketId(ticketId).stream()
				.map(TicketMapper::toTicketAttachmentResponse)
				.toList();
		List<TechnicianUpdateLogResponse> updates = technicianUpdateLogRepository.findByTicketIdOrderByUpdatedAtDesc(ticketId).stream()
				.map(TicketMapper::toTechnicianUpdateLogResponse)
				.toList();

		return TicketMapper.toTicketDetailsResponse(ticket, comments, attachments, updates);
	}

	@Override
	public PaginatedResponse<IncidentTicketResponse> getAllTickets(int page, int size) {
		Page<IncidentTicket> ticketPage = incidentTicketRepository.findAll(
				PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

		return PaginatedResponse.<IncidentTicketResponse>builder()
				.content(ticketPage.getContent().stream().map(TicketMapper::toIncidentTicketResponse).toList())
				.currentPage(ticketPage.getNumber())
				.totalPages(ticketPage.getTotalPages())
				.totalElements(ticketPage.getTotalElements())
				.pageSize(ticketPage.getSize())
				.hasNext(ticketPage.hasNext())
				.hasPrevious(ticketPage.hasPrevious())
				.build();
	}

	@Override
	public IncidentTicketResponse assignTechnician(String ticketId, AssignTechnicianRequest request) {
		IncidentTicket ticket = findTicketById(ticketId);

		if (TicketStatus.isFinalStatus(ticket.getCurrentStatus())) {
			throw new BadRequestException("Finalized tickets cannot be reassigned");
		}

		TicketStatus previousStatus = ticket.getCurrentStatus();
		ticket.setAssignedTechnicianUserId(request.getTechnicianUserId());
		ticket.setAssignedTechnicianName(request.getTechnicianName());
		if (ticket.getCurrentStatus() == TicketStatus.OPEN) {
			ticket.setCurrentStatus(TicketStatus.IN_PROGRESS);
		}

		IncidentTicket savedTicket = incidentTicketRepository.save(ticket);

		if (previousStatus != savedTicket.getCurrentStatus()) {
			technicianTicketUpdateService.logUpdate(
					savedTicket.getId(),
					savedTicket.getAssignedTechnicianUserId(),
					savedTicket.getAssignedTechnicianName(),
					previousStatus,
					savedTicket.getCurrentStatus(),
					"Technician assigned to ticket");
		}

		return TicketMapper.toIncidentTicketResponse(savedTicket);
	}

	@Override
	public IncidentTicketResponse updateTicketStatus(String ticketId, UpdateTicketStatusRequest request) {
		IncidentTicket ticket = findTicketById(ticketId);
		TicketStatus currentStatus = ticket.getCurrentStatus();
		TicketStatus newStatus = request.getNewStatus();

		if (currentStatus != newStatus && !TicketStatus.isValidTransition(currentStatus, newStatus)) {
			throw new BadRequestException("Invalid ticket status transition from " + currentStatus + " to " + newStatus);
		}

		ticket.setCurrentStatus(newStatus);
		if (newStatus == TicketStatus.RESOLVED) {
			ticket.setResolvedAt(LocalDateTime.now());
		} else if (newStatus == TicketStatus.CLOSED) {
			ticket.setClosedAt(LocalDateTime.now());
		} else if (newStatus == TicketStatus.REJECTED) {
			ticket.setClosedAt(LocalDateTime.now());
		}

		IncidentTicket savedTicket = incidentTicketRepository.save(ticket);
		technicianTicketUpdateService.logUpdate(
				savedTicket.getId(),
				savedTicket.getAssignedTechnicianUserId(),
				savedTicket.getAssignedTechnicianName(),
				currentStatus,
				savedTicket.getCurrentStatus(),
				request.getUpdateMessage());

		notifyTicketStatusChange(savedTicket, currentStatus, savedTicket.getCurrentStatus());

		return TicketMapper.toIncidentTicketResponse(savedTicket);
	}

	@Override
	public IncidentTicketResponse rejectTicket(String ticketId, RejectTicketRequest request) {
		IncidentTicket ticket = findTicketById(ticketId);
		TicketStatus currentStatus = ticket.getCurrentStatus();

		if (currentStatus != TicketStatus.REJECTED && !TicketStatus.isValidTransition(currentStatus, TicketStatus.REJECTED)) {
			throw new BadRequestException("Invalid ticket status transition from " + currentStatus + " to REJECTED");
		}

		ticket.setCurrentStatus(TicketStatus.REJECTED);
		ticket.setRejectionReason(request.getRejectionReason());
		ticket.setClosedAt(LocalDateTime.now());

		IncidentTicket savedTicket = incidentTicketRepository.save(ticket);
		technicianTicketUpdateService.logUpdate(
				savedTicket.getId(),
				savedTicket.getAssignedTechnicianUserId(),
				savedTicket.getAssignedTechnicianName(),
				currentStatus,
				TicketStatus.REJECTED,
				request.getRejectionReason());

		notifyTicketStatusChange(savedTicket, currentStatus, TicketStatus.REJECTED);

		return TicketMapper.toIncidentTicketResponse(savedTicket);
	}

	@Override
	public IncidentTicketResponse addResolutionNotes(String ticketId, AddResolutionNotesRequest request) {
		IncidentTicket ticket = findTicketById(ticketId);
		TicketStatus currentStatus = ticket.getCurrentStatus();

		if (currentStatus != TicketStatus.RESOLVED && !TicketStatus.isValidTransition(currentStatus, TicketStatus.RESOLVED)) {
			throw new BadRequestException("Invalid ticket status transition from " + currentStatus + " to RESOLVED");
		}

		ticket.setResolutionNotes(request.getResolutionNotes());
		ticket.setCurrentStatus(TicketStatus.RESOLVED);
		ticket.setResolvedAt(LocalDateTime.now());

		IncidentTicket savedTicket = incidentTicketRepository.save(ticket);
		technicianTicketUpdateService.logUpdate(
				savedTicket.getId(),
				savedTicket.getAssignedTechnicianUserId(),
				savedTicket.getAssignedTechnicianName(),
				currentStatus,
				TicketStatus.RESOLVED,
				request.getResolutionNotes());

		notifyTicketStatusChange(savedTicket, currentStatus, TicketStatus.RESOLVED);

		return TicketMapper.toIncidentTicketResponse(savedTicket);
	}

	private void notifyTicketStatusChange(IncidentTicket ticket, TicketStatus previousStatus, TicketStatus newStatus) {
		if (ticket.getReportedByUserId() == null) {
			return;
		}

		notificationService.notifyUser(
				ticket.getReportedByUserId(),
				NotificationType.TICKET_STATUS_CHANGED,
				"Ticket Status Updated",
				"Ticket " + ticket.getTicketReferenceNumber() + " changed from " + previousStatus + " to " + newStatus + ".",
				"TICKET",
				ticket.getId());
	}

	private void validateCreateRequest(CreateIncidentTicketRequest request) {
		boolean resourceProvided = StringUtils.hasText(request.getResourceIdentifier())
				|| StringUtils.hasText(request.getResourceName())
				|| request.getResourceType() != null;
		boolean locationProvided = StringUtils.hasText(request.getLocationIdentifier())
				|| StringUtils.hasText(request.getLocationName());

		if (resourceProvided && locationProvided) {
			throw new BadRequestException("Provide either resource details or location details, not both");
		}

		if (!resourceProvided && !locationProvided) {
			throw new BadRequestException("Either resource details or location details are required");
		}

		if (resourceProvided) {
			if (!StringUtils.hasText(request.getResourceIdentifier())
					|| !StringUtils.hasText(request.getResourceName())
					|| request.getResourceType() == null) {
				throw new BadRequestException("Resource identifier, resource name, and resource type are required together");
			}
		}

		if (locationProvided) {
			if (!StringUtils.hasText(request.getLocationIdentifier())
					|| !StringUtils.hasText(request.getLocationName())) {
				throw new BadRequestException("Location identifier and location name are required together");
			}
		}
	}

	private IncidentTicket findTicketById(String ticketId) {
		return incidentTicketRepository.findById(ticketId)
				.orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));
	}

	private UserAccount findUserById(String userId) {
		return userAccountRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
	}
}