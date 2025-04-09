package com.couponmoa.backend.common.aop;

import com.couponmoa.backend.common.annotation.LockKey;
import com.couponmoa.backend.common.annotation.NamedLock;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.common.repository.NamedLockRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class NamedLockAspect {

    private final NamedLockRepository namedLockRepository;

    @Around("@annotation(namedLock)")
    public Object namedLock(ProceedingJoinPoint joinPoint, NamedLock namedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Integer keyIndex = findLockKeyParameterIndex(signature);

        String key = namedLock.value() + ":" + joinPoint.getArgs()[keyIndex];
        if (namedLockRepository.acquireLock(key, namedLock.time())) {
            try {
                return joinPoint.proceed();
            } finally {
                namedLockRepository.releaseLock(key);
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
