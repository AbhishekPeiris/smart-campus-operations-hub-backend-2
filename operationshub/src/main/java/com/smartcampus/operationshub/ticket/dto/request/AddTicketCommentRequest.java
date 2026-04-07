package com.smartcampus.operationshub.ticket.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddTicketCommentRequest {

    @NotBlank(message = "Comment cannot be empty")
    @Size(min = 2, max = 1000, message = "Comment must be between 2 and 1000 characters")
    private String commentText;
}