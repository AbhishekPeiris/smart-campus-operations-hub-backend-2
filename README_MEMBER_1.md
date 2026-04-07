# README_MEMBER_1.md

## Member 1 Scope

Facilities catalogue and resource management endpoints.

## Module: Facilities & Assets Catalogue

### Supported Resource Types

- LECTURE_HALL
- LABORATORY
- MEETING_ROOM
- EQUIPMENT
- OFFICE_SPACE
- COMMON_AREA
- LIBRARY
- OTHER

### Resource Status

- ACTIVE
- OUT_OF_SERVICE

### Resource Metadata

Each resource supports:

- resourceCode
- resourceName
- resourceType
- capacity
- location
- availabilityWindows (dayOfWeek, startTime, endTime)
- status
- description

## Endpoints

### 1. Create Resource (ADMIN only)

- Method: POST
- Path: /api/v1/resources

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

### 2. Update Resource (ADMIN only)

- Method: PUT
- Path: /api/v1/resources/{resourceId}

### 3. Update Resource Status (ADMIN only)

- Method: PATCH
- Path: /api/v1/resources/{resourceId}/status?status=OUT_OF_SERVICE

### 4. Get Resource by ID

- Method: GET
- Path: /api/v1/resources/{resourceId}

### 5. Search and Filter Resources

- Method: GET
- Path: /api/v1/resources?resourceType=LABORATORY&minCapacity=30&location=Block%20B&status=ACTIVE&page=0&size=10

Supported query filters:

- resourceType
- minCapacity
- location
- status
- page
- size

### 6. Get Resource by Code

- Method: GET
- Path: /api/v1/resources/code/{resourceCode}

Example:

```text
/api/v1/resources/code/LH-A-01
```

### 7. Get Resource Metadata Options

- Method: GET
- Path: /api/v1/resources/metadata/options

Response example:

```json
{
  "success": true,
  "message": "Resource metadata retrieved",
  "data": {
    "resourceTypes": [
      "LECTURE_HALL",
      "LABORATORY",
      "MEETING_ROOM",
      "EQUIPMENT",
      "OFFICE_SPACE",
      "COMMON_AREA",
      "LIBRARY",
      "OTHER"
    ],
    "resourceStatuses": ["ACTIVE", "OUT_OF_SERVICE"]
  }
}
```

### 8. Delete Resource (ADMIN only)

- Method: DELETE
- Path: /api/v1/resources/{resourceId}

Example:

```text
/api/v1/resources/67f2aa4bf5b7ce5c7f949221
```

## Authorization

- GET /api/v1/resources/\*\*: authenticated users
- POST/PUT/PATCH/DELETE /api/v1/resources/\*\*: ADMIN only
