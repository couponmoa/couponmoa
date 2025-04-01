package com.couponmoa.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {
    private final HttpStatus status;

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getHttpStatus();
    }

    public ApplicationException(ErrorCode errorCode, String message) {
        super(message);
        this.status = errorCode.getHttpStatus();
    }
}
