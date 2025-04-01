package com.couponmoa.backend.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String status;
    private final int code;
    private final String message;
    private final LocalDateTime timestamp;

}
