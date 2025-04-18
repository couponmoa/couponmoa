package com.couponmoa.backend.domain.coupon.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.config.JwtAuthenticationToken;
import com.couponmoa.backend.domain.coupon.dto.request.CouponCreateRequest;
import com.couponmoa.backend.domain.coupon.dto.request.CouponUpdateRequest;
import com.couponmoa.backend.domain.coupon.dto.response.CouponResponse;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.coupon.service.v2.CouponServiceV2;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceV2Test {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserCouponSubscribeService userCouponSubServ;

    @Mock
    private UserStoreSubscribeService userStoreSubServ;

    @InjectMocks
    private CouponServiceV2 couponServiceV2;

    private Store store;

    @BeforeEach
    void setUp() {
        User user = new User("test@example.com", "password", "nickname", UserRole.ROLE_ADMIN);
        store = new Store(user, "테스트 가게", "가게 설명", "가게 주소");
        ReflectionTestUtils.setField(store, "id", 1L);

        AuthUser authUser = new AuthUser(user.getId(), user.getEmail(), user.getUserRole());
        JwtAuthenticationToken auth = new JwtAuthenticationToken(authUser);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void 쿠폰_생성_성공() throws NoSuchFieldException, IllegalAccessException {
        // Given
        CouponCreateRequest request = CouponCreateRequest.builder()
                .name("테스트 쿠폰")
                .totalQuantity(1000)
                .discountAmount(BigDecimal.valueOf(1000))
                .discountRate(BigDecimal.ZERO)
                .storeId(1L)
                .build();

        when(storeRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(store);
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> {
            Coupon savedCoupon = invocation.getArgument(0);
            Field idField = Coupon.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(savedCoupon, 1L);
            return savedCoupon;
        });

        // When
        CouponResponse response = couponServiceV2.createCoupon(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void 쿠폰_수정_성공() {
        // Given
        Coupon coupon = Coupon.builder()
                .name("수정 전 쿠폰")
                .totalQuantity(100)
                .discountAmount(BigDecimal.valueOf(1000))
                .startDate(LocalDateTime.of(2025, 4, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 5, 1, 0, 0))
                .expiryDate(LocalDateTime.of(2025, 6, 30, 0, 0))
                .store(store)
                .build();

        ReflectionTestUtils.setField(coupon, "id", 1L);

        when(couponRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(coupon);
        when(storeRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(store);
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponUpdateRequest request = CouponUpdateRequest.builder()
                .name("수정 이후 쿠폰")
                .newTotalQuantity(200)
                .endDate(LocalDateTime.of(2025, 6, 1, 0, 0))
                .storeId(1L)
                .build();

        // When
        CouponResponse response = couponServiceV2.updateCoupon(1L, request);

        // Mock 레포지토리에 save(update)되는 시점의 인스턴트를 캡쳐, 내부 값 저장.
        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepository).save(captor.capture());
        Coupon savedCoupon = captor.getValue();

        // Then
        assertNotNull(response);
        assertEquals("수정 이후 쿠폰", savedCoupon.getName());
        assertEquals(200, savedCoupon.getTotalQuantity());
        assertEquals(LocalDateTime.of(2025, 6, 1, 0, 0), savedCoupon.getEndDate());

        verify(userCouponSubServ).sendAlert(anyLong());
    }

    @Test
    void 쿠폰_삭제_성공() {
        // Given
        Coupon coupon = Coupon.builder().store(store).build();
        when(couponRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(coupon);
        when(storeRepository.findByIdOrElseThrow(anyLong(), any())).thenReturn(store);

        // When
        couponServiceV2.deleteCoupon(1L);

        // Then
        assertNotNull(coupon.getDeletedAt());
        verify(couponRepository, never()).delete(any()); // 실제 DB에서 삭제되는 건 아님
        verify(couponRepository, times(1)).findByIdOrElseThrow(anyLong(), any());
    }

    @Test
    void 쿠폰_생성_실패_스토어_없음() {
        // Given
        CouponCreateRequest request = CouponCreateRequest.builder()
                .storeId(1L)
                .build();

        when(storeRepository.findByIdOrElseThrow(anyLong(), any()))
                .thenThrow(new ApplicationException(ErrorCode.STORE_NOT_FOUND));

        // When & Then
        assertThrows(ApplicationException.class, () -> couponServiceV2.createCoupon(request));
        verify(storeRepository, times(1)).findByIdOrElseThrow(anyLong(), any());
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
        assertThrows(ApplicationException.class, () -> couponServiceV2.updateCoupon(1L, request));
        verify(couponRepository, times(1)).findByIdOrElseThrow(anyLong(), any());
    }

    @Test
    void 쿠폰_삭제_실패_쿠폰_없음() {
        // Given
        when(couponRepository.findByIdOrElseThrow(anyLong(), any()))
                .thenThrow(new ApplicationException(ErrorCode.COUPON_NOT_FOUND));

        // When & Then
        assertThrows(ApplicationException.class, () -> couponServiceV2.deleteCoupon(1L));
        verify(couponRepository, times(1)).findByIdOrElseThrow(anyLong(), any());
    }
}