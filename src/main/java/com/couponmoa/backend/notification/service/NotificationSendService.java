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

@Service
@RequiredArgsConstructor
public class NotificationSendService {

    private final JavaMailSender mailSender;

    // 만료 전 알림 전송
    @Transactional
    public void sendExpireCouponNotifications(List<Notification> notifications) {
        for (Notification noti : notifications) {
            User user = noti.getUserCoupon().getUser();
            Coupon coupon = noti.getUserCoupon().getCoupon();

            String subject = "쿠폰 만료일 하루 전 알림";
            String text = String.format("%s 쿠폰이 하루 뒤 만료됩니다!", coupon.getName());

            sendEmail(List.of(user), subject, text);

            // isNotified true로 변경. 전송 확인
            noti.setIsNotified();
        }
    }

    // 이메일 전송 공통 로직
    private void sendEmail(List<User> users, String subject, String text) {
        if (users.isEmpty()) return;

        String[] emailArray = users.stream()
                .map(User::getEmail)
                .toArray(String[]::new);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailArray);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
