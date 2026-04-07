package com.smartcampus.operationshub.ticket.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ticket_attachments")
public class TicketAttachment {

    @Id
    private String id;

    @Indexed
    @Field("ticket_id")
    private String ticketId;

    @Field("original_file_name")
    private String originalFileName;

    @Indexed(unique = true)
    @Field("stored_file_name")
    private String storedFileName;

    @Field("file_content_type")
    private String fileContentType;

    @Field("file_size_in_bytes")
    private Long fileSizeInBytes;

    @Field("file_access_url")
    private String fileAccessUrl;

    @Field("uploaded_by_user_id")
    private String uploadedByUserId;

    @Field("uploaded_by_name")
    private String uploadedByName;

    @CreatedDate
    @Field("uploaded_at")
    private LocalDateTime uploadedAt;
}