package com.couponmoa.backend.common.emailSender.service;

import com.couponmoa.backend.common.emailSender.SqsProperties;
import com.couponmoa.backend.common.emailSender.dto.SendToMQDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class SqsService {
    private final SqsTemplate sqsTemplate;
    private final SqsProperties sqsProperties;
    private String queueUrl;

    public void sendMessage(SendToMQDto message) {
        queueUrl = sqsProperties.getEmailAlert();
        sqsTemplate.send(queueUrl, message);
    }
}
