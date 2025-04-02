package com.couponmoa.backend.domain.user.dto.request;

import lombok.Getter;

@Getter
public class SigninRequest {

    private String email;

    private String password;
}
