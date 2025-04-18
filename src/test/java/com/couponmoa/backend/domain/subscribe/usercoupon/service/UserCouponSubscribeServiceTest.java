package com.couponmoa.backend.domain.subscribe.usercoupon.service;

import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.subscribe.usercoupon.repository.UserCouponSubscribeRepository;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import com.couponmoa.backend.domain.usercoupon.repository.UserCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserCouponSubscribeServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private CouponRepository couponRepo;

    @Mock
    private UserCouponRepository userCouponRepo;

    @Mock
    private UserCouponSubscribeRepository userCouponSubRepo;

    @InjectMocks
    private UserCouponSubscribeService userCouponSubServ;

    private User user;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        User user = new User("test@emial.com", "Password1234!", "test nickname", UserRole.ROLE_USER);
        Coupon coupon = Coupon.builder().name("테스트용 쿠폰").build();
        ReflectionTestUtils.setField(coupon, "id", 1L);
    }

    /**
     * 구독 테스트
     */
    @Nested
    class SubscribeCouponTest {
        @Test
        void 쿠폰_구독_성공() {
            given(userRepo.findByIdOrElseThrow(anyLong(), eq(ErrorCode.USER_NOT_FOUND))).willReturn(user);
            given(couponRepo.findByIdOrElseThrow(anyLong(), eq(ErrorCode.COUPON_NOT_FOUND))).willReturn(coupon);
            given(userCouponSubRepo.existsByUserAndCoupon(any(User.class), any(Coupon.class))).willReturn(false);


        }
    }

}