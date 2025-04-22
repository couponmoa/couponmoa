package com.couponmoa.backend.domain.couponstats.repository;

import com.couponmoa.backend.domain.couponstats.entity.CouponDailyUsageStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CouponDailyUsageStatsRepository extends JpaRepository<CouponDailyUsageStats, Long> {
    @Query("SELECT s FROM CouponDailyUsageStats s WHERE s.couponId = :couponId AND s.statDate BETWEEN :start AND :end")
    List<CouponDailyUsageStats> findAllByCouponId(Long couponId, LocalDate start, LocalDate end);
}
