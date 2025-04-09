package com.couponmoa.backend.notification.service;

import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationSendService {

    private final JavaMailSender mailSender;

    // 만료 전 알림 전송
    @Transactional
    public void sendExpireCouponNotifications(List<Notification> notifications) {
        // 쿠폰 이름을 기준으로 알림 리스트 묶음
        Map<String, List<Notification>> grouped = notifications.stream()
                .collect(Collectors.groupingBy(n -> n.getUserCoupon().getCoupon().getName()));

        for (Map.Entry<String, List<Notification>> entry : grouped.entrySet()) {
            String couponName = entry.getKey();
            List<Notification> notiList = entry.getValue();

            // 메일 전송에 필요한 정보
            List<User> users = notiList.stream().map(n -> n.getUserCoupon().getUser()).toList();
            String subject = "쿠폰 만료일 하루 전 알림";
            String text = String.format("%s 쿠폰이 하루 뒤 만료됩니다!", couponName);

            // 메일 전송
            sendEmail(users, subject, text);

            // isNotified true로 변경. 전송 확인
            notiList.forEach(Notification::setIsNotified);
        }
    }

    // 이메일 전송 공통 로직
    private void sendEmail(List<User> users, String subject, String text) {
        if (users.isEmpty()) return;

        String[] emailArray = users.stream()
                .map(User::getEmail)
                .toArray(String[]::new);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setBcc(emailArray);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
