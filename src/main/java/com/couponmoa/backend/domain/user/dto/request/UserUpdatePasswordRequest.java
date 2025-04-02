package com.couponmoa.backend.domain.user.dto.request;

import com.couponmoa.backend.common.Const;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserUpdatePasswordRequest {

    @NotBlank
    private String oldPassword;

    @Pattern(
            regexp = Const.PASSWORD_PATTERN,
            message = "비밀번호 형식이 올바르지 않습니다."
    )
    @NotBlank
    private String newPassword;
}
