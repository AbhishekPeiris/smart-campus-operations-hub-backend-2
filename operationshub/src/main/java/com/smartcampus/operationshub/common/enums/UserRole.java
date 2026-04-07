package com.smartcampus.operationshub.common.enums;

public enum UserRole {

    USER("Standard system user who reports incidents"),
    TECHNICIAN("Staff member responsible for handling and resolving incidents"),
    ADMIN("Administrator with full system access and management permissions");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isValidRole(String role) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }
}