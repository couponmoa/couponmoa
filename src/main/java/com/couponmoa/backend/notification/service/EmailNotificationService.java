package com.couponmoa.backend.notification.service;

import com.couponmoa.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    // 이메일 전송 공통 로직
    @Async("threadPoolTaskExecutor")
    public void sendEmail(List<User> users, String subject, String text) {
        if (users.isEmpty()) return;
        log.info("[Async 시작] 쿠폰 만료 알림 전송 스레드: {}", Thread.currentThread().getName());
        String[] emailArray = users.stream()
                .map(User::getEmail)
                .toArray(String[]::new);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setBcc(emailArray);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        log.info("메일 전송 완료");
    }
}
