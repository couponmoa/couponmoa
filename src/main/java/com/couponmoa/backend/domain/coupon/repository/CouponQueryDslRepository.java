package com.couponmoa.backend.domain.coupon.repository;

import com.couponmoa.backend.domain.coupon.dto.request.CouponCursor;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponseDto;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CouponQueryDslRepository {

    Page<CouponSimpleResponseDto> searchCouponsByStore(Long storeId, String keyword, CouponStatus status,
                                                BigDecimal discountAmount, BigDecimal discountRate,
                                                LocalDateTime startDate, Pageable pageable);

    List<CouponSimpleResponseDto> searchCouponsByKeyword(CouponStatus status, CouponCursor cursor, int size);
}
