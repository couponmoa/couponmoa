package com.couponmoa.backend.domain.usercoupon.service;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.notification.service.ExpiredNotificationService;
import com.couponmoa.backend.domain.notification.service.IssuedNotificationService;
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
import static org.mockito.Mockito.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserCouponAsyncServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private UserCouponRepository userCouponRepository;
    @Mock
    private UserCouponRedisService userCouponRedisService;
    @Mock
    private IssuedNotificationService issuedNotificationService;
    @Mock
    private ExpiredNotificationService expiredNotificationService;
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

            verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CouponIssueTests {

        private static final long userId = 1L;
        private static final Coupon coupon = mock();

        @Test
        @Order(1)
        void 쿠폰_발급_실패() {
            given(userCouponRedisService.couponIssue(anyLong(), anyLong())).willReturn(1);

            userCouponAsyncService.couponIssue(userId, coupon);

            verify(userCouponRepository, times(0)).save(any(UserCoupon.class));
            verify(issuedNotificationService, times(0)).createIssuedNotification(anyLong(), any(UserCoupon.class));
            verify(expiredNotificationService, times(0)).createCouponExpireNotification(any(UserCoupon.class));
        }

        @Test
        @Order(2)
        void 쿠폰_발급_성공() {
            given(userCouponRedisService.couponIssue(anyLong(), anyLong())).willReturn(0);
            given(userRepository.getReferenceById(anyLong())).willReturn(mock(User.class));
            given(userCouponRepository.save(any(UserCoupon.class))).willReturn(mock(UserCoupon.class));

            userCouponAsyncService.couponIssue(userId, coupon);

            verify(issuedNotificationService, times(1)).createIssuedNotification(anyLong(), any(UserCoupon.class));
            verify(expiredNotificationService, times(1)).createCouponExpireNotification(any(UserCoupon.class));
        }
    }
}