package com.smartcampus.operationshub.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.smartcampus.operationshub.common.enums.UserRole;
import com.smartcampus.operationshub.user.model.UserAccount;

@Repository
public interface UserAccountRepository extends MongoRepository<UserAccount, String> {

    Optional<UserAccount> findByUniversityEmailAddress(String universityEmailAddress);

    boolean existsByUniversityEmailAddress(String universityEmailAddress);

    List<UserAccount> findByRole(UserRole role);

    List<UserAccount> findByAccountEnabled(Boolean accountEnabled);
}