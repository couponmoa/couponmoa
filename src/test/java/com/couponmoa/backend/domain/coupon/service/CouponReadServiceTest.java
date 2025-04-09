package com.couponmoa.backend.domain.coupon.service;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.domain.coupon.dto.response.CouponDetailResponseDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponSimpleResponseDto;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CouponReadServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponReadService couponReadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 쿠폰_목록_조회_0건_정상작동() {
        // given
        List<Coupon> couponList = List.of(mock(Coupon.class));
        Page<Coupon> page = new PageImpl<>(couponList);

        when(couponRepository.findAllSortedByIQ(any(Pageable.class)))
                .thenReturn(page);

        // when
        ApiResponse<Page<CouponSimpleResponseDto>> result = couponReadService.findAllCoupons(1, 10);

        // then
        assertThat(result.getData().getTotalElements()).isEqualTo(1);
        verify(couponRepository).findAllSortedByIQ(any(Pageable.class));
    }

    @Test
    void 쿠폰_목록_조회_없을시_예외처리() {
        // given
        Page<Coupon> page = new PageImpl<>(List.of());
        when(couponRepository.findAllSortedByIQ(any(Pageable.class))).thenReturn(page);

        // when
        ApiResponse<Page<CouponSimpleResponseDto>> result = couponReadService.findAllCoupons(1, 10);

        // then
        assertThat(result.getData().getContent()).isEmpty();
        assertThat(result.getData().getTotalElements()).isEqualTo(0);
    }

    @Test
    void 쿠폰_상세_조회_성공() {
        // given
        Coupon coupon = new Coupon();
        ReflectionTestUtils.setField(coupon, "id", 1L);
        ReflectionTestUtils.setField(coupon, "name", "테스트용_쿠폰");

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        // when
        ApiResponse<CouponDetailResponseDto> result = couponReadService.findCoupon(1L, mock(AuthUser.class));

        // then
        assertThat(result.getData().getId()).isEqualTo(1L);
        assertThat(result.getData().getName()).isEqualTo("테스트용_쿠폰");

        verify(couponRepository).findById(1L);
    }

    @Test
    void 쿠폰_상세_조회_실패_존재하지않음() {
        // given
        Long couponId = 1L;
        when(couponRepository.findById(couponId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(ApplicationException.class, () ->
                couponReadService.findCoupon(couponId, mock(AuthUser.class))
        );
        verify(couponRepository).findById(couponId);
    }
}