# README_MEMBER_3.md

## Member 3 Scope

Incident tickets, attachments, technician updates, and comments.

## Module: Maintenance & Incident Ticketing

### Ticket Workflow

- OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED
- ADMIN can also set REJECTED with reason

### Attachment Rule

- Up to 3 attachments per ticket

## Endpoints

### Incident Tickets

### 1. Create Ticket

- Method: POST
- Path: /api/v1/tickets?userId={userId}

### 2. Update Ticket

- Method: PUT
- Path: /api/v1/tickets/{ticketId}

### 3. Get Ticket by ID

- Method: GET
- Path: /api/v1/tickets/{ticketId}

### 4. Get All Tickets (paginated)

- Method: GET
- Path: /api/v1/tickets?page=0&size=10

### 5. Assign Technician (ADMIN only)

- Method: PATCH
- Path: /api/v1/tickets/{ticketId}/assign

### 6. Update Ticket Status (TECHNICIAN or ADMIN)

- Method: PATCH
- Path: /api/v1/tickets/{ticketId}/status

### 7. Reject Ticket (ADMIN only)

- Method: PATCH
- Path: /api/v1/tickets/{ticketId}/reject

### 8. Resolve Ticket (TECHNICIAN or ADMIN)

- Method: PATCH
- Path: /api/v1/tickets/{ticketId}/resolve

### Comments

### 9. Add Comment

- Method: POST
- Path: /api/v1/tickets/comments?ticketId={ticketId}

### 10. Update Comment

- Method: PUT
- Path: /api/v1/tickets/comments/{commentId}

### 11. Delete Comment

- Method: DELETE
- Path: /api/v1/tickets/comments/{commentId}

### 12. Get Comments by Ticket

- Method: GET
- Path: /api/v1/tickets/comments/{ticketId}

Comment ownership rule:

- Owner can edit/delete own comments
- ADMIN can edit/delete any comment

### Attachments

### 13. Upload Attachment Metadata

- Method: POST
- Path: /api/v1/tickets/attachments?ticketId={ticketId}&fileName={fileName}&fileType={fileType}&fileUrl={fileUrl}&userId={userId}

### 14. Get Attachments by Ticket

- Method: GET
- Path: /api/v1/tickets/attachments/{ticketId}

### 15. Delete Attachment

- Method: DELETE
- Path: /api/v1/tickets/attachments/{attachmentId}

### Technician Updates

### 16. Get Technician Updates by Ticket

- Method: GET
- Path: /api/v1/tickets/updates/{ticketId}

## Authorization

- Ticket create/update/read: authenticated by role rules in security config
- Status/resolve: TECHNICIAN, ADMIN
- Assign/reject: ADMIN
- Comments: USER, TECHNICIAN, ADMIN (with ownership checks)
- Attachments: USER, TECHNICIAN, ADMIN
