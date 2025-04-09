package com.couponmoa.backend.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {
    String value();
    long time() default 10;
}
