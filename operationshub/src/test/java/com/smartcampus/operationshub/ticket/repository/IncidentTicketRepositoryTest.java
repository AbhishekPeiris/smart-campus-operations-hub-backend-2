package com.smartcampus.operationshub.ticket.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.smartcampus.operationshub.common.enums.IncidentCategory;
import com.smartcampus.operationshub.common.enums.TicketPriorityLevel;
import com.smartcampus.operationshub.common.enums.TicketStatus;
import com.smartcampus.operationshub.ticket.model.IncidentTicket;

@DataMongoTest
class IncidentTicketRepositoryTest {

    @Autowired
    private IncidentTicketRepository incidentTicketRepository;

    @Test
    @DisplayName("Should find tickets by current status")
    void shouldFindTicketsByCurrentStatus() {
        incidentTicketRepository.deleteAll();

        IncidentTicket ticket = IncidentTicket.builder()
                .ticketReferenceNumber("INC-TEST-001")
                .reportedByUserId("user-001")
                .reportedByName("Test User")
                .incidentCategory(IncidentCategory.HARDWARE_ISSUE)
                .ticketTitle("Projector not working")
                .description("The projector in lecture hall A is not turning on.")
                .priorityLevel(TicketPriorityLevel.HIGH)
                .currentStatus(TicketStatus.OPEN)
                .preferredContactName("Test User")
                .preferredContactEmailAddress("test@smartcampus.com")
                .preferredContactPhoneNumber("0771234567")
                .resourceIdentifier("RES-001")
                .resourceName("Projector A")
                .build();

        incidentTicketRepository.save(ticket);

        List<IncidentTicket> results = incidentTicketRepository.findByCurrentStatus(TicketStatus.OPEN);

        assertTrue(results.stream().anyMatch(saved -> "INC-TEST-001".equals(saved.getTicketReferenceNumber())));
    }
}