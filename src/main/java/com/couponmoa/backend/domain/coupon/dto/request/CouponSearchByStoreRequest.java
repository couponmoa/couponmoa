package com.couponmoa.backend.domain.coupon.dto.request;

import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class CouponSearchByStoreRequest {

    private String keyword;
    private CouponStatus status;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private LocalDateTime startDate;
}
