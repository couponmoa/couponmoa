package com.couponmoa.backend.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private String nickname;

}
