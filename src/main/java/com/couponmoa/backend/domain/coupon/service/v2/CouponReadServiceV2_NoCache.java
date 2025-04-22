package com.couponmoa.backend.domain.coupon.service.v2;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.dto.request.CouponCursor;
import com.couponmoa.backend.domain.coupon.dto.request.CouponSearchByStoreRequest;
import com.couponmoa.backend.domain.coupon.dto.response.CouponDetailResponse;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponse;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponQueryDslRepository;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponReadServiceV2_NoCache {

    private final CouponQueryDslRepository couponQueryDslRepository;
    private final CouponRepository couponRepository;

    @Timed(value = "coupon.find_by_keyword.time", description = "키워드로 쿠폰 조회에 걸린 시간", histogram = true)
    @Counted(value = "coupon.find_by_keyword.count", description = "키워드로 쿠폰 조회 횟수")
    public List<CouponSimpleResponse> findCouponsByKeyword(CouponStatus status, CouponCursor cursor, int size) {
        log.info("findCouponsByKeyword 호출");
        return searchWithSafeCursor(status, cursor == null ? new CouponCursor(null, null, null) : cursor, size);
    }

    @Timed(value = "coupon.find_by_store.time", description = "스토어별 쿠폰 조회에 걸린 시간", histogram = true)
    @Counted(value = "coupon.find_by_store.count", description = "스토어별 쿠폰 조회 횟수")
    public Page<CouponSimpleResponse> findCouponsByStore(
            Long storeId,
            CouponSearchByStoreRequest requestDto,
            int size, int page
    ) {
        log.info("findCouponsByStore 호출");
        Pageable pageable = PageRequest.of(page - 1, size);

        return couponQueryDslRepository.searchCouponsByStore(
                storeId,
                requestDto.getKeyword(),
                requestDto.getStatus(),
                requestDto.getDiscountAmount(),
                requestDto.getDiscountRate(),
                requestDto.getStartDate(),
                pageable
        );
    }

    @Timed(value = "coupon.find.time", description = "쿠폰 상세 조회에 걸린 시간", histogram = true)
    @Counted(value = "coupon.find.count", description = "쿠폰 상세 조회 횟수")
    public CouponDetailResponse findCoupon(Long couponId, AuthUser authUser) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COUPON_NOT_FOUND));
        return CouponDetailResponse.toDto(coupon);
    }

    public List<CouponSimpleResponse> searchWithSafeCursor(
            CouponStatus status,
            CouponCursor cursor,
            int size) {
        return couponQueryDslRepository.searchCouponsByKeyword(status, cursor, size);
    }
}
