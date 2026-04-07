package com.smartcampus.operationshub.ticket.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartcampus.operationshub.common.enums.TicketStatus;
import com.smartcampus.operationshub.ticket.dto.response.TechnicianUpdateLogResponse;
import com.smartcampus.operationshub.ticket.mapper.TicketMapper;
import com.smartcampus.operationshub.ticket.model.TechnicianUpdateLog;
import com.smartcampus.operationshub.ticket.repository.TechnicianUpdateLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TechnicianTicketUpdateServiceImpl implements TechnicianTicketUpdateService {

    private final TechnicianUpdateLogRepository technicianUpdateLogRepository;

    @Override
    public void logUpdate(
            String ticketId,
            String technicianId,
            String technicianName,
            TicketStatus previousStatus,
            TicketStatus newStatus,
            String message
    ) {
        TechnicianUpdateLog updateLog = TechnicianUpdateLog.builder()
                .ticketId(ticketId)
                .technicianUserId(technicianId)
                .technicianName(technicianName)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .updateMessage(message)
                .build();

        technicianUpdateLogRepository.save(updateLog);
    }

    @Override
    public List<TechnicianUpdateLogResponse> getUpdatesByTicket(String ticketId) {
        return technicianUpdateLogRepository.findByTicketIdOrderByUpdatedAtDesc(ticketId).stream()
                .map(TicketMapper::toTechnicianUpdateLogResponse)
                .toList();
    }
}