# README_MEMBER_4.md

## Member 4 Scope

Notifications, role management enhancements, and OAuth integration improvements.

## Module A: Notifications

### Notification Events

- BOOKING_APPROVED
- BOOKING_REJECTED
- TICKET_STATUS_CHANGED
- TICKET_COMMENT_ADDED

### Endpoints

### 1. Get My Notifications

- Method: GET
- Path: /api/v1/notifications?page=0&size=10&unreadOnly=false

### 2. Get Unread Notification Count

- Method: GET
- Path: /api/v1/notifications/unread-count

### 3. Mark Notification as Read

- Method: PATCH
- Path: /api/v1/notifications/{notificationId}/read

### 4. Mark All Notifications as Read

- Method: PATCH
- Path: /api/v1/notifications/read-all

### 5. Get Notification by ID

- Method: GET
- Path: /api/v1/notifications/{notificationId}

### 6. Delete Notification

- Method: DELETE
- Path: /api/v1/notifications/{notificationId}

## Module B: Role Management Improvements

### 7. Update User Role (ADMIN only)

- Method: PATCH
- Path: /api/v1/users/{id}/role

Request body example:

```json
{
  "role": "TECHNICIAN"
}
```

### 8. Get Available Roles

- Method: GET
- Path: /api/v1/users/roles

## Module C: OAuth Integration Improvements

### 9. Google OAuth Login

- Method: POST
- Path: /api/v1/auth/google

Request body example:

```json
{
  "idToken": "<google-id-token>"
}
```

### 10. Get Google OAuth Config (frontend bootstrap)

- Method: GET
- Path: /api/v1/auth/oauth/google/config

Response example:

```json
{
  "success": true,
  "message": "Google OAuth configuration retrieved",
  "data": {
    "provider": "google",
    "enabled": true,
    "clientId": "1234567890-abc.apps.googleusercontent.com"
  }
}
```

Behavior:

- Validates Google ID token with Google tokeninfo endpoint
- Verifies audience against configured client ID
- Verifies email is verified
- Creates user automatically (default role USER) if not existing
- Returns platform JWT for subsequent API access

Configuration:

- application.security.oauth.google-client-id=${GOOGLE_CLIENT_ID:}

## Authorization

- Notifications endpoints: authenticated users
- Notification delete endpoint: authenticated users
- Update user role: ADMIN only
- Google OAuth endpoint: public
