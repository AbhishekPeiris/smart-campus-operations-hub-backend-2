package com.smartcampus.operationshub.resource.dto.response;

import java.util.List;

import com.smartcampus.operationshub.common.enums.ResourceStatus;
import com.smartcampus.operationshub.common.enums.ResourceType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceMetadataResponse {

    private List<ResourceType> resourceTypes;
    private List<ResourceStatus> resourceStatuses;
}
