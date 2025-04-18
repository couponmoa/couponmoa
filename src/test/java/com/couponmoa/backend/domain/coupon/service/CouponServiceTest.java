package com.couponmoa.backend.domain.coupon.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.dto.request.CouponCreateRequest;
import com.couponmoa.backend.domain.coupon.dto.request.CouponUpdateRequest;
import com.couponmoa.backend.domain.coupon.dto.response.CouponResponse;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.coupon.service.v1.CouponService;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.subscribe.usercoupon.service.UserCouponSubscribeService;
import com.couponmoa.backend.domain.subscribe.userstore.service.UserStoreSubscribeService;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock private CouponRepository couponRepository;

    @Mock private StoreRepository storeRepository;

    @Mock private UserCouponSubscribeService userCouponSubServ;

    @Mock private UserStoreSubscribeService userStoreSubServ;

    @InjectMocks private CouponService couponService;

    private Store store;

    @BeforeEach
    void setUp() {
        User user = new User("admin@example.com", "password", "nickname", UserRole.ROLE_USER);
        store = new Store(user, "가게명", "설명", "주소");
        ReflectionTestUtils.setField(store, "id", 1L);

        AuthUser authUser = new AuthUser(user.getId(), user.getEmail(), user.getUserRole());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authUser, "", authUser.getAuthorities()));
    }

    @Test
    void 쿠폰_생성_성공() throws Exception {
        // Given
        CouponCreateRequest request = CouponCreateRequest.builder()
                .name("테스트용_할인_쿠폰")
                .totalQuantity(100)
                .discountAmount(BigDecimal.valueOf(1000))
                .discountRate(BigDecimal.ZERO)
                .minOrderAmount(BigDecimal.ZERO)
                .maxDiscountAmount(BigDecimal.valueOf(10000))
                .description("테스트 쿠폰입니다.")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .expiryDate(LocalDateTime.now().plusDays(10))
                .storeId(1L)
                .build();

        when(storeRepository.findByIdOrElseThrow(1L, ErrorCode.STORE_NOT_FOUND)).thenReturn(store);
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> {
            Coupon savedCoupon = invocation.getArgument(0);
            Field idField = Coupon.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(savedCoupon, 1L);
            return savedCoupon;
        });

        // When
        CouponResponse response = couponService.createCoupon(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void 쿠폰_생성_실패_할인로직_검증_오류() {
        // Given
        CouponCreateRequest request = CouponCreateRequest.builder()
                .name("잘못된 쿠폰")
                .totalQuantity(100)
                .discountAmount(BigDecimal.valueOf(1000))
                .discountRate(BigDecimal.valueOf(10))
                .minOrderAmount(BigDecimal.ZERO)
                .maxDiscountAmount(BigDecimal.valueOf(2000))
                .description("할인로직 오류")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .expiryDate(LocalDateTime.now().plusDays(10))
                .storeId(1L)
                .build();

        when(storeRepository.findByIdOrElseThrow(1L, ErrorCode.STORE_NOT_FOUND)).thenReturn(store);

        // When & Then
        assertThrows(ApplicationException.class, () -> couponService.createCoupon(request));
    }

    @Test
    void 쿠폰_생성_실패_스토어_없음() {
        // Given
        CouponCreateRequest request = CouponCreateRequest.builder()
                .storeId(99L)
                .build();

        when(storeRepository.findByIdOrElseThrow(anyLong(), any()))
                .thenThrow(new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        // When & Then
        assertThrows(ApplicationException.class, () -> couponService.createCoupon(request));
        verify(storeRepository).findByIdOrElseThrow(anyLong(), any());
    }

    @Test
    void 쿠폰_수정_성공() {
        // Given
        Coupon coupon = Coupon.builder()
                .name("기존 쿠폰")
                .totalQuantity(100)
                .discountAmount(BigDecimal.valueOf(1000))
                .startDate(LocalDateTime.of(2025, 4, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 5, 1, 0, 0))
                .expiryDate(LocalDateTime.of(2025, 6, 30, 0, 0))
                .store(store)
                .build();

        ReflectionTestUtils.setField(coupon, "id", 1L);

        when(couponRepository.findByIdOrElseThrow(1L, ErrorCode.COUPON_NOT_FOUND)).thenReturn(coupon);
        when(storeRepository.findByIdOrElseThrow(1L, ErrorCode.STORE_NOT_FOUND)).thenReturn(store);
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponUpdateRequest request = CouponUpdateRequest.builder()
                .name("수정된 쿠폰")
                .newTotalQuantity(200)
                .endDate(LocalDateTime.of(2025, 6, 1, 0, 0))
                .storeId(1L)
                .build();

        // When
        CouponResponse response = couponService.updateCoupon(1L, request);

        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepository).save(captor.capture());
        Coupon savedCoupon = captor.getValue();

        // Then
        assertNotNull(response);
        assertEquals("수정된 쿠폰", savedCoupon.getName());
        assertEquals(200, savedCoupon.getTotalQuantity());
        assertEquals(LocalDateTime.of(2025, 6, 1, 0, 0), savedCoupon.getEndDate());
    }

    @Test
    void 쿠폰_수정_실패_쿠폰_없음() {
        // Given
        CouponUpdateRequest request = CouponUpdateRequest.builder()
                .storeId(1L)
                .build();

        when(couponRepository.findByIdOrElseThrow(anyLong(), any()))
                .thenThrow(new ApplicationException(ErrorCode.COUPON_NOT_FOUND));

        // When & Then
        assertThrows(ApplicationException.class, () -> couponService.updateCoupon(1L, request));
        verify(couponRepository).findByIdOrElseThrow(anyLong(), any());
    }

    @Test
    void 쿠폰_삭제_성공() {
        // Given
        Coupon coupon = Coupon.builder().store(store).build();
        ReflectionTestUtils.setField(coupon, "id", 1L);

        when(couponRepository.findByIdOrElseThrow(1L, ErrorCode.COUPON_NOT_FOUND)).thenReturn(coupon);
        when(storeRepository.findByIdOrElseThrow(1L, ErrorCode.STORE_NOT_FOUND)).thenReturn(store);

        // When
        couponService.deleteCoupon(1L);

        // Then
        assertNotNull(coupon.getDeletedAt());
        verify(couponRepository, never()).delete(any());
    }

    @Test
    void 쿠폰_삭제_실패_쿠폰_없음() {
        // Given
        when(couponRepository.findByIdOrElseThrow(anyLong(), any()))
                .thenThrow(new ApplicationException(ErrorCode.COUPON_NOT_FOUND));

        // When & Then
        assertThrows(ApplicationException.class, () -> couponService.deleteCoupon(1L));
        verify(couponRepository).findByIdOrElseThrow(anyLong(), any());
    }
}
