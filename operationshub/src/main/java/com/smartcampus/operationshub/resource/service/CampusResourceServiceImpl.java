package com.smartcampus.operationshub.resource.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smartcampus.operationshub.common.dto.PaginatedResponse;
import com.smartcampus.operationshub.common.enums.ResourceStatus;
import com.smartcampus.operationshub.common.enums.ResourceType;
import com.smartcampus.operationshub.common.exception.BadRequestException;
import com.smartcampus.operationshub.common.exception.ResourceNotFoundException;
import com.smartcampus.operationshub.resource.dto.request.CreateCampusResourceRequest;
import com.smartcampus.operationshub.resource.dto.request.UpdateCampusResourceRequest;
import com.smartcampus.operationshub.resource.dto.response.CampusResourceResponse;
import com.smartcampus.operationshub.resource.dto.response.ResourceMetadataResponse;
import com.smartcampus.operationshub.resource.model.CampusResource;
import com.smartcampus.operationshub.resource.model.ResourceAvailabilityWindow;
import com.smartcampus.operationshub.resource.repository.CampusResourceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampusResourceServiceImpl implements CampusResourceService {

    private final CampusResourceRepository campusResourceRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public CampusResourceResponse createResource(CreateCampusResourceRequest request) {
        if (campusResourceRepository.existsByResourceCode(request.getResourceCode())) {
            throw new BadRequestException("A resource with this code already exists");
        }

        validateAvailabilityWindows(request.getAvailabilityWindows());

        CampusResource resource = CampusResource.builder()
                .resourceCode(request.getResourceCode().trim())
                .resourceName(request.getResourceName().trim())
                .resourceType(request.getResourceType())
                .capacity(request.getCapacity())
                .location(request.getLocation().trim())
                .availabilityWindows(request.getAvailabilityWindows())
                .status(request.getStatus() == null ? ResourceStatus.ACTIVE : request.getStatus())
                .description(request.getDescription())
                .build();

        return toResponse(campusResourceRepository.save(resource));
    }

    @Override
    public CampusResourceResponse updateResource(String resourceId, UpdateCampusResourceRequest request) {
        CampusResource resource = findById(resourceId);

        if (StringUtils.hasText(request.getResourceName())) {
            resource.setResourceName(request.getResourceName().trim());
        }
        if (request.getResourceType() != null) {
            resource.setResourceType(request.getResourceType());
        }
        if (request.getCapacity() != null) {
            resource.setCapacity(request.getCapacity());
        }
        if (StringUtils.hasText(request.getLocation())) {
            resource.setLocation(request.getLocation().trim());
        }
        if (request.getAvailabilityWindows() != null) {
            validateAvailabilityWindows(request.getAvailabilityWindows());
            resource.setAvailabilityWindows(request.getAvailabilityWindows());
        }
        if (request.getDescription() != null) {
            resource.setDescription(request.getDescription());
        }

        return toResponse(campusResourceRepository.save(resource));
    }

    @Override
    public CampusResourceResponse updateResourceStatus(String resourceId, ResourceStatus status) {
        CampusResource resource = findById(resourceId);
        resource.setStatus(status);
        return toResponse(campusResourceRepository.save(resource));
    }

    @Override
    public CampusResourceResponse getResourceById(String resourceId) {
        return toResponse(findById(resourceId));
    }

    @Override
    public CampusResourceResponse getResourceByCode(String resourceCode) {
        return toResponse(campusResourceRepository.findByResourceCode(resourceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with code: " + resourceCode)));
    }

    @Override
    public ResourceMetadataResponse getResourceMetadata() {
        return ResourceMetadataResponse.builder()
                .resourceTypes(List.of(ResourceType.values()))
                .resourceStatuses(List.of(ResourceStatus.values()))
                .build();
    }

    @Override
    public void deleteResource(String resourceId) {
        CampusResource resource = findById(resourceId);
        campusResourceRepository.deleteById(resource.getId());
    }

    @Override
    public PaginatedResponse<CampusResourceResponse> searchResources(
            ResourceType resourceType,
            Integer minCapacity,
            String location,
            ResourceStatus status,
            int page,
            int size) {

        List<Criteria> criteriaList = new ArrayList<>();

        if (resourceType != null) {
            criteriaList.add(Criteria.where("resource_type").is(resourceType));
        }
        if (minCapacity != null) {
            criteriaList.add(Criteria.where("capacity").gte(minCapacity));
        }
        if (StringUtils.hasText(location)) {
            criteriaList.add(Criteria.where("location").regex(location.trim(), "i"));
        }
        if (status != null) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, CampusResource.class);

        query.with(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<CampusResourceResponse> content = mongoTemplate.find(query, CampusResource.class)
                .stream()
                .map(this::toResponse)
                .toList();

        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);

        return PaginatedResponse.<CampusResourceResponse>builder()
                .content(content)
                .currentPage(page)
                .totalPages(totalPages)
                .totalElements(total)
                .pageSize(size)
                .hasNext(page + 1 < totalPages)
                .hasPrevious(page > 0)
                .build();
    }

    private void validateAvailabilityWindows(List<ResourceAvailabilityWindow> windows) {
        if (windows == null) {
            return;
        }

        for (ResourceAvailabilityWindow window : windows) {
            LocalTime start = parseTime(window.getStartTime(), "availability start time");
            LocalTime end = parseTime(window.getEndTime(), "availability end time");
            if (!end.isAfter(start)) {
                throw new BadRequestException("Availability end time must be after start time");
            }
        }
    }

    private LocalTime parseTime(String value, String fieldName) {
        try {
            return LocalTime.parse(value);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid " + fieldName + ": " + value);
        }
    }

    private CampusResource findById(String resourceId) {
        return campusResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));
    }

    private CampusResourceResponse toResponse(CampusResource resource) {
        return CampusResourceResponse.builder()
                .id(resource.getId())
                .resourceCode(resource.getResourceCode())
                .resourceName(resource.getResourceName())
                .resourceType(resource.getResourceType())
                .capacity(resource.getCapacity())
                .location(resource.getLocation())
                .availabilityWindows(resource.getAvailabilityWindows())
                .status(resource.getStatus())
                .description(resource.getDescription())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }
}
