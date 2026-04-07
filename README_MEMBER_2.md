# README_MEMBER_2.md

## Member 2 Scope

Booking workflow and scheduling conflict checking.

## Module: Booking Management

### Booking Workflow

- PENDING -> APPROVED
- PENDING -> REJECTED
- APPROVED -> CANCELLED

### Conflict Rule

The system prevents overlapping bookings for the same resource on the same date.

Overlap check logic:

- existing.startTime < new.endTime
- existing.endTime > new.startTime
- status in (PENDING, APPROVED)

## Endpoints

### 1. Create Booking Request

- Method: POST
- Path: /api/v1/bookings

Request body example:

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

### 2. Review Booking (ADMIN only)

- Method: PATCH
- Path: /api/v1/bookings/{bookingId}/review

Request body example:

```json
{
  "decision": "APPROVED",
  "reason": "Approved for scheduled academic activity"
}
```

### 3. Cancel Approved Booking (owner or ADMIN)

- Method: PATCH
- Path: /api/v1/bookings/{bookingId}/cancel

Request body example:

```json
{
  "reason": "Event postponed"
}
```

### 4. Get Booking by ID (owner or ADMIN)

- Method: GET
- Path: /api/v1/bookings/{bookingId}

### 5. Get My Bookings

- Method: GET
- Path: /api/v1/bookings/my?status=APPROVED&bookingDate=2026-04-15&page=0&size=10

### 6. Get All Bookings (ADMIN only)

- Method: GET
- Path: /api/v1/bookings?status=PENDING&resourceId={resourceId}&requestedByUserId={userId}&bookingDate=2026-04-15&page=0&size=10

Supported query filters:

- status
- resourceId
- requestedByUserId
- bookingDate
- page
- size

### 7. Approve Booking (ADMIN only)

- Method: PATCH
- Path: /api/v1/bookings/{bookingId}/approve

Request body example:

```json
{
  "reason": "Approved for timetable compliance"
}
```

### 8. Reject Booking (ADMIN only)

- Method: PATCH
- Path: /api/v1/bookings/{bookingId}/reject

Request body example:

```json
{
  "reason": "Resource reserved for an exam session"
}
```

### 9. Get Bookings by Resource

- Method: GET
- Path: /api/v1/bookings/resource/{resourceId}?bookingDate=2026-04-15&status=APPROVED&page=0&size=10

### 10. Check Booking Conflicts (pre-check endpoint)

- Method: GET
- Path: /api/v1/bookings/conflicts?resourceId={resourceId}&bookingDate=2026-04-15&startTime=10:00&endTime=12:00

Response example:

```json
{
  "success": true,
  "message": "Booking conflict check completed",
  "data": {
    "conflict": true,
    "conflictingBookings": [
      {
        "bookingId": "67f2ac31f5b7ce5c7f949301",
        "resourceId": "67f2aa4bf5b7ce5c7f949221",
        "bookingDate": "2026-04-15",
        "startTime": "09:30",
        "endTime": "11:00",
        "status": "APPROVED",
        "requestedByName": "Jane Doe"
      }
    ]
  }
}
```

## Authorization

- POST /api/v1/bookings: USER, TECHNICIAN, ADMIN
- GET /api/v1/bookings/my: authenticated users
- GET /api/v1/bookings/{bookingId}: owner or ADMIN
- GET /api/v1/bookings: ADMIN only
- PATCH /api/v1/bookings/{bookingId}/review: ADMIN only
- PATCH /api/v1/bookings/{bookingId}/approve: ADMIN only
- PATCH /api/v1/bookings/{bookingId}/reject: ADMIN only
- PATCH /api/v1/bookings/{bookingId}/cancel: owner or ADMIN
