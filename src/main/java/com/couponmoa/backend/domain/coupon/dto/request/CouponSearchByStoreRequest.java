package com.couponmoa.backend.domain.coupon.dto.request;

import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponSearchByStoreRequest {

    private String keyword;
    private CouponStatus status;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private LocalDateTime startDate;

    @Builder
    public CouponSearchByStoreRequest(String keyword, CouponStatus status, BigDecimal discountAmount, BigDecimal discountRate, LocalDateTime startDate) {
        this.keyword = keyword;
        this.status = status;
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
        this.startDate = startDate;
    }
}
