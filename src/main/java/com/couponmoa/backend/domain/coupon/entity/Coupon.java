package com.couponmoa.backend.domain.coupon.entity;

import com.couponmoa.backend.common.entity.BaseEntity;
import com.couponmoa.backend.domain.coupon.enums.CouponCategory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "coupons")
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int totalQuantity;
    private int availableQuantity;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime expiryDate;
    private CouponCategory counponCategory;

    @Builder
    public Coupon(String name, int totalQuantity, int availableQuantity,BigDecimal discountAmount, BigDecimal discountRate,
                  String description,LocalDateTime expiryDate,CouponCategory counponCategory) {
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
        this.description = description;
        this.expiryDate = expiryDate;
        this.counponCategory = counponCategory;
    }

    public void update(String name, int totalQuantity, int availableQuantity,BigDecimal discountAmount, BigDecimal discountRate,
                       String description,LocalDateTime expiryDate,CouponCategory counponCategory) {
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
        this.description = description;
        this.expiryDate = expiryDate;
        this.counponCategory = counponCategory;
    }

    public void availableQuantityDown() {
        if (availableQuantity <= 0) {
            throw new IllegalStateException("쿠폰 잔여 개수는 음수일 수 없습니다.");
        }
        availableQuantity--;
    }
}
