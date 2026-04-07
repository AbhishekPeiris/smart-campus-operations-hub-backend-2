package com.smartcampus.operationshub.resource.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.smartcampus.operationshub.common.enums.ResourceStatus;
import com.smartcampus.operationshub.common.enums.ResourceType;
import com.smartcampus.operationshub.resource.model.ResourceAvailabilityWindow;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampusResourceResponse {

    private String id;
    private String resourceCode;
    private String resourceName;
    private ResourceType resourceType;
    private Integer capacity;
    private String location;
    private List<ResourceAvailabilityWindow> availabilityWindows;
    private ResourceStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
