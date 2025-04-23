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
import com.couponmoa.backend.domain.user.entity.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class CouponStatsServiceTest {

    @Mock
    private CouponRepository couponRepository;
    @Mock
    private CouponDailyUsageStatsRepository usageStatsRepository;
    @InjectMocks
    private CouponStatsService couponStatsService;

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindCouponDailyUsageStatsTests {

        private final long userId = 1L;
        private final long couponId = 1L;
        private final CouponUsageSearchRequest request = new CouponUsageSearchRequest(
                LocalDate.now().minusDays(7),
                LocalDate.now().minusDays(1)
        );

        @Test
        @Order(1)
        void 쿠폰_사용량_조회_쿠폰_없음_실패() {
            ErrorCode errorCode = ErrorCode.COUPON_NOT_FOUND;
            given(couponRepository.findActiveByIdWithStore(anyLong()))
                    .willReturn(Optional.empty());

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> couponStatsService.findCouponDailyUsageStats(userId, couponId, request));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(2)
        void 쿠폰_사용량_조회_쿠폰_주인_아님_실패() {
            ErrorCode errorCode = ErrorCode.COUPON_ACCESS_DENIED;
            User user = mock();
            given(user.getId()).willReturn(2L);
            Store store = mock();
            given(store.getUser()).willReturn(user);
            Coupon coupon = mock();
            given(coupon.getStore()).willReturn(store);
            given(couponRepository.findActiveByIdWithStore(anyLong()))
                    .willReturn(Optional.of(coupon));

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> couponStatsService.findCouponDailyUsageStats(userId, couponId, request));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(3)
        void 쿠폰_사용량_조회_성공() {
            User user = mock();
            given(user.getId()).willReturn(userId);
            Store store = mock();
            given(store.getUser()).willReturn(user);
            Coupon coupon = mock();
            given(coupon.getStore()).willReturn(store);
            given(couponRepository.findActiveByIdWithStore(anyLong()))
                    .willReturn(Optional.of(coupon));

            Long usageCount = 3L;
            CouponDailyUsageStats usageStats = mock();
            given(usageStats.getStatDate()).willReturn(LocalDate.now().minusDays(7));
            given(usageStats.getUsageCount()).willReturn(usageCount);
            List<CouponDailyUsageStats> usageStatsList = List.of(usageStats);
            given(usageStatsRepository.findAllByCouponId(anyLong(), any(), any())).willReturn(usageStatsList);

            List<CouponUsageResponse> result = couponStatsService.findCouponDailyUsageStats(userId, couponId, request);

            assertNotNull(result);
            assertThat(result).hasSize((int) ChronoUnit.DAYS.between(request.getStart(), request.getEnd()) + 1);
            assertThat(result).extracting("usageCount").containsExactly(usageCount, 0L, 0L, 0L, 0L, 0L, 0L);
            assertThat(result.get(0).getDate()).isEqualTo(request.getStart());
        }
    }

}