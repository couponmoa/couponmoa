package com.couponmoa.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum ErrorCode {

    //
    NOT_FOUNT_COUPON(NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    NOT_FOUNT_USER(NOT_FOUND, "유저를 찾을 수 없습니다."),
    NOT_FOUNT_USER_COUPON(NOT_FOUND, "쿠폰쿠독 id를 찾을 수 없습니다."),

    //F
    FORBIDDEN_ADMIN_ONLY(FORBIDDEN, "ADMIN 권한을 가진 유저만 접근할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
