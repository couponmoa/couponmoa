package com.couponmoa.backend.domain.couponstats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CouponUsageResponse {
    private final LocalDate date;
    private final Long usageCount;
}
