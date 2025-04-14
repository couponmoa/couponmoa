package com.couponmoa.backend;

import com.couponmoa.backend.common.emailSender.SqsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableCaching
@EnableConfigurationProperties(SqsProperties.class)
public class CouponmoaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponmoaApplication.class, args);
    }

}
