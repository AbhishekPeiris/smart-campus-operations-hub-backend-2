package com.smartcampus.operationshub.ticket.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.smartcampus.operationshub.ticket.model.TicketComment;

@Repository
public interface TicketCommentRepository extends MongoRepository<TicketComment, String> {

    List<TicketComment> findByTicketIdOrderByCreatedAtAsc(String ticketId);

    List<TicketComment> findByCommentOwnerUserId(String userId);

    void deleteByTicketId(String ticketId);
}