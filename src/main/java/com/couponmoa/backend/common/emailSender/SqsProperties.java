package com.couponmoa.backend.common.emailSender;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "spring.cloud.aws.sqs.queue")
@Getter
@Setter
public class SqsProperties {
    private String emailAlert;
}
