package com.smartcampus.operationshub.common.enums;

public enum ResourceType {

    LECTURE_HALL("Large lecture hall for academic sessions"),
    LABORATORY("Computer or science laboratory"),
    MEETING_ROOM("Small meeting or discussion room"),
    EQUIPMENT("Movable equipment such as projectors, cameras, etc."),
    OFFICE_SPACE("Administrative or staff office space"),
    COMMON_AREA("Shared spaces like corridors, lobbies, etc."),
    LIBRARY("Library or study area"),
    OTHER("Any other type of resource");

    private final String description;

    ResourceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ResourceType fromString(String value) {
        for (ResourceType type : ResourceType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid resource type: " + value);
    }
}