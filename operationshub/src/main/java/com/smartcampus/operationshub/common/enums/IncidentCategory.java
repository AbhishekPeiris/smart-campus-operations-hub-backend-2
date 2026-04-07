package com.smartcampus.operationshub.common.enums;

public enum IncidentCategory {

    HARDWARE_ISSUE("Physical equipment damage or malfunction"),
    SOFTWARE_ISSUE("Application or system software problem"),
    NETWORK_ISSUE("Internet or internal network connectivity issue"),
    ELECTRICAL_ISSUE("Power failure or electrical malfunction"),
    FACILITY_DAMAGE("Damage to physical infrastructure like walls, doors, etc."),
    SAFETY_CONCERN("Security or safety-related incident"),
    CLEANLINESS_ISSUE("Cleaning or hygiene-related problem"),
    OTHER("Any other type of issue not listed above");

    private final String description;

    IncidentCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static IncidentCategory fromString(String value) {
        for (IncidentCategory category : IncidentCategory.values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid incident category: " + value);
    }
}