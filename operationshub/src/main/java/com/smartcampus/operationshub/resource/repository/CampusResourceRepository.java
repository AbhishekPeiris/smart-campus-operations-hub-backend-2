package com.smartcampus.operationshub.resource.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.smartcampus.operationshub.resource.model.CampusResource;

@Repository
public interface CampusResourceRepository extends MongoRepository<CampusResource, String> {

    boolean existsByResourceCode(String resourceCode);

    Optional<CampusResource> findByResourceCode(String resourceCode);
}
