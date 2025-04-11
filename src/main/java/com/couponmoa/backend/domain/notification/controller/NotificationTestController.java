package com.couponmoa.backend.domain.notification.controller;

import com.couponmoa.backend.scheduler.notification.NotificationScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/notification")
public class NotificationTestController {

    private final NotificationScheduler notificationScheduler;

    @GetMapping("/expire")
    public String testExpireNotification() {
        notificationScheduler.expireCouponNotifications();
        return "스케줄러 수동 실행 완료";
    }
}
