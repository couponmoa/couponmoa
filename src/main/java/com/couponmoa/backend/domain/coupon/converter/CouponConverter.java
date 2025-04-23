package com.couponmoa.backend.domain.coupon.converter;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.entity.Search;

public class CouponConverter {

    // Coupon 객체를 Elasticsearch 문서 형식인 Search 객체로 변환
    public static Search toSearchDocument(Coupon coupon) {
        return Search.builder()
                .couponId(coupon.getId())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .discountAmount(coupon.getDiscountAmount())
                .discountRate(coupon.getDiscountRate())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .expiryDate(coupon.getExpiryDate().toString())  // 날짜를 String으로 변환
                .storeId(coupon.getStore().getId())
                .storeName(coupon.getStore().getName())
                .build();
    }
}
