package com.couponmoa.backend.domain.coupon.service;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.dto.request.CouponCreateRequestDto;
import com.couponmoa.backend.domain.coupon.dto.request.CouponUpdateRequestDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponResponseDto;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.couponmoa.backend.domain.coupon.enums.CouponStatus.editStatus;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;
    private final StoreRepository storeRepository;

    public ApiResponse<CouponResponseDto> createCoupon(CouponCreateRequestDto requestDto) {

        // Store의 소유자가 맞는지 검증 & Store가 존재하는지도 검증
        Store store = validateStoreOwnerAndGetStore(requestDto.getStoreId());

        // 할인 로직 검증
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
        validateDates(requestDto.getStartDate(), requestDto.getEndDate(), requestDto.getExpiryDate(),true);

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
                .expiryDate(requestDto.getExpiryDate())
                .store(store)
                .status(CouponStatus.UPCOMING)
                .build();

        Coupon savedCoupon = couponRepository.save(newCoupon);

        return ApiResponse.success(new CouponResponseDto(
                savedCoupon.getId()));
    }

    // 일단 update시에 store는 변경할 수 없다고 가정.
    public ApiResponse<CouponResponseDto> updateCoupon(Long couponId,CouponUpdateRequestDto requestDto) {

        // 아직 존재하는 쿠폰인지 검증
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.COUPON_NOT_FOUND));

        // Store의 소유자가 맞는지 검증 & Store가 존재하는지도 검증
        Store store = validateStoreOwnerAndGetStore(requestDto.getStoreId());

        // update 요청데이터의 dates null 검증, null 일 경우 이전 데이터로.
        ResolvedDates resolvedDates = resolveDates(requestDto, coupon);

        // 날짜 검증
        validateDates(resolvedDates.startDate(), resolvedDates.endDate(),resolvedDates.expiryDate(),false);

        // newTotalQuantity 검증 및 반영, availableQuantity가 0이 되면 status도 ENDED로 업데이트됨
        if (requestDto.getNewTotalQuantity() > 0) {
            coupon.updateQuantity(requestDto.getNewTotalQuantity());
        }

        // 할인율과 할인금액 검증
        boolean isDiscountAmountSet = requestDto.getDiscountAmount() != null && requestDto.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0;
        boolean isDiscountRateSet = requestDto.getDiscountRate() != null && requestDto.getDiscountRate().compareTo(BigDecimal.ZERO) > 0;

        if (isDiscountAmountSet && isDiscountRateSet) {
            throw new ApplicationException(ErrorCode.INVALID_DISCOUNT_SETTING);
        }

        // 날짜 변경 감지, 변경된 부분이 있다면 Status 변경
        if (!resolvedDates.startDate().isEqual(coupon.getStartDate()) ||
                !resolvedDates.endDate().isEqual(coupon.getEndDate())) {

            CouponStatus newStatus = editStatus(resolvedDates.startDate(), resolvedDates.endDate(), coupon.getAvailableQuantity());
            coupon.updateStatus(newStatus);
        }

        // 사실상 put 방식처럼 작동하도록.. 이게맞나 ?
        updateIfPresent(coupon, requestDto);

        return ApiResponse.success(new CouponResponseDto(coupon.getId()));
    }

    public void deleteCoupon(Long couponId) {
        // 존재하는 쿠폰인지 검증
        Coupon coupon = couponRepository.findByIdOrElseThrow(couponId, ErrorCode.COUPON_NOT_FOUND);

        // Store의 소유자가 맞는지 검증 & Store가 존재하는지도 검증
        validateStoreOwnerAndGetStore(coupon.getStore().getId());

        coupon.delete();
    }

    private Store validateStoreOwnerAndGetStore(Long storeId) {
        Store store = storeRepository.findByIdOrElseThrow(storeId, ErrorCode.STORE_NOT_FOUND);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //로그인 된 유저인지 체크
        if (!(principal instanceof AuthUser authUSer)) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (!store.getUser().getEmail().equals(authUSer.getEmail())) {
            throw new ApplicationException(ErrorCode.NOT_VALIDATE_STORE_OWNER);
        }

        return store;
    }

    private void updateIfPresent(Coupon coupon, CouponUpdateRequestDto requestDto) {
        String name = requestDto.getName() != null ? requestDto.getName() : coupon.getName();
        BigDecimal discountAmount = requestDto.getDiscountAmount() != null ? requestDto.getDiscountAmount() : coupon.getDiscountAmount();
        BigDecimal discountRate = requestDto.getDiscountRate() != null ? requestDto.getDiscountRate() : coupon.getDiscountRate();
        BigDecimal minOrderAmount = requestDto.getMinOrderAmount() != null ? requestDto.getMinOrderAmount() : coupon.getMinOrderAmount();
        BigDecimal maxDiscountAmount = requestDto.getMaxDiscountAmount() != null ? requestDto.getMaxDiscountAmount() : coupon.getMaxDiscountAmount();
        String description = requestDto.getDescription() != null ? requestDto.getDescription() : coupon.getDescription();
        LocalDateTime startDate = requestDto.getStartDate() != null ? requestDto.getStartDate() : coupon.getStartDate();
        LocalDateTime endDate = requestDto.getEndDate() != null ? requestDto.getEndDate() : coupon.getEndDate();
        LocalDateTime expiryDate = requestDto.getExpiryDate() != null ? requestDto.getExpiryDate() : coupon.getExpiryDate();

        coupon.update(
                name,
                discountAmount,
                discountRate,
                minOrderAmount,
                maxDiscountAmount,
                description,
                startDate,
                endDate,
                expiryDate,
                coupon.getStore()
        );
    }

    private static void validateDates(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime expiryDate, boolean isCreate) {
        LocalDateTime now = LocalDateTime.now();

        if (startDate.isAfter(endDate)) {
            throw new ApplicationException(ErrorCode.INVALID_END_DATE);
        }

        if (expiryDate.isBefore(endDate)) {
            throw new ApplicationException(ErrorCode.INVALID_EXPIRY_DATE);
        }

        if (isCreate) {
            // 생성일 경우에만 현재 시간 기준 검증
            if (startDate.isBefore(now)) {
                throw new ApplicationException(ErrorCode.INVALID_START_DATE);
            }
        }
    }

    // 쿠폰 수정시에 요청 dto의 값과 기존 날짜 값을 고려해 실제 수정될 날짜 값을 담는 내부 record 클래스.
    private static record ResolvedDates(
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime expiryDate
    ) {}

    private static ResolvedDates resolveDates(CouponUpdateRequestDto requestDto, Coupon coupon) {
        LocalDateTime startDate = requestDto.getStartDate() != null ? requestDto.getStartDate() : coupon.getStartDate();
        LocalDateTime endDate = requestDto.getEndDate() != null ? requestDto.getEndDate() : coupon.getEndDate();
        LocalDateTime expiryDate = requestDto.getExpiryDate() != null ? requestDto.getExpiryDate() : coupon.getExpiryDate();

        return new ResolvedDates(startDate, endDate, expiryDate);
    }
}
