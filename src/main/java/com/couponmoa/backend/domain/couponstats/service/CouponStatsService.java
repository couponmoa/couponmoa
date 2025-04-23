package com.couponmoa.backend.domain.couponstats.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.couponstats.dto.request.CouponUsageSearchRequest;
import com.couponmoa.backend.domain.couponstats.dto.response.CouponUsageResponse;
import com.couponmoa.backend.domain.couponstats.entity.CouponDailyUsageStats;
import com.couponmoa.backend.domain.couponstats.repository.CouponDailyUsageStatsRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CouponStatsService {

    private final CouponRepository couponRepository;
    private final CouponDailyUsageStatsRepository couponUsageStatsRepository;

    public List<CouponUsageResponse> findCouponDailyUsageStats(Long userId, Long couponId, CouponUsageSearchRequest request) {
        Coupon coupon = couponRepository.findActiveByIdWithStore(couponId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COUPON_NOT_FOUND));

        validateCouponStoreOwner(coupon.getStore(), userId);

        List<CouponDailyUsageStats> usageStats = couponUsageStatsRepository.findAllByCouponId(couponId, request.getStart(), request.getEnd());
        return convertToDto(usageStats, request.getStart(), request.getEnd());
    }

    private void validateCouponStoreOwner(Store store, Long userId) {
        if (!store.getUser().getId().equals(userId)) {
            throw new ApplicationException(ErrorCode.COUPON_ACCESS_DENIED);
        }
    }

    private List<CouponUsageResponse> convertToDto(List<CouponDailyUsageStats> usageStats, LocalDate start, LocalDate end) {
        Map<LocalDate, Long> usageMap = usageStats.stream()
                .collect(Collectors.toMap(CouponDailyUsageStats::getStatDate, CouponDailyUsageStats::getUsageCount));

        return Stream.iterate(start, date -> !date.isAfter(end), date -> date.plusDays(1))
                .map(date -> new CouponUsageResponse(date, usageMap.getOrDefault(date, 0L)))
                .toList();
    }
}
