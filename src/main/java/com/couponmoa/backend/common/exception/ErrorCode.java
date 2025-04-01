package com.couponmoa.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Getter
public enum ErrorCode {

    //F
    FORBIDDEN_ADMIN_ONLY(FORBIDDEN, "ADMIN 권한을 가진 유저만 접근할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
