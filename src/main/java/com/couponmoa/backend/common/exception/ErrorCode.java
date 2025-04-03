package com.couponmoa.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    // common
    FORBIDDEN_ADMIN_ONLY(FORBIDDEN, "ADMIN 권한을 가진 유저만 접근할 수 있습니다."),

    // auth
    TOKEN_NOT_FOUND(NOT_FOUND, "존재하지 않는 토큰입니다."),
    INVALID_JWT(UNAUTHORIZED, "유효하지 않는 JWT 서명입니다."),
    EXPIRED_JWT(UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT(BAD_REQUEST, "지원되지 않는 JWT 토큰입니다."),

    // user
    INVALID_USER_ROLE(FORBIDDEN,"유효하지 않은 권한입니다."),
    EMAIL_ALREADY_EXIST(BAD_REQUEST,"이미 존재하는 이메일입니다."),
    EMAIL_ALREADY_DELETED(BAD_REQUEST,"이미 탈퇴한 이메일입니다."),
    USER_NOT_FOUND(NOT_FOUND,"존재하지 않는 계정입니다."),
    INVALID_PASSWORD(BAD_REQUEST,"비밀번호가 일치하지 않습니다"),
    SAME_PASSWORD(BAD_REQUEST,"동일한 비밀번호입니다."),

    // server
    EXCEPTION(INTERNAL_SERVER_ERROR, "알 수 없는 에러입니다.");


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
