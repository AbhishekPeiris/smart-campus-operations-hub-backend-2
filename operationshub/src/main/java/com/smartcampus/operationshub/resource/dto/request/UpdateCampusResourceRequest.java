package com.smartcampus.operationshub.resource.dto.request;

import java.util.List;

import com.smartcampus.operationshub.common.enums.ResourceType;
import com.smartcampus.operationshub.resource.model.ResourceAvailabilityWindow;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateCampusResourceRequest {

    private String resourceName;

    private ResourceType resourceType;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String location;

    @Valid
    private List<ResourceAvailabilityWindow> availabilityWindows;

    private String description;
}
