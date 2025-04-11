package com.couponmoa.backend.domain.emailSender.service;

import com.couponmoa.backend.domain.emailSender.dto.SendToMQDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class SqsService {
    private final SqsTemplate sqsTemplate;

    @Value("${cloud.aws.sqs.queue.email-alert}")
    private String queueUrl;

    public void sendMessage(SendToMQDto message) {
        sqsTemplate.send(queueUrl, message);
    }
}
