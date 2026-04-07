package com.smartcampus.operationshub.resource.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.smartcampus.operationshub.common.enums.ResourceStatus;
import com.smartcampus.operationshub.common.enums.ResourceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "campus_resources")
public class CampusResource {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("resource_code")
    private String resourceCode;

    @Indexed
    @Field("resource_name")
    private String resourceName;

    @Indexed
    @Field("resource_type")
    private ResourceType resourceType;

    @Field("capacity")
    private Integer capacity;

    @Indexed
    @Field("location")
    private String location;

    @Field("availability_windows")
    @Builder.Default
    private List<ResourceAvailabilityWindow> availabilityWindows = new ArrayList<>();

    @Indexed
    @Field("status")
    @Builder.Default
    private ResourceStatus status = ResourceStatus.ACTIVE;

    @Field("description")
    private String description;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
