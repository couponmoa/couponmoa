package com.couponmoa.backend.domain.emailSender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponAlertDto {
    private Long userId;
    private String message;
}
