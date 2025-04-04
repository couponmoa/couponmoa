package com.couponmoa.backend.domain.coupon.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponCreateRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private int totalQuantity;

    @NotNull
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull
    private BigDecimal discountRate = BigDecimal.ZERO;
    private BigDecimal minOrderAmount;

    @JsonSetter(nulls = Nulls.SKIP)   // @NotNull 방어코드 작성 튜터님께 여쭤볼 것.
    private BigDecimal maxDiscountAmount = BigDecimal.valueOf(9_999_999);
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSetter(nulls = Nulls.SKIP)
    private LocalDateTime startDate = LocalDateTime.now().plusHours(1);

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSetter(nulls = Nulls.SKIP)
    private LocalDateTime endDate = LocalDateTime.now().plusHours(2);

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSetter(nulls = Nulls.SKIP)
    private LocalDateTime expiryDate = LocalDateTime.now().plusMonths(1);

    @NotNull
    private Long storeId;
}