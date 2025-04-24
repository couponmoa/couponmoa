package com.couponmoa.backend.domain.coupon.dto.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponCreateRequest {

    @NotBlank
    private String name;

    @NotNull
    private int totalQuantity;

    @NotNull
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull
    private BigDecimal discountRate = BigDecimal.ZERO;
    private BigDecimal minOrderAmount;

    @JsonSetter(nulls = Nulls.SKIP)
    private BigDecimal maxDiscountAmount = BigDecimal.valueOf(9_999_999);
    private String description;

    @JsonSetter(nulls = Nulls.SKIP)
    private LocalDateTime startDate = LocalDateTime.now().plusHours(1);

    @JsonSetter(nulls = Nulls.SKIP)
    private LocalDateTime endDate = LocalDateTime.now().plusHours(2);

    @JsonSetter(nulls = Nulls.SKIP)
    private LocalDateTime expiryDate = LocalDateTime.now().plusMonths(1);

    @NotNull
    private Long storeId;

    @Builder
    public CouponCreateRequest(
            String name,
            int totalQuantity,
            BigDecimal discountAmount,
            BigDecimal discountRate,
            BigDecimal minOrderAmount,
            BigDecimal maxDiscountAmount,
            String description,
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime expiryDate,
            Long storeId
    ) {
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        this.discountRate = discountRate != null ? discountRate : BigDecimal.ZERO;
        this.minOrderAmount = minOrderAmount != null ? minOrderAmount : BigDecimal.ZERO;
        this.maxDiscountAmount = maxDiscountAmount != null ? maxDiscountAmount : BigDecimal.valueOf(9_999_999);
        this.description = description;
        this.startDate = startDate != null ? startDate : LocalDateTime.now().plusHours(1);
        this.endDate = endDate != null ? endDate : LocalDateTime.now().plusHours(2);
        this.expiryDate = expiryDate != null ? expiryDate : LocalDateTime.now().plusMonths(1);
        this.storeId = storeId;
    }
}