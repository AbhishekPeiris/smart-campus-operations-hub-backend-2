# Smart Campus Operations Hub Backend

Spring Boot and MongoDB REST API for authentication, user management, incident ticketing, comments, attachments, and technician update logs.

## Tech Stack

- Java 21 (LTS target)
- Spring Boot 3.2.5
- Spring Security with JWT
- Spring Data MongoDB
- Maven
- OpenAPI (springdoc)

## Prerequisites

- Java 21 installed
- MongoDB running locally or a remote MongoDB URI
- Maven available in terminal

## Run Locally

1. Start MongoDB (default used by app):

```text
mongodb://localhost:27017/smart_campus_operations_hub
```

2. Run from repository root:

```bash
mvn clean -DskipTests spring-boot:run
```

3. Application URLs:

- Base URL: `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

## Configuration

Current defaults from `application.yml`:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/smart_campus_operations_hub}
  devtools:
    restart:
      enabled: false

server:
  port: 8081

application:
  security:
    jwt:
      secret-key: smart_campus_operations_hub_123456789
      expiration: 86400000

file:
  upload-dir: uploads/tickets
  max-attachments-per-ticket: 3
  max-file-size: 5242880
```

## Authentication

- Public endpoints:
  - `POST /api/v1/auth/register`
  - `POST /api/v1/auth/login`
- Protected endpoints:
  - All `/api/v1/users/**`
  - All `/api/v1/tickets/**`
- Authorization header:

```text
Authorization: Bearer <accessToken>
```

## Standard Response Format

All successful endpoints return:

```json
{
  "success": true,
  "message": "Operation message",
  "data": {}
}
```

Paginated endpoints return:

```json
{
  "success": true,
  "message": "Items retrieved",
  "data": {
    "content": [],
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 10,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

## Enum Values

- User roles: `USER`, `TECHNICIAN`, `ADMIN`
- Incident categories:
  - `HARDWARE_ISSUE`
  - `SOFTWARE_ISSUE`
  - `NETWORK_ISSUE`
  - `ELECTRICAL_ISSUE`
  - `FACILITY_DAMAGE`
  - `SAFETY_CONCERN`
  - `CLEANLINESS_ISSUE`
  - `OTHER`
- Ticket priority: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`
- Ticket status: `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `REJECTED`
- Resource type:
  - `LECTURE_HALL`
  - `LABORATORY`
  - `MEETING_ROOM`
  - `EQUIPMENT`
  - `OFFICE_SPACE`
  - `COMMON_AREA`
  - `LIBRARY`
  - `OTHER`

## API Endpoints

### Auth Endpoints

1. Register user

- Method: `POST`
- Path: `/api/v1/auth/register`

Request body:

```json
{
  "fullName": "John Doe",
  "universityEmailAddress": "john@uni.com",
  "password": "123456",
  "contactNumber": "+94771234567",
  "role": "USER"
}
```

Response example:

```json
{
  "success": true,
  "message": "User registered successfully",
  "data": null
}
```

2. Login

- Method: `POST`
- Path: `/api/v1/auth/login`

Request body:

```json
{
  "universityEmailAddress": "john@uni.com",
  "password": "123456"
}
```

Response example:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "<jwt-token>",
    "tokenType": "Bearer",
    "userId": "67f1b8b2f5b7ce5c7f949001",
    "fullName": "John Doe",
    "universityEmailAddress": "john@uni.com",
    "role": "USER"
  }
}
```

### User Endpoints

3. Get user profile

- Method: `GET`
- Path: `/api/v1/users/{id}`

Response example:

```json
{
  "success": true,
  "message": "User profile retrieved",
  "data": {
    "id": "67f1b8b2f5b7ce5c7f949001",
    "fullName": "John Doe",
    "universityEmailAddress": "john@uni.com",
    "contactNumber": "+94771234567",
    "role": "USER",
    "accountEnabled": true,
    "createdAt": "2026-04-06T00:00:00",
    "updatedAt": "2026-04-06T00:00:00"
  }
}
```

4. Get all users (paginated)

- Method: `GET`
- Path: `/api/v1/users?page=0&size=10`

Response example:

```json
{
  "success": true,
  "message": "Users retrieved",
  "data": {
    "content": [
      {
        "id": "67f1b8b2f5b7ce5c7f949001",
        "fullName": "John Doe",
        "universityEmailAddress": "john@uni.com",
        "role": "USER"
      }
    ],
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 10,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

5. Get users by role

- Method: `GET`
- Path: `/api/v1/users/role/{role}`
- Example: `/api/v1/users/role/TECHNICIAN`

Response example:

```json
{
  "success": true,
  "message": "Users by role retrieved",
  "data": [
    {
      "id": "67f1b8b2f5b7ce5c7f949010",
      "fullName": "Tech User",
      "universityEmailAddress": "tech@uni.com",
      "role": "TECHNICIAN"
    }
  ]
}
```

6. Update user status

- Method: `PATCH`
- Path: `/api/v1/users/{id}/status?enabled=false`

Response example:

```json
{
  "success": true,
  "message": "User status updated",
  "data": null
}
```

### Ticket Endpoints

7. Create incident ticket

- Method: `POST`
- Path: `/api/v1/tickets?userId={userId}`
- Important: provide either resource fields OR location fields, not both.

Request body example (resource-based):

```json
{
  "incidentCategory": "HARDWARE_ISSUE",
  "ticketTitle": "Projector is not turning on",
  "description": "Projector in Lecture Hall A does not power on after multiple attempts.",
  "priorityLevel": "HIGH",
  "preferredContactName": "John Doe",
  "preferredContactEmailAddress": "john@uni.com",
  "preferredContactPhoneNumber": "+94771234567",
  "resourceIdentifier": "RES-PRJ-001",
  "resourceName": "Epson Projector",
  "resourceType": "EQUIPMENT"
}
```

Response example:

```json
{
  "success": true,
  "message": "Ticket created",
  "data": {
    "id": "67f1bb6af5b7ce5c7f949111",
    "ticketCode": "INC-20260406-0001",
    "ticketTitle": "Projector is not turning on",
    "status": "OPEN",
    "priorityLevel": "HIGH",
    "createdByUserId": "67f1b8b2f5b7ce5c7f949001",
    "createdByName": "John Doe",
    "assignedTechnicianId": null,
    "assignedTechnicianName": null,
    "createdAt": "2026-04-06T00:10:00",
    "updatedAt": "2026-04-06T00:10:00"
  }
}
```

8. Update ticket

- Method: `PUT`
- Path: `/api/v1/tickets/{ticketId}`

Request body example:

```json
{
  "ticketTitle": "Projector still not working",
  "description": "Issue persists after power reset.",
  "priorityLevel": "CRITICAL",
  "preferredContactName": "John D",
  "preferredContactEmailAddress": "john@uni.com",
  "preferredContactPhoneNumber": "+94770000000"
}
```

9. Get ticket by id

- Method: `GET`
- Path: `/api/v1/tickets/{ticketId}`

Response contains ticket + nested `comments`, `attachments`, and `technicianUpdates`.

10. Get all tickets (paginated)

- Method: `GET`
- Path: `/api/v1/tickets?page=0&size=10`

11. Assign technician

- Method: `PATCH`
- Path: `/api/v1/tickets/{ticketId}/assign`

Request body:

```json
{
  "technicianUserId": "67f1b8b2f5b7ce5c7f949010",
  "technicianName": "Tech User"
}
```

12. Update ticket status

- Method: `PATCH`
- Path: `/api/v1/tickets/{ticketId}/status`

Request body:

```json
{
  "newStatus": "IN_PROGRESS",
  "updateMessage": "Technician has started diagnosis"
}
```

13. Reject ticket

- Method: `PATCH`
- Path: `/api/v1/tickets/{ticketId}/reject`

Request body:

```json
{
  "rejectionReason": "Invalid incident details"
}
```

14. Resolve ticket (add resolution notes)

- Method: `PATCH`
- Path: `/api/v1/tickets/{ticketId}/resolve`

Request body:

```json
{
  "resolutionNotes": "Replaced faulty power module and tested successfully."
}
```

### Ticket Comment Endpoints

15. Add comment

- Method: `POST`
- Path: `/api/v1/tickets/comments?ticketId={ticketId}&userId={userId}`

Request body:

```json
{
  "commentText": "Please prioritize this issue"
}
```

16. Update comment

- Method: `PUT`
- Path: `/api/v1/tickets/comments/{commentId}`

Request body:

```json
{
  "commentText": "Updated comment text"
}
```

17. Delete comment

- Method: `DELETE`
- Path: `/api/v1/tickets/comments/{commentId}`

18. Get comments by ticket

- Method: `GET`
- Path: `/api/v1/tickets/comments/{ticketId}`

### Ticket Attachment Endpoints

19. Upload attachment metadata

- Method: `POST`
- Path: `/api/v1/tickets/attachments?ticketId={ticketId}&fileName={fileName}&fileType={fileType}&fileUrl={fileUrl}&userId={userId}`

Example:

```text
/api/v1/tickets/attachments?ticketId=67f1bb6af5b7ce5c7f949111&fileName=damage.jpg&fileType=image/jpeg&fileUrl=http://localhost:8081/uploads/tickets/damage.jpg&userId=67f1b8b2f5b7ce5c7f949001
```

20. Get attachments by ticket

- Method: `GET`
- Path: `/api/v1/tickets/attachments/{ticketId}`

21. Delete attachment

- Method: `DELETE`
- Path: `/api/v1/tickets/attachments/{attachmentId}`

### Technician Update Log Endpoint

22. Get technician updates by ticket

- Method: `GET`
- Path: `/api/v1/tickets/updates/{ticketId}`

## Quick cURL Examples

Register:

```bash
curl -X POST "http://localhost:8081/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"John Doe","universityEmailAddress":"john@uni.com","password":"123456","contactNumber":"+94771234567","role":"USER"}'
```

Login:

```bash
curl -X POST "http://localhost:8081/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"universityEmailAddress":"john@uni.com","password":"123456"}'
```

Create ticket:

```bash
curl -X POST "http://localhost:8081/api/v1/tickets?userId=67f1b8b2f5b7ce5c7f949001" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"incidentCategory":"HARDWARE_ISSUE","ticketTitle":"Projector is not turning on","description":"Projector in Lecture Hall A does not power on after multiple attempts.","priorityLevel":"HIGH","preferredContactName":"John Doe","preferredContactEmailAddress":"john@uni.com","preferredContactPhoneNumber":"+94771234567","resourceIdentifier":"RES-PRJ-001","resourceName":"Epson Projector","resourceType":"EQUIPMENT"}'
```

## Authorization Summary

- `USER`:
  - Can create tickets
  - Can read tickets
  - Can create, update, delete comments
  - Can create and delete attachments
- `TECHNICIAN`:
  - Can read users (role endpoint for technicians allowed)
  - Can update ticket status and resolve tickets
  - Can read all ticket-related endpoints
- `ADMIN`:
  - Full access to assignment, rejection, status changes, and user status updates

## Notes

- If MongoDB is not running, the app can start but Mongo operations will fail at runtime.
- DevTools restart is disabled by default for stable local startup.
