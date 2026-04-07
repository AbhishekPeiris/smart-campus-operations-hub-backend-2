package com.smartcampus.operationshub.common.enums;

public enum TicketPriorityLevel {

    LOW("Low priority issue - does not affect critical operations", 1),
    MEDIUM("Moderate issue - should be resolved within a reasonable time", 2),
    HIGH("High priority - affects important functionality", 3),
    CRITICAL("Critical issue - system is unusable or severely impacted", 4);

    private final String description;
    private final int severityLevel;

    TicketPriorityLevel(String description, int severityLevel) {
        this.description = description;
        this.severityLevel = severityLevel;
    }

    public String getDescription() {
        return description;
    }

    public int getSeverityLevel() {
        return severityLevel;
    }

    public static TicketPriorityLevel fromString(String value) {
        for (TicketPriorityLevel priority : TicketPriorityLevel.values()) {
            if (priority.name().equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority level: " + value);
    }
}