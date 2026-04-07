package com.smartcampus.operationshub.common;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import com.smartcampus.operationshub.auth.dto.LoginRequest;
import com.smartcampus.operationshub.common.dto.ApiErrorResponse;
import com.smartcampus.operationshub.common.enums.IncidentCategory;
import com.smartcampus.operationshub.common.enums.TicketPriorityLevel;
import com.smartcampus.operationshub.common.exception.BadRequestException;
import com.smartcampus.operationshub.common.exception.GlobalExceptionHandler;
import com.smartcampus.operationshub.ticket.dto.request.CreateIncidentTicketRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ValidationAndErrorHandlingTest {

    private final Validator validator;
    private final GlobalExceptionHandler globalExceptionHandler;

    ValidationAndErrorHandlingTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
        this.globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should validate login request required fields")
    void shouldValidateLoginRequestRequiredFields() {
        LoginRequest request = new LoginRequest();
        request.setUniversityEmailAddress("");
        request.setPassword("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(v -> "Email is required".equals(v.getMessage())));
        assertTrue(violations.stream().anyMatch(v -> "Password is required".equals(v.getMessage())));
    }

    @Test
    @DisplayName("Should validate create incident ticket payload constraints")
    void shouldValidateCreateTicketPayloadConstraints() {
        CreateIncidentTicketRequest request = new CreateIncidentTicketRequest();
        request.setIncidentCategory(IncidentCategory.HARDWARE_ISSUE);
        request.setTicketTitle("Bad");
        request.setDescription("Too short");
        request.setPriorityLevel(TicketPriorityLevel.HIGH);

        Set<ConstraintViolation<CreateIncidentTicketRequest>> violations = validator.validate(request);

        assertTrue(violations.stream().anyMatch(v -> "Title must be between 5 and 120 characters".equals(v.getMessage())));
        assertTrue(violations.stream().anyMatch(v -> "Description must be between 15 and 2000 characters".equals(v.getMessage())));
        assertTrue(violations.stream().anyMatch(v -> "Preferred contact name is required".equals(v.getMessage())));
    }

    @Test
    @DisplayName("Should map bad request exception to 400 response")
    void shouldMapBadRequestExceptionTo400Response() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/tickets");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleBadRequestException(
                new BadRequestException("Invalid location details"),
                request
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid location details", response.getBody().getMessage());
        assertEquals("/api/v1/tickets", response.getBody().getPath());
    }

    @Test
    @DisplayName("Should map missing request parameter to 400 response")
    void shouldMapMissingRequestParameterTo400Response() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/tickets");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleMissingServletRequestParameterException(
                new MissingServletRequestParameterException("userId", "String"),
                request
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing required request parameter: userId", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should map malformed JSON to 400 response")
    void shouldMapMalformedJsonTo400Response() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/tickets");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadableException(
                new HttpMessageNotReadableException("Malformed JSON"),
                request
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Malformed JSON request", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should map bad credentials to 401 response")
    void shouldMapBadCredentialsTo401Response() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/login");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleBadCredentialsException(
                new BadCredentialsException("Bad credentials"),
                request
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody().getMessage());
    }
}
