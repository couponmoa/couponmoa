package com.couponmoa.backend.domain.coupon.entity;

import com.couponmoa.backend.common.entity.BaseEntity;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.store.entity.Store;
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
    private int issuedQuantity;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private String description;

    private LocalDateTime startDate;  // 쿠폰 발급 시작일
    private LocalDateTime endDate;    // 쿠폰 발급 종료일
    private LocalDateTime expiryDate; // 쿠폰 만료일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @Builder
    public Coupon(String name, int totalQuantity, BigDecimal discountAmount, BigDecimal discountRate,
                  BigDecimal minOrderAmount, BigDecimal maxDiscountAmount, String description,
                  LocalDateTime startDate, LocalDateTime endDate, LocalDateTime expiryDate,
                  Store store, CouponStatus status) {

        this.name = name;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = 0;
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expiryDate = expiryDate;
        this.store = store;
        this.status = status;
    }

    public void updateQuantity(int newTotalQuantity) {
        if (newTotalQuantity < this.issuedQuantity) {
            throw new ApplicationException(ErrorCode.INVALID_TOTAL_QUANTITY);
        }

        this.totalQuantity = newTotalQuantity;
    }

    public void update(String name, BigDecimal discountAmount, BigDecimal discountRate,
                       BigDecimal minOrderAmount, BigDecimal maxDiscountAmount, String description,
                       LocalDateTime startDate, LocalDateTime endDate, LocalDateTime expiryDate, Store store) {
        this.name = name;
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expiryDate = expiryDate;
        this.store = store;
    }

    public void updateStatus(CouponStatus newstatus) {
        this.status = newstatus;
    }

    public void delete() {
        this.setDeletedAt(LocalDateTime.now());
    }

    public int getAvailableQuantity() {
        return totalQuantity - issuedQuantity;
    }
}
