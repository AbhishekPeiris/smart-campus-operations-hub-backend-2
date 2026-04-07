package com.smartcampus.operationshub.ticket.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.smartcampus.operationshub.ticket.model.TicketAttachment;

@Repository
public interface TicketAttachmentRepository extends MongoRepository<TicketAttachment, String> {

    List<TicketAttachment> findByTicketId(String ticketId);

    long countByTicketId(String ticketId);

    void deleteByTicketId(String ticketId);
}