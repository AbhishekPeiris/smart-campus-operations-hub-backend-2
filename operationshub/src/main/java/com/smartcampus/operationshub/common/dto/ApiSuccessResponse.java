package com.smartcampus.operationshub.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiSuccessResponse<T> {

    private boolean success;
    private String message;
    private T data;
}