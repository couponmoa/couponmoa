package com.couponmoa.backend.domain.coupon.service;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.domain.coupon.dto.request.CouponSaveRequestDto;
import com.couponmoa.backend.domain.coupon.dto.request.CouponUpdateRequestDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponResponseDto;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.couponmoa.backend.common.exception.ErrorCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;
    private final StoreRepository storeRepository;

    public ApiResponse<CouponResponseDto> createCoupon(CouponSaveRequestDto requestDto) {

        // store 존재 검증
        Store store = storeRepository.findByIdOrElseThrow(requestDto.getStoreId(), ErrorCode.STORE_NOT_FOUND);

        // 할인 검증
        // discountAmount와 discountRate 중 하나는 반드시 0이어야 함 (변액 할인과 정액 할인을 동시에 제공하는 쿠폰은 없다.)
        boolean isDiscountAmountDefault = requestDto.getDiscountAmount().compareTo(BigDecimal.ZERO) == 0;
        boolean isDiscountRateDefault = requestDto.getDiscountRate().compareTo(BigDecimal.ZERO) == 0;

        // 정액 할인이나 변액 할인 둘중 하나는 설정 해야함.
        if (isDiscountAmountDefault && isDiscountRateDefault) {
            throw new ApplicationException(ErrorCode.DISCOUNT_REQUIRED);
        }

        // 정액 할인과 변액 할인이 동시에 적용될 수 없음.
        if (!isDiscountAmountDefault && !isDiscountRateDefault) {
            throw new ApplicationException(ErrorCode.INVALID_DISCOUNT_SETTING);
        }

        // 최대 할인 금액은 정액 할인 금액보다 커야함.
        if (!isDiscountAmountDefault && requestDto.getMaxDiscountAmount() != null) {
            if (requestDto.getDiscountAmount().compareTo(requestDto.getMaxDiscountAmount()) > 0) {
                throw new ApplicationException(ErrorCode.DISCOUNT_EXCEEDS_MAX);
            }
        }

        // 날짜 검증
        LocalDateTime now = LocalDateTime.now();
        if (requestDto.getStartDate().isBefore(now)) {
            throw new ApplicationException(ErrorCode.INVALID_START_DATE);
        }
        if (!requestDto.getStartDate().isBefore(requestDto.getEndDate())) {
            throw new ApplicationException(ErrorCode.INVALID_DATE_ORDER);
        }
        if (!requestDto.getEndDate().isBefore(requestDto.getExpiryDate())) {
            throw new ApplicationException(ErrorCode.INVALID_EXPIRY_DATE);
        }

        Coupon newCoupon = Coupon.builder()
                .name(requestDto.getName())
                .totalQuantity(requestDto.getTotalQuantity())
                .discountAmount(requestDto.getDiscountAmount())
                .discountRate(requestDto.getDiscountRate())
                .minOrderAmount(requestDto.getMinOrderAmount())
                .maxDiscountAmount(requestDto.getMaxDiscountAmount())
                .description(requestDto.getDescription())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .expiryDate(requestDto.getExpiryDate()) //
                .category(requestDto.getCategory()) //
                .store(store)
                .build();

        Coupon savedCoupon = couponRepository.save(newCoupon);

        return ApiResponse.success(new CouponResponseDto(
                savedCoupon.getId()));
    }

    public ApiResponse<CouponResponseDto> updateCoupon(Long couponId,CouponUpdateRequestDto requestDto) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COUPON_NOT_FOUND));

        // 쿠폰 시작일 이후에도 수정 가능, 만료일만 검증
        if (requestDto.getEndDate() != null && requestDto.getExpiryDate() != null) {
            if (!requestDto.getEndDate().isBefore(requestDto.getExpiryDate())) {
                throw new ApplicationException(ErrorCode.INVALID_EXPIRY_DATE);
            }
        }

        // newTotalQuantity 검증 및 반영
        if (requestDto.getNewTotalQuantity() > 0) {
            coupon.updateQuantity(requestDto.getNewTotalQuantity());
        }

        // 할인율과 할인 금액 검증
        boolean isDiscountAmountSet = requestDto.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0;
        boolean isDiscountRateSet = requestDto.getDiscountRate().compareTo(BigDecimal.ZERO) > 0;

        if (isDiscountAmountSet && isDiscountRateSet) {
            throw new ApplicationException(ErrorCode.INVALID_DISCOUNT_SETTING);
        }

        coupon.update(
                requestDto.getName(),
                requestDto.getDiscountAmount(),
                requestDto.getDiscountRate(),
                requestDto.getMinOrderAmount(),
                requestDto.getMaxDiscountAmount(),
                requestDto.getDescription(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                requestDto.getExpiryDate(),
                requestDto.getCategory(),
                coupon.getStore()
        );

        return ApiResponse.success(new CouponResponseDto(coupon.getId()));
    }

    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findByIdOrElseThrow(couponId, null);
        coupon.delete();
    }
}
