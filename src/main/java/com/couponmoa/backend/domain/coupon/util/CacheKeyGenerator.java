package com.couponmoa.backend.domain.coupon.util;

import com.couponmoa.backend.domain.coupon.dto.request.CouponCursor;
import com.couponmoa.backend.domain.coupon.dto.request.CouponSearchByStoreRequestDto;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import lombok.extern.slf4j.Slf4j;

// 캐시 키를 생성하는 역할, 파라미터를 기반으로 고유한 키 생성
@Slf4j
public class CacheKeyGenerator {

    // 검색 조건을 기준으로 고유한 키 생성 ( ex: status-IN_PROGRESS-issuedQuantity-all-keyword-스타벅스-couponId-all-size-10-page-1)
    public static String generateCacheKey(CouponStatus status, CouponCursor cursor, int size, int page) {
        StringBuilder keyBuilder = new StringBuilder();

        keyBuilder.append("status-").append(status.name()).append("-");

        if (cursor != null) {
            keyBuilder.append("issuedQuantity-")
                    .append(cursor.issuedQuantity() != null ? cursor.issuedQuantity() : "all")
                    .append("-");

            keyBuilder.append("keyword-")
                    .append(cursor.keyword() != null ? cursor.keyword() : "all")
                    .append("-");

            keyBuilder.append("couponId-")
                    .append(cursor.couponId() != null ? cursor.couponId() : "all")
                    .append("-");
        }

        keyBuilder.append("size-").append(size).append("-");
        keyBuilder.append("page-").append(page);

        return keyBuilder.toString();
    }

    // Store와 관련된 조건을 기준으로 고유한 키 생성 ( ex: storeId-1-keyword-아메리카노-status-IN_PROGRESS-discountAmount-3000-discountRate-null-startDate-2025-04-14-size-10-page-1
    public static String generateCacheKey(Long storeId, CouponSearchByStoreRequestDto requestDto, int size, int page) {
        StringBuilder keyBuilder = new StringBuilder();

        keyBuilder.append("storeId-").append(storeId).append("-");

        if (requestDto != null) {
            keyBuilder.append("keyword-").append(requestDto.getKeyword() != null ? requestDto.getKeyword() : "all").append("-");
            keyBuilder.append("status-").append(requestDto.getStatus() != null ? requestDto.getStatus().name() : "all").append("-");
            keyBuilder.append("discountAmount-").append(requestDto.getDiscountAmount() != null ? requestDto.getDiscountAmount() : "all").append("-");
            keyBuilder.append("discountRate-").append(requestDto.getDiscountRate() != null ? requestDto.getDiscountRate() : "all").append("-");
            keyBuilder.append("startDate-").append(requestDto.getStartDate() != null ? requestDto.getStartDate() : "all").append("-");
        }

        keyBuilder.append("size-").append(size).append("-");
        keyBuilder.append("page-").append(page);

        return keyBuilder.toString();
    }

    // 쿠폰 ID를 기반으로 고유한 키 생성 ( ex: couponId-123 )
    public static String generateCacheKey(Long couponId) {
        return "couponId-" + couponId;
    }
}