package com.couponmoa.backend.domain.usercoupon.service;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.domain.usercoupon.repository.UserCouponRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserCouponAsyncServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private UserCouponRepository userCouponRepository;
    @InjectMocks
    private UserCouponAsyncService userCouponAsyncService;

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class SaveUserCouponTests {

        private static final long userId = 1L;
        private static final long couponId = 1L;

        @Test
        @Order(1)
        void 사용자_쿠폰_저장_성공() {
            given(userRepository.getReferenceById(anyLong())).willReturn(mock(User.class));
            given(couponRepository.getReferenceById(anyLong())).willReturn(mock(Coupon.class));

            userCouponAsyncService.saveUserCoupon(userId, couponId);

            verify(userCouponRepository).save(any(UserCoupon.class));
        }
    }
}