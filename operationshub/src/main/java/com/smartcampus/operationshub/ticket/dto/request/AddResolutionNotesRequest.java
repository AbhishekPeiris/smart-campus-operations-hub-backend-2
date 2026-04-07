package com.smartcampus.operationshub.ticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddResolutionNotesRequest {

    @NotBlank(message = "Resolution notes are required")
    @Size(min = 5, max = 2000, message = "Resolution notes must be between 5 and 2000 characters")
    private String resolutionNotes;
}