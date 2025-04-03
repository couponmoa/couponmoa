package com.couponmoa.backend.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, String value, Duration duration){
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}
