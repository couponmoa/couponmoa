package com.couponmoa.backend.domain.coupon.service;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.dto.request.CouponCreateRequestDto;
import com.couponmoa.backend.domain.coupon.dto.response.CouponResponseDto;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.subscribe.usercoupon.service.UserCouponSubscribeService;
import com.couponmoa.backend.domain.subscribe.userstore.service.UserStoreSubscribeService;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CouponServiceTest {

    @Mock private CouponRepository couponRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private UserStoreSubscribeService userStoreSubscribeService;

    @InjectMocks private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUser authUser = new AuthUser(1L, "admin@example.com", UserRole.ROLE_USER);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authUser, "", authUser.getAuthorities()));
    }

    @Test
    void 쿠폰_생성_성공() {
        // given
        CouponCreateRequestDto request = new CouponCreateRequestDto(
                "테스트용_할인_쿠폰", 100, BigDecimal.valueOf(1000), BigDecimal.ZERO,
                BigDecimal.valueOf(0), BigDecimal.valueOf(10000), "테스트를_위해_생성된_쿠폰입니다.",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                1L
        );

        // User mock 객체 생성
        User mockUser = new User("admin@example.com", "password", "nickname", UserRole.ROLE_USER);

        // Store mock 객체 생성 및 설정
        Store mockStore = mock(Store.class);
        when(mockStore.getUser()).thenReturn(mockUser);

        when(storeRepository.findByIdOrElseThrow(1L, ErrorCode.STORE_NOT_FOUND)).thenReturn(mockStore);

        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> {
            Coupon savedCoupon = invocation.getArgument(0);

            try {
                Field idField = Coupon.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(savedCoupon, 1L);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return savedCoupon;
        });

        // when
        ApiResponse<CouponResponseDto> result = couponService.createCoupon(request);

        // then
        assertThat(result.getData().getId()).isNotNull();
    }

    @Test
    void 쿠폰_생성_실패_할인로직_검증_오류() {
        // given
        CouponCreateRequestDto request = new CouponCreateRequestDto(
                "테스트용_할인_쿠폰", 100,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(0), BigDecimal.valueOf(2000),
                "할인로직_예외_발생해야한다", LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                1L
        );

        User mockUser = new User("admin@example.com", "password", "nickname", UserRole.ROLE_USER);
        Store mockStore = mock(Store.class);
        when(mockStore.getUser()).thenReturn(mockUser);

        when(storeRepository.findByIdOrElseThrow(1L, ErrorCode.STORE_NOT_FOUND)).thenReturn(mockStore);

        // when & then
        assertThrows(ApplicationException.class, () ->
                couponService.createCoupon(request)
        );
    }

    @Test
    void 쿠폰_삭제_성공() {
        // given
        User user = new User("admin@example.com", "password", "닉네임",UserRole.ROLE_USER); // User 생성자 필요
        Store store = new Store(user, "가게이름", "설명", "주소");         // Store 생성자 사용

        Coupon coupon = Coupon.builder()
                .name("테스트 쿠폰")
                .totalQuantity(100)
                .discountAmount(BigDecimal.valueOf(1000))
                .discountRate(BigDecimal.ZERO)
                .minOrderAmount(BigDecimal.valueOf(5000))
                .maxDiscountAmount(BigDecimal.valueOf(1000))
                .description("쿠폰 설명")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .expiryDate(LocalDateTime.now().plusDays(30))
                .store(store) // 꼭 넣어줘야 함!
                .status(CouponStatus.UPCOMING)
                .build();

        when(couponRepository.findByIdOrElseThrow(1L, ErrorCode.COUPON_NOT_FOUND)).thenReturn(coupon);

        // mock 인증 유저 (SecurityContext)
        AuthUser authUser = new AuthUser(user.getId(), user.getEmail(), user.getUserRole());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(authUser, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        when(storeRepository.findByIdOrElseThrow(store.getId(), ErrorCode.STORE_NOT_FOUND)).thenReturn(store);

        // when
        couponService.deleteCoupon(1L);

        // then
        verify(couponRepository).findByIdOrElseThrow(1L, ErrorCode.COUPON_NOT_FOUND);
    }
}
