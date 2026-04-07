package com.smartcampus.operationshub.resource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartcampus.operationshub.common.dto.ApiSuccessResponse;
import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.common.enums.ResourceStatus;
import com.smartcampus.operationshub.common.enums.ResourceType;
import com.smartcampus.operationshub.resource.dto.request.CreateCampusResourceRequest;
import com.smartcampus.operationshub.resource.dto.request.UpdateCampusResourceRequest;
import com.smartcampus.operationshub.resource.dto.response.CampusResourceResponse;
import com.smartcampus.operationshub.resource.dto.response.ResourceMetadataResponse;
import com.smartcampus.operationshub.resource.service.CampusResourceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class CampusResourceController {

    private final CampusResourceService campusResourceService;

    @PostMapping
    public ApiSuccessResponse<CampusResourceResponse> createResource(
            @Valid @RequestBody CreateCampusResourceRequest request) {
        return ApiSuccessResponse.<CampusResourceResponse>builder()
                .success(true)
                .message("Resource created")
                .data(campusResourceService.createResource(request))
                .build();
    }

    @PutMapping("/{resourceId}")
    public ApiSuccessResponse<CampusResourceResponse> updateResource(
            @PathVariable String resourceId,
            @Valid @RequestBody UpdateCampusResourceRequest request) {
        return ApiSuccessResponse.<CampusResourceResponse>builder()
                .success(true)
                .message("Resource updated")
                .data(campusResourceService.updateResource(resourceId, request))
                .build();
    }

    @PatchMapping("/{resourceId}/status")
    public ApiSuccessResponse<CampusResourceResponse> updateResourceStatus(
            @PathVariable String resourceId,
            @RequestParam ResourceStatus status) {
        return ApiSuccessResponse.<CampusResourceResponse>builder()
                .success(true)
                .message("Resource status updated")
                .data(campusResourceService.updateResourceStatus(resourceId, status))
                .build();
    }

    @GetMapping("/{resourceId}")
    public ApiSuccessResponse<CampusResourceResponse> getResourceById(@PathVariable String resourceId) {
        return ApiSuccessResponse.<CampusResourceResponse>builder()
                .success(true)
                .message("Resource retrieved")
                .data(campusResourceService.getResourceById(resourceId))
                .build();
    }

        @GetMapping("/code/{resourceCode}")
        public ApiSuccessResponse<CampusResourceResponse> getResourceByCode(@PathVariable String resourceCode) {
                return ApiSuccessResponse.<CampusResourceResponse>builder()
                                .success(true)
                                .message("Resource retrieved")
                                .data(campusResourceService.getResourceByCode(resourceCode))
                                .build();
        }

        @GetMapping("/metadata/options")
        public ApiSuccessResponse<ResourceMetadataResponse> getResourceMetadata() {
                return ApiSuccessResponse.<ResourceMetadataResponse>builder()
                                .success(true)
                                .message("Resource metadata retrieved")
                                .data(campusResourceService.getResourceMetadata())
                                .build();
        }

        @DeleteMapping("/{resourceId}")
        public ApiSuccessResponse<String> deleteResource(@PathVariable String resourceId) {
                campusResourceService.deleteResource(resourceId);
                return ApiSuccessResponse.<String>builder()
                                .success(true)
                                .message("Resource deleted")
                                .data(null)
                                .build();
        }

    @GetMapping
    public ApiSuccessResponse<PaginatedResponse<CampusResourceResponse>> searchResources(
            @RequestParam(required = false) ResourceType resourceType,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) ResourceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiSuccessResponse.<PaginatedResponse<CampusResourceResponse>>builder()
                .success(true)
                .message("Resources retrieved")
                .data(campusResourceService.searchResources(resourceType, minCapacity, location, status, page, size))
                .build();
    }
}
