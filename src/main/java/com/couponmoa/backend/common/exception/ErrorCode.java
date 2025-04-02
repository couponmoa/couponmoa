package com.couponmoa.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    //F
    FORBIDDEN_ADMIN_ONLY(FORBIDDEN, "ADMIN 권한을 가진 유저만 접근할 수 있습니다."),

    // Coupon
    COUPON_NOT_FOUND(NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    COUPON_NOT_ACTIVE(BAD_REQUEST, "쿠폰 발급 기간이 아닙니다."),
    COUPON_SOLE_OUT(BAD_REQUEST, "쿠폰이 모두 소진되었습니다."),

    // UserCoupon
    USER_COUPON_NOT_FOUND(NOT_FOUND, "사용자 쿠폰을 찾을 수 없습니다."),
    USER_COUPON_ALREADY_ISSUED(CONFLICT, "이미 발급받은 쿠폰입니다."),
    USER_COUPON_ACCESS_DENIED(FORBIDDEN, "해당 쿠폰을 조회할 권한이 없습니다."),
    USER_COUPON_CODE_UNAVAILABLE(BAD_REQUEST, "쿠폰이 이미 사용되었거나 만료되었습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
