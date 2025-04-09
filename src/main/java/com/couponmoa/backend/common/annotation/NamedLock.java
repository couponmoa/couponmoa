package com.couponmoa.backend.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NamedLock {
    String value();
    int time() default 10;
}
