package com.couponmoa.backend.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserDeleteRequest {
    @NotBlank
    private String password;
}
