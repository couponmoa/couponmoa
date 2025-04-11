package com.couponmoa.backend.domain.emailSender;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloud.aws.sqs.queue")
@Getter
@Setter
public class SqsProperties {
    private String emailAlert;
}
