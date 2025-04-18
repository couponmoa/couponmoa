package com.couponmoa.backend.domain.coupon.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.dto.request.CouponSearchByStoreRequest;
import com.couponmoa.backend.domain.coupon.dto.response.CouponDetailResponse;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponse;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponQueryDslRepository;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.coupon.service.v2.CouponReadServiceV2;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CouponReadServiceV2Test {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponQueryDslRepository couponQueryDslRepository;

    @InjectMocks
    private CouponReadServiceV2 couponReadServiceV2;

    private Store dummyStore;
    private Coupon dummyCoupon;

    @BeforeEach
    void setUp() {
        // Store 객체 초기화
        dummyStore = Store.builder()
                .name("테스트 스토어")
                .description("테스트 설명")
                .address("서울시 강남구")
                .user(mock(User.class))
                .build();

        // Coupon 객체 초기화
        dummyCoupon = Coupon.builder()
                .name("상세 쿠폰")
                .totalQuantity(100)
                .discountAmount(BigDecimal.valueOf(1000))
                .endDate(LocalDateTime.of(2025, 5, 1, 0, 0))
                .store(dummyStore)
                .build();
    }

    @Test
    void 키워드로_쿠폰_조회_성공() {
        // Given
        List<CouponSimpleResponse> coupons = Collections.singletonList(
                CouponSimpleResponse.builder().id(1L).name("테스트").discountAmount(BigDecimal.valueOf(3000)).build()
        );
        when(couponQueryDslRepository.searchCouponsByKeyword(any(), any(), anyInt())).thenReturn(coupons);

        // When
        List<CouponSimpleResponse> result = couponReadServiceV2.findCouponsByKeyword(null, null, 10);

        // Then
        assertFalse(result.isEmpty());
        assertEquals("테스트", result.get(0).getName());
        verify(couponQueryDslRepository, times(1)).searchCouponsByKeyword(any(), any(), anyInt());
    }

    @Test
    void 키워드로_쿠폰_조회_결과_없음() {
        // Given
        when(couponQueryDslRepository.searchCouponsByKeyword(any(), any(), anyInt())).thenReturn(Collections.emptyList());

        // When
        List<CouponSimpleResponse> result = couponReadServiceV2.findCouponsByKeyword(null, null, 10);

        // Then
        assertTrue(result.isEmpty());
        verify(couponQueryDslRepository, times(1)).searchCouponsByKeyword(any(), any(), anyInt());
    }

    @Test
    void 스토어별_쿠폰_조회_성공() {
        // Given
        Page<CouponSimpleResponse> coupons = new PageImpl<>(Collections.singletonList(
                CouponSimpleResponse.builder()
                        .id(1L)
                        .name("스토어별 조회 테스트 쿠폰")
                        .discountAmount(BigDecimal.valueOf(3000))
                        .discountRate(BigDecimal.ZERO)
                        .build()));

        when(couponQueryDslRepository.searchCouponsByStore(
                eq(1L),
                eq("테스트"),
                nullable(CouponStatus.class),
                nullable(BigDecimal.class),
                nullable(BigDecimal.class),
                nullable(LocalDateTime.class),
                any(Pageable.class)
        )).thenReturn(coupons);

        // When
        Page<CouponSimpleResponse> result = couponReadServiceV2.findCouponsByStore(1L,
                new CouponSearchByStoreRequest("테스트",null,null,null,null), 10, 1);

        // Then
        assertFalse(result.isEmpty());
        assertEquals("스토어별 조회 테스트 쿠폰", result.getContent().get(0).getName());

        verify(couponQueryDslRepository, times(1)).searchCouponsByStore(
                eq(1L),
                eq("테스트"),
                nullable(CouponStatus.class),
                nullable(BigDecimal.class),
                nullable(BigDecimal.class),
                nullable(LocalDateTime.class),
                any(Pageable.class)
        );
    }

    @Test
    void 쿠폰_상세_조회_성공() {
        // Given
        when(couponRepository.findById(anyLong())).thenReturn(Optional.of(dummyCoupon));

        // When
        CouponDetailResponse result = couponReadServiceV2.findCoupon(1L, null);

        // Then
        assertNotNull(result);
        assertEquals("상세 쿠폰", result.getName());
        verify(couponRepository, times(1)).findById(anyLong());
    }

    @Test
    void 쿠폰_상세_조회_실패_존재하지_않음() {
        // Given
        when(couponRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ApplicationException.class, () -> couponReadServiceV2.findCoupon(1L, null));
        verify(couponRepository, times(1)).findById(anyLong());
    }

    @Test
    void fallback_키워드로_쿠폰_조회_성공() {
        // Given
        when(couponRepository.findAll()).thenReturn(List.of(dummyCoupon));

        // When
        List<CouponSimpleResponse> result = couponReadServiceV2.fallbackFindCouponsByKeyword(
                null, null, 10, new ApplicationException(ErrorCode.REDIS_FAILURE));

        // Then
        assertFalse(result.isEmpty());
        assertEquals("상세 쿠폰", result.get(0).getName());
        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void fallback_스토어별_쿠폰_조회_성공() {
        // Given
        Page<Coupon> couponPage = new PageImpl<>(List.of(dummyCoupon));
        when(couponRepository.findByStoreId(anyLong(), any(Pageable.class))).thenReturn(couponPage);

        // When
        Page<CouponSimpleResponse> result = couponReadServiceV2.fallbackFindCouponsByStore(
                1L, new CouponSearchByStoreRequest(), 10, 1, new ApplicationException(ErrorCode.REDIS_FAILURE));

        // Then
        assertFalse(result.isEmpty());
        assertEquals("상세 쿠폰", result.getContent().get(0).getName());
        verify(couponRepository, times(1)).findByStoreId(anyLong(), any(Pageable.class));
    }

    @Test
    void fallback_쿠폰_상세_조회_성공() {
        // Given
        when(couponRepository.findById(anyLong())).thenReturn(Optional.of(dummyCoupon));

        // When
        CouponDetailResponse result = couponReadServiceV2.fallbackFindCoupon(
                1L, null, new ApplicationException(ErrorCode.REDIS_FAILURE));

        // Then
        assertNotNull(result);
        assertEquals("상세 쿠폰", result.getName());
        verify(couponRepository, times(1)).findById(anyLong());
    }
}
