package com.smartcampus.operationshub.ticket.model;

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
@Document(collection = "ticket_comments")
public class TicketComment {

    @Id
    private String id;

    @Indexed
    @Field("ticket_id")
    private String ticketId;

    @Field("comment_text")
    private String commentText;

    @Indexed
    @Field("comment_owner_user_id")
    private String commentOwnerUserId;

    @Field("comment_owner_name")
    private String commentOwnerName;

    @Field("comment_owner_role")
    private UserRole commentOwnerRole;

    @Builder.Default
    @Field("edited")
    private Boolean edited = false;

    @CreatedDate
    @Indexed
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}