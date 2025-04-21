package com.couponmoa.backend.domain.couponstats.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "coupon_daily_usage_stats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponDailyUsageStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long couponId;

    @Column(nullable = false)
    private LocalDate statDate;

    @Column(nullable = false)
    private Long usageCount;
}
