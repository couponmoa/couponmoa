package com.couponmoa.backend.common.aop;

import com.couponmoa.backend.common.annotation.DistributedLock;
import com.couponmoa.backend.common.annotation.LockKey;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(distributedLock)")
    public Object distributedLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Integer keyIndex = findLockKeyParameterIndex(signature);

        String key = distributedLock.value() + ":" + joinPoint.getArgs()[keyIndex];
        RLock lock = redissonClient.getFairLock(key);
        if (lock.tryLock(distributedLock.time(), TimeUnit.SECONDS)) {
            try {
                return joinPoint.proceed();
            } finally {
                lock.unlock();
            }
        } else {
            throw new ApplicationException(ErrorCode.LOCK_TIMEOUT);
        }
    }

    private Integer findLockKeyParameterIndex(MethodSignature signature) {
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof LockKey) {
                    return i;
                }
            }
        }
        throw new IllegalStateException("락 키가 설정되지 않았습니다.");
    }
}
