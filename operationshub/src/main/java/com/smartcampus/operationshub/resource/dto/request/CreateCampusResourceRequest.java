package com.smartcampus.operationshub.resource.dto.request;

import java.util.ArrayList;
import java.util.List;

import com.smartcampus.operationshub.common.enums.ResourceStatus;
import com.smartcampus.operationshub.common.enums.ResourceType;
import com.smartcampus.operationshub.resource.model.ResourceAvailabilityWindow;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCampusResourceRequest {

    @NotBlank(message = "Resource code is required")
    private String resourceCode;

    @NotBlank(message = "Resource name is required")
    private String resourceName;

    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotBlank(message = "Location is required")
    private String location;

    @Valid
    private List<ResourceAvailabilityWindow> availabilityWindows = new ArrayList<>();

    private ResourceStatus status;

    private String description;
}
