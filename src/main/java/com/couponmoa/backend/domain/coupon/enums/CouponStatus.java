package com.couponmoa.backend.domain.coupon.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum CouponStatus {
    UPCOMING, IN_PROGRESS, ENDED;

    public static CouponStatus editStatus(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startDate)) {
            return CouponStatus.UPCOMING;
        }
        if (now.isAfter(endDate)) {
            return CouponStatus.ENDED;
        }
        return CouponStatus.IN_PROGRESS;
    }
}

// 생성 시점에는 이미 날짜 검증 로직이 존재하기 때문에,
// UPCOMING 상태만 유효함.
// 이후에는 시간의 흐름에 따라 자동으로 변경 or 업데이트 시 자동으로 변경( 카테고리를 직접 변경할 수는 없음)
// + 조회해서 응답값으로 반환할때도 변경이 되도록 로직 추가. 뭔가 더해야할까 ?
