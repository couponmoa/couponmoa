package com.couponmoa.backend.domain.usercoupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCouponRedisService {

    private static final String COUPON_KEY_PREFIX = "coupon:";
    private static final String COUPON_STOCK_SUFFIX = ":stock";

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Long> couponIssueScript;

    public Integer couponIssue(Long userId, Long couponId) {
        String stockKey = COUPON_KEY_PREFIX + couponId + COUPON_STOCK_SUFFIX;
        String userSetKey = COUPON_KEY_PREFIX + couponId;
        Long resultCode = redisTemplate.execute(couponIssueScript, List.of(stockKey, userSetKey), String.valueOf(userId));
        return Math.toIntExact(resultCode);
    }
}
