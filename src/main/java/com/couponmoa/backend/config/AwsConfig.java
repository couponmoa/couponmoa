package com.couponmoa.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.region.static:ap-northeast-2}")
    private String region;

    @Bean
    public Region awsRegion() {
        return Region.of(region);
    }
}
