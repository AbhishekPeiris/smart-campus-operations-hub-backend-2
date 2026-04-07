package com.smartcampus.operationshub.resource.service;

import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.common.enums.ResourceStatus;
import com.smartcampus.operationshub.common.enums.ResourceType;
import com.smartcampus.operationshub.resource.dto.request.CreateCampusResourceRequest;
import com.smartcampus.operationshub.resource.dto.request.UpdateCampusResourceRequest;
import com.smartcampus.operationshub.resource.dto.response.CampusResourceResponse;
import com.smartcampus.operationshub.resource.dto.response.ResourceMetadataResponse;

public interface CampusResourceService {

    CampusResourceResponse createResource(CreateCampusResourceRequest request);

    CampusResourceResponse updateResource(String resourceId, UpdateCampusResourceRequest request);

    CampusResourceResponse updateResourceStatus(String resourceId, ResourceStatus status);

    CampusResourceResponse getResourceById(String resourceId);

    CampusResourceResponse getResourceByCode(String resourceCode);

    ResourceMetadataResponse getResourceMetadata();

    void deleteResource(String resourceId);

    PaginatedResponse<CampusResourceResponse> searchResources(
            ResourceType resourceType,
            Integer minCapacity,
            String location,
            ResourceStatus status,
            int page,
            int size);
}
