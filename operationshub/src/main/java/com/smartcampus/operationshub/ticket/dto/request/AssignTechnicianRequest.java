package com.smartcampus.operationshub.ticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignTechnicianRequest {

    @NotBlank(message = "Technician user ID is required")
    private String technicianUserId;

    @NotBlank(message = "Technician name is required")
    private String technicianName;
}