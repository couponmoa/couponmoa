package com.couponmoa.backend.domain.coupon.entity;

import com.couponmoa.backend.common.entity.BaseEntity;
import com.couponmoa.backend.domain.coupon.enums.CouponCategory;
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
    @Column(name = "coupon_id")
    private Long id;

    private String name;
    private int totalQuantity;  // 반드시 availableQuantity와 함께 수정되어야 함.
    private int availableQuantity; // 서버에서 자동으로 설정.
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private String description;

    private LocalDateTime startDate;  // 쿠폰 발급 시작일
    private LocalDateTime endDate;    // 쿠폰 발급 종료일
    private LocalDateTime expiryDate; // 쿠폰 만료일
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private CouponCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id",nullable = false)
    private Store store;

    @Builder
    public Coupon(String name, int totalQuantity, BigDecimal discountAmount, BigDecimal discountRate,
                  BigDecimal minOrderAmount, BigDecimal maxDiscountAmount, String description,
                  LocalDateTime startDate, LocalDateTime endDate, LocalDateTime expiryDate, CouponCategory category,
                  Store store) {
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity; // totalQuantity와 같은 값으로 자동 초기화
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expiryDate = expiryDate;
        this.category = category;
        this.store = store;
    }

    public void useCoupon() {
        if (this.availableQuantity <= 0) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }
        this.availableQuantity--; // 자동으로 설정
    }

    public void updateQuantity(int newTotalQuantity) {
        int usedCouponQuantity = this.totalQuantity - this.availableQuantity;

        if (newTotalQuantity < usedCouponQuantity) {
            throw new IllegalArgumentException("새로운 총 수량은 이미 발급된 쿠폰 수보다 커야합니다.");
        }

        this.totalQuantity = newTotalQuantity;
        this.availableQuantity = newTotalQuantity - usedCouponQuantity; // 자동으로 설정
    }

    public void update(String name, BigDecimal discountAmount, BigDecimal discountRate,
                       BigDecimal minOrderAmount, BigDecimal maxDiscountAmount, String description,
                       LocalDateTime startDate, LocalDateTime endDate, LocalDateTime expiryDate, CouponCategory category,
                       Store store) {
        this.name = name;
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expiryDate = expiryDate;
        this.category = category;
        this.store = store;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
