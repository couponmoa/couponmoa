package com.couponmoa.backend.common.util;

import com.couponmoa.backend.domain.coupon.dto.request.CouponCursor;
import com.couponmoa.backend.domain.coupon.dto.request.CouponSearchByStoreRequest;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.store.dto.request.StoreCursor;
import lombok.extern.slf4j.Slf4j;

// 캐시 키를 생성하는 역할, 파라미터를 기반으로 고유한 키 생성
@Slf4j
public class CacheKeyGenerator {

    // 검색 조건을 기준으로 고유한 키 생성 (예: status-IN_PROGRESS-issuedQuantity-all-keyword-스타벅스-couponId-all-size-10)
    public static String generateCacheKey(CouponStatus status, CouponCursor cursor, int size) {
        StringBuilder keyBuilder = new StringBuilder();

        keyBuilder.append("status-").append(status != null ? status.name() : "all").append("-");

        if (cursor == null) {
            keyBuilder.append("issuedQuantity-all-")
                    .append("keyword-all-")
                    .append("couponId-all-");
        } else {
            keyBuilder.append("issuedQuantity-")
                    .append(cursor.issuedQuantity() != null ? cursor.issuedQuantity().toString() : "all")
                    .append("-");

            keyBuilder.append("keyword-")
                    .append(cursor.keyword() != null ? cursor.keyword() : "all")
                    .append("-");

            keyBuilder.append("couponId-")
                    .append(cursor.couponId() != null ? cursor.couponId().toString() : "all")
                    .append("-");
        }

        keyBuilder.append("size-").append(size);

        return keyBuilder.toString();
    }

    // Store와 관련된 조건을 기준으로 고유한 키 생성 ( ex: storeId-1-keyword-아메리카노-status-IN_PROGRESS-discountAmount-3000-discountRate-null-startDate-2025-04-14-size-10-page-1
    public static String generateCacheKey(Long storeId, CouponSearchByStoreRequest requestDto, int size, int page) {
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

    // Store 검색 조건을 기반으로 고유한 캐시 키 생성( ex: keyword-커피-storeId-10-size-20), storeId: 커서 기반 페이징을 위해 사용, 없으면 생략 가능
    public static String generateCacheKey(StoreCursor cursor, int size) {
        StringBuilder keyBuilder = new StringBuilder();

        keyBuilder.append("keyword-")
                .append(cursor != null && cursor.keyword() != null ? cursor.keyword() : "all")
                .append("-");

        if (cursor != null && cursor.storeId() != null) {
            keyBuilder.append("storeId-").append(cursor.storeId()).append("-");
        }

        keyBuilder.append("size-").append(size);
        return keyBuilder.toString();
    }

    // 쿠폰 ID를 기반으로 고유한 키 생성 ( ex: couponId-123 )
    public static String generateCouponCacheKey(Long couponId) {
        return "couponId-" + couponId;
    }

    // 단일 스토어 ID 기준 캐시 키 (ex: storeId-123)
    public static String generateStoreCacheKey(Long storeId) {
        return "storeId-" + storeId;
    }

}