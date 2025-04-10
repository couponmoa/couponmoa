package com.couponmoa.backend.domain.usercoupon.service;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponRedisService {

    private static final String COUPON_KEY_PREFIX = "coupon:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Long> couponIssueScript;

    public void saveStock(Coupon coupon) {
        String key = COUPON_KEY_PREFIX + coupon.getId() + ":stock";
        String stock = String.valueOf(coupon.getTotalQuantity());
        Duration ttl = Duration.between(coupon.getStartDate(), coupon.getEndDate());
        redisTemplate.opsForValue().set(key, stock, ttl);
    }

    public Long couponIssue(Long userId, Long couponId) {
        String stockKey = COUPON_KEY_PREFIX + couponId + ":stock";
        String userSetKey = COUPON_KEY_PREFIX + couponId;
        return redisTemplate.execute(couponIssueScript, List.of(stockKey, userSetKey), String.valueOf(userId));
    }

    public void deleteUserSet(Long couponId) {
        String key = COUPON_KEY_PREFIX + couponId;
        redisTemplate.delete(key);
    }
}
