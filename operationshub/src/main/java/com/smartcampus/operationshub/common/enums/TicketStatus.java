package com.smartcampus.operationshub.common.enums;

import java.util.EnumSet;

public enum TicketStatus {

    OPEN("Ticket has been created and is waiting for processing"),
    IN_PROGRESS("Ticket is being actively handled by a technician"),
    RESOLVED("Issue has been resolved, awaiting closure"),
    CLOSED("Ticket has been fully completed and closed"),
    REJECTED("Ticket has been rejected by administrator with a reason");

    private final String description;

    TicketStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Allowed workflow transitions
     */
    public static boolean isValidTransition(TicketStatus currentStatus, TicketStatus newStatus) {

        switch (currentStatus) {

            case OPEN:
                return EnumSet.of(IN_PROGRESS, REJECTED).contains(newStatus);

            case IN_PROGRESS:
                return EnumSet.of(RESOLVED, REJECTED).contains(newStatus);

            case RESOLVED:
                return EnumSet.of(CLOSED).contains(newStatus);

            case CLOSED:
                return false; // No changes allowed

            case REJECTED:
                return false; // No changes allowed

            default:
                return false;
        }
    }

    /**
     * Check if ticket is in final state
     */
    public static boolean isFinalStatus(TicketStatus status) {
        return status == CLOSED || status == REJECTED;
    }
}