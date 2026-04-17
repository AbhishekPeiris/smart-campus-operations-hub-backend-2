package com.smartcampus.operationshub.user.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.smartcampus.operationshub.common.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_accounts")
public class UserAccount {

    @Id
    private String id;

    @Field("full_name")
    private String fullName;

    @Indexed(unique = true)
    @Field("university_email_address")
    private String universityEmailAddress;

    @Indexed(unique = true, sparse = true)
    @Field("google_subject_id")
    private String googleSubjectId;

    @Field("password_hash")
    private String passwordHash;

    @Field("contact_number")
    private String contactNumber;

    @Field("role")
    private UserRole role;

    @Builder.Default
    @Field("account_enabled")
    private Boolean accountEnabled = true;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
