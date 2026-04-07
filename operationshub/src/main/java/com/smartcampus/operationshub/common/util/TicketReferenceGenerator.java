package com.smartcampus.operationshub.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TicketReferenceGenerator {

    public static String generateTicketCode() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String randomPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        return "INC-" + timestamp + "-" + randomPart;
    }
}