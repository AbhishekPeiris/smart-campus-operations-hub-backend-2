# Smart Campus Operations Hub Backend

Spring Boot and MongoDB REST API for authentication, user management, incident ticketing, comments, attachments, and technician update logs.

## Member-specific Documentation

- Member 1 (Facilities catalogue, metadata, delete, code lookup): [README_MEMBER_1.md](README_MEMBER_1.md)
- Member 2 (Booking management, approve/reject shortcuts, conflict check): [README_MEMBER_2.md](README_MEMBER_2.md)
- Member 3 (Incident ticketing): [README_MEMBER_3.md](README_MEMBER_3.md)
- Member 4 (Notifications, roles, OAuth): [README_MEMBER_4.md](README_MEMBER_4.md)

## Tech Stack

- Java 21 (LTS target)
- Spring Boot 3.2.5
- Spring Security with JWT
- Google OAuth 2.0 sign-in (ID token exchange)
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
  - `POST /api/v1/auth/google`
- Protected endpoints:
  - All `/api/v1/users/**`
  - All `/api/v1/tickets/**`
  - All `/api/v1/resources/**`
  - All `/api/v1/bookings/**`
  - All `/api/v1/notifications/**`
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
- Resource status: `ACTIVE`, `OUT_OF_SERVICE`
- Booking status: `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`
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
- Notification type:
  - `BOOKING_APPROVED`
  - `BOOKING_REJECTED`
  - `TICKET_STATUS_CHANGED`
  - `TICKET_COMMENT_ADDED`

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

3. Google OAuth login

- Method: `POST`
- Path: `/api/v1/auth/google`

Request body:

```json
{
  "idToken": "<google-id-token>"
}
```

Response example:

```json
{
  "success": true,
  "message": "Google login successful",
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

7. Update user role (ADMIN only)

- Method: `PATCH`
- Path: `/api/v1/users/{id}/role`

Request body:

```json
{
  "role": "TECHNICIAN"
}
```

### Resource Catalogue Endpoints

8. Create resource (ADMIN only)

- Method: `POST`
- Path: `/api/v1/resources`

Request body example:

```json
{
  "resourceCode": "LH-A-01",
  "resourceName": "Lecture Hall A",
  "resourceType": "LECTURE_HALL",
  "capacity": 120,
  "location": "Engineering Block - Floor 1",
  "status": "ACTIVE",
  "availabilityWindows": [
    {
      "dayOfWeek": "MONDAY",
      "startTime": "08:00",
      "endTime": "18:00"
    }
  ],
  "description": "Main lecture hall with projector and audio system"
}
```

9. Update resource (ADMIN only)

- Method: `PUT`
- Path: `/api/v1/resources/{resourceId}`

10. Update resource status (ADMIN only)

- Method: `PATCH`
- Path: `/api/v1/resources/{resourceId}/status?status=OUT_OF_SERVICE`

11. Get resource by id

- Method: `GET`
- Path: `/api/v1/resources/{resourceId}`

12. Search resources

- Method: `GET`
- Path: `/api/v1/resources?resourceType=LABORATORY&minCapacity=30&location=Block%20B&status=ACTIVE&page=0&size=10`

### Booking Endpoints

13. Create booking request

- Method: `POST`
- Path: `/api/v1/bookings`

Request body:

```json
{
  "resourceId": "67f2aa4bf5b7ce5c7f949221",
  "bookingDate": "2026-04-15",
  "startTime": "10:00",
  "endTime": "12:00",
  "purpose": "Final year project presentation",
  "expectedAttendees": 45
}
```

14. Review booking (ADMIN only)

- Method: `PATCH`
- Path: `/api/v1/bookings/{bookingId}/review`

Request body:

```json
{
  "decision": "APPROVED",
  "reason": "Approved for scheduled academic activity"
}
```

15. Cancel approved booking (owner or ADMIN)

- Method: `PATCH`
- Path: `/api/v1/bookings/{bookingId}/cancel`

Request body:

```json
{
  "reason": "Event postponed"
}
```

16. Get booking by id (owner or ADMIN)

- Method: `GET`
- Path: `/api/v1/bookings/{bookingId}`

17. Get my bookings

- Method: `GET`
- Path: `/api/v1/bookings/my?status=APPROVED&bookingDate=2026-04-15&page=0&size=10`

18. Get all bookings (ADMIN only)

- Method: `GET`
- Path: `/api/v1/bookings?status=PENDING&resourceId={resourceId}&requestedByUserId={userId}&bookingDate=2026-04-15&page=0&size=10`

### Ticket Endpoints

### Ticket Endpoints

19. Create incident ticket

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

20. Update ticket

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

21. Get ticket by id

- Method: `GET`
- Path: `/api/v1/tickets/{ticketId}`

Response contains ticket + nested `comments`, `attachments`, and `technicianUpdates`.

22. Get all tickets (paginated)

- Method: `GET`
- Path: `/api/v1/tickets?page=0&size=10`

23. Assign technician

- Method: `PATCH`
- Path: `/api/v1/tickets/{ticketId}/assign`

Request body:

```json
{
  "technicianUserId": "67f1b8b2f5b7ce5c7f949010",
  "technicianName": "Tech User"
}
```

24. Update ticket status

- Method: `PATCH`
- Path: `/api/v1/tickets/{ticketId}/status`

Request body:

```json
{
  "newStatus": "IN_PROGRESS",
  "updateMessage": "Technician has started diagnosis"
}
```

25. Reject ticket

- Method: `PATCH`
- Path: `/api/v1/tickets/{ticketId}/reject`

Request body:

```json
{
  "rejectionReason": "Invalid incident details"
}
```

26. Resolve ticket (add resolution notes)

- Method: `PATCH`
- Path: `/api/v1/tickets/{ticketId}/resolve`

Request body:

```json
{
  "resolutionNotes": "Replaced faulty power module and tested successfully."
}
```

### Ticket Comment Endpoints

### Ticket Comment Endpoints

27. Add comment

- Method: `POST`
- Path: `/api/v1/tickets/comments?ticketId={ticketId}`

Request body:

```json
{
  "commentText": "Please prioritize this issue"
}
```

28. Update comment

- Method: `PUT`
- Path: `/api/v1/tickets/comments/{commentId}`

Request body:

```json
{
  "commentText": "Updated comment text"
}
```

29. Delete comment

- Method: `DELETE`
- Path: `/api/v1/tickets/comments/{commentId}`

30. Get comments by ticket

- Method: `GET`
- Path: `/api/v1/tickets/comments/{ticketId}`

### Ticket Attachment Endpoints

### Ticket Attachment Endpoints

31. Upload attachment metadata

- Method: `POST`
- Path: `/api/v1/tickets/attachments?ticketId={ticketId}&fileName={fileName}&fileType={fileType}&fileUrl={fileUrl}&userId={userId}`

Example:

```text
/api/v1/tickets/attachments?ticketId=67f1bb6af5b7ce5c7f949111&fileName=damage.jpg&fileType=image/jpeg&fileUrl=http://localhost:8081/uploads/tickets/damage.jpg&userId=67f1b8b2f5b7ce5c7f949001
```

32. Get attachments by ticket

- Method: `GET`
- Path: `/api/v1/tickets/attachments/{ticketId}`

33. Delete attachment

- Method: `DELETE`
- Path: `/api/v1/tickets/attachments/{attachmentId}`

### Technician Update Log Endpoint

### Technician Update Log Endpoint

34. Get technician updates by ticket

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

Create booking:

```bash
curl -X POST "http://localhost:8081/api/v1/bookings" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"resourceId":"67f2aa4bf5b7ce5c7f949221","bookingDate":"2026-04-15","startTime":"10:00","endTime":"12:00","purpose":"Final year project presentation","expectedAttendees":45}'
```

Review booking (admin):

```bash
curl -X PATCH "http://localhost:8081/api/v1/bookings/67f2ac31f5b7ce5c7f949301/review" \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{"decision":"APPROVED","reason":"Approved for scheduled academic activity"}'
```

## Authorization Summary

- `USER`:
  - Can create tickets
  - Can read tickets
  - Can create booking requests
  - Can cancel own approved bookings
  - Can view own bookings
  - Can create, update, delete comments
  - Can create and delete attachments
- `TECHNICIAN`:
  - Can read users (role endpoint for technicians allowed)
  - Can create booking requests
  - Can view own bookings
  - Can update ticket status and resolve tickets
  - Can read all ticket-related endpoints
- `ADMIN`:
  - Full access to assignment, rejection, status changes, and user status updates
  - Full access to resources catalogue management
  - Can review booking requests (approve/reject)
  - Can view all bookings and filter bookings
  - Can update user roles

## Notification Behavior

- Booking requester receives notifications when booking is approved or rejected.
- Ticket owner receives notifications when ticket status changes.
- Ticket owner receives notifications when someone else adds a comment to their ticket.
- Notification API:
  - `GET /api/v1/notifications`
  - `GET /api/v1/notifications/unread-count`
  - `PATCH /api/v1/notifications/{notificationId}/read`
  - `PATCH /api/v1/notifications/read-all`

## Notes

- If MongoDB is not running, the app can start but Mongo operations will fail at runtime.
- DevTools restart is disabled by default for stable local startup.
