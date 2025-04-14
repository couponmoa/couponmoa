package com.couponmoa.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    // subscribe
    DUPLICATED_USER_COUPON(CONFLICT, "이미 구독한 쿠폰입니다."),

    // common
    FORBIDDEN_ADMIN_ONLY(FORBIDDEN, "ADMIN 권한을 가진 유저만 접근할 수 있습니다."),

    // auth
    TOKEN_NOT_FOUND(NOT_FOUND, "존재하지 않는 토큰입니다."),
    INVALID_JWT(UNAUTHORIZED, "유효하지 않는 JWT 서명입니다."),
    EXPIRED_JWT(UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT(BAD_REQUEST, "지원되지 않는 JWT 토큰입니다."),
    UNAUTHORIZED_ACCESS(UNAUTHORIZED,"로그인 되어 있지 않습니다." ),
    REFRESH_TOKEN_FORBIDDEN(FORBIDDEN,"Refresh Token으로 접근할 수 없습니다."),

    // user
    INVALID_USER_ROLE(FORBIDDEN,"유효하지 않은 권한입니다."),
    EMAIL_ALREADY_EXIST(BAD_REQUEST,"이미 존재하는 이메일입니다."),
    EMAIL_ALREADY_DELETED(BAD_REQUEST,"이미 탈퇴한 이메일입니다."),
    USER_NOT_FOUND(NOT_FOUND,"존재하지 않는 계정입니다."),
    INVALID_PASSWORD(BAD_REQUEST,"비밀번호가 일치하지 않습니다"),
    SAME_PASSWORD(BAD_REQUEST,"동일한 비밀번호입니다."),

    // user coupon
    USER_COUPON_NOT_FOUND(NOT_FOUND, "사용자 쿠폰을 찾을 수 없습니다."),
    USER_COUPON_ALREADY_ISSUED(CONFLICT, "이미 발급받은 쿠폰입니다."),
    USER_COUPON_ACCESS_DENIED(FORBIDDEN, "해당 쿠폰에 대한 권한이 없습니다."),
    USER_COUPON_CODE_UNAVAILABLE(BAD_REQUEST, "쿠폰이 이미 사용되었거나 만료되었습니다."),

    // server
    EXCEPTION(INTERNAL_SERVER_ERROR, "알 수 없는 에러입니다."),

    // store
    STORE_NOT_FOUND(NOT_FOUND, "존재하지 않는 스토어 입니다."),
    NOT_VALIDATE_STORE_OWNER(UNAUTHORIZED,"남의 스토어를 건들지 마라" ),

    // coupon
    DISCOUNT_REQUIRED(BAD_REQUEST, "할인 금액과 할인율 중 하나는 설정되어야 합니다." ),
    INVALID_DISCOUNT_SETTING(BAD_REQUEST,"할인 금액과 할인율 중 하나만 설정할 수 있습니다." ),
    DISCOUNT_EXCEEDS_MAX(BAD_REQUEST,"할인 금액은 최대 할인 금액을 초과할 수 없습니다." ),
    INVALID_START_DATE(BAD_REQUEST,"쿠폰 발급 시작일은 현재 시간 이후여야 합니다." ),
    INVALID_EXPIRY_DATE(BAD_REQUEST,"쿠폰 만료일은 발급 종료일 이후여야 합니다." ),
    COUPON_NOT_FOUND(NOT_FOUND,"존재하지 않는 쿠폰입니다." ),
    INVALID_TOTAL_QUANTITY(BAD_REQUEST,"새로운 총 수량은 이미 발급된 쿠폰 수보다 커야합니다." ),
    COUPON_OUT_OF_STOCK(BAD_REQUEST,"쿠폰이 모두 소진되었습니다." ),
    COUPON_NOT_ACTIVE(BAD_REQUEST, "쿠폰 발급 기간이 아닙니다."),
    COUPON_SOLE_OUT(BAD_REQUEST, "쿠폰이 모두 소진되었습니다."),
    INVALID_END_DATE(BAD_REQUEST,"쿠폰 발급 시작일은 종료일보다 이전이어야 합니다." ),

    // notification
    NOTIFICATION_NOT_FOUND(BAD_REQUEST,"존재하지 않는 알림입니다."),
    SQS_SEND_FAILED(INTERNAL_SERVER_ERROR,"알림 전송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
