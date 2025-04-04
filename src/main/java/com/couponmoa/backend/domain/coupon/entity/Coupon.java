package com.couponmoa.backend.domain.coupon.entity;

import com.couponmoa.backend.common.entity.BaseEntity;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
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
    private int totalQuantity;  // 반드시 availableQuantity와 함께 수정되어야 함.
    private int availableQuantity; // 서버에서 자동으로 설정.
    private int issuedQuantity;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private String description;

    private LocalDateTime startDate;  // 쿠폰 발급 시작일
    private LocalDateTime endDate;    // 쿠폰 발급 종료일
    private LocalDateTime expiryDate; // 쿠폰 만료일
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id", nullable = false)
    private Store store;

    @Builder
    public Coupon(String name, int totalQuantity, BigDecimal discountAmount, BigDecimal discountRate,
                  BigDecimal minOrderAmount, BigDecimal maxDiscountAmount, String description,
                  LocalDateTime startDate, LocalDateTime endDate, LocalDateTime expiryDate, Store store) {

        this.name = name;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity; // totalQuantity와 같은 값으로 자동 초기화
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
    }

    public void updateQuantity(int newTotalQuantity) {
        int issuedCouponQuantity = this.totalQuantity - this.availableQuantity;

        //이 부분 트래픽증가시 동시성이슈로 인해 예외 발생 가능성 매우 높음.
        if (newTotalQuantity < issuedCouponQuantity) {
            throw new ApplicationException(ErrorCode.INVALID_TOTAL_QUANTITY);
        }

        int newAvailableQuantity = Math.max(0, newTotalQuantity - issuedCouponQuantity);

        this.totalQuantity = newTotalQuantity;
        this.availableQuantity = newAvailableQuantity;
        this.issuedQuantity = issuedCouponQuantity;
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

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void availableQuantityDown() {
        if (availableQuantity <= 0) {
            throw new IllegalStateException("쿠폰 잔여 개수는 음수일 수 없습니다.");
        }
        availableQuantity--;
        issuedQuantity++;
    }
}
