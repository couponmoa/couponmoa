package com.couponmoa.backend.domain.usercoupon.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserCouponRedisServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @InjectMocks
    private UserCouponRedisService userCouponRedisService;

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CouponIssueTests {

        private static final long userId = 1L;
        private static final long couponId = 1L;

        @Test
        @Order(1)
        void 쿠폰_발급_스크립트_호출_성공() {
            long resultCode = 0;
            given(redisTemplate.execute(any(), anyList(), anyString())).willReturn(resultCode);

            Integer result = userCouponRedisService.couponIssue(userId, couponId);

            assertEquals(Math.toIntExact(resultCode), result);
        }
    }
}