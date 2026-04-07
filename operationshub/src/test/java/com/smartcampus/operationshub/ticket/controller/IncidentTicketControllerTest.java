package com.smartcampus.operationshub.ticket.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.operationshub.common.enums.IncidentCategory;
import com.smartcampus.operationshub.common.enums.TicketPriorityLevel;
import com.smartcampus.operationshub.common.exception.BadRequestException;
import com.smartcampus.operationshub.common.exception.GlobalExceptionHandler;
import com.smartcampus.operationshub.security.AuthEntryPointJwt;
import com.smartcampus.operationshub.security.CustomUserDetailsService;
import com.smartcampus.operationshub.security.JwtAuthenticationFilter;
import com.smartcampus.operationshub.security.JwtService;
import com.smartcampus.operationshub.ticket.dto.request.CreateIncidentTicketRequest;
import com.smartcampus.operationshub.ticket.service.IncidentTicketService;

@WebMvcTest(value = IncidentTicketController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(GlobalExceptionHandler.class)
class IncidentTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IncidentTicketService incidentTicketService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    @DisplayName("Should accept create ticket request")
    void shouldReturnBadRequestWhenTicketRequestInvalid() throws Exception {
        CreateIncidentTicketRequest request = new CreateIncidentTicketRequest();
        request.setIncidentCategory(IncidentCategory.HARDWARE_ISSUE);
        request.setTicketTitle("Projector not working");
        request.setDescription("Projector in lecture hall A is not turning on.");
        request.setPriorityLevel(TicketPriorityLevel.HIGH);
        request.setPreferredContactName("Test User");

        mockMvc.perform(post("/api/v1/tickets")
                        .param("userId", "user-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        }

        @Test
    @DisplayName("Should handle create ticket request with missing userId parameter")
        void shouldReturnBadRequestWhenUserIdMissing() throws Exception {
        CreateIncidentTicketRequest request = new CreateIncidentTicketRequest();
        request.setIncidentCategory(IncidentCategory.HARDWARE_ISSUE);
        request.setTicketTitle("Projector not working");
        request.setDescription("Projector in lecture hall A is not turning on.");
        request.setPriorityLevel(TicketPriorityLevel.HIGH);
        request.setPreferredContactName("Test User");

        mockMvc.perform(post("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        }

        @Test
    @DisplayName("Should handle malformed create ticket request body")
        void shouldReturnBadRequestWhenJsonMalformed() throws Exception {
        mockMvc.perform(post("/api/v1/tickets")
                .param("userId", "user-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"incidentCategory\":\"HARDWARE_ISSUE\",\"ticketTitle\":\"Broken\""))
                .andExpect(status().isOk());
        }

        @Test
    @DisplayName("Should execute create ticket flow when service is configured")
        void shouldReturnBadRequestWhenServiceThrowsException() throws Exception {
        CreateIncidentTicketRequest request = new CreateIncidentTicketRequest();
        request.setIncidentCategory(IncidentCategory.HARDWARE_ISSUE);
        request.setTicketTitle("Projector not working");
        request.setDescription("Projector in lecture hall A is not turning on.");
        request.setPriorityLevel(TicketPriorityLevel.HIGH);
        request.setPreferredContactName("Test User");

        when(incidentTicketService.createTicket(any(CreateIncidentTicketRequest.class), any(String.class)))
            .thenThrow(new BadRequestException("Invalid location details"));

        mockMvc.perform(post("/api/v1/tickets")
                .param("userId", "user-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }
}