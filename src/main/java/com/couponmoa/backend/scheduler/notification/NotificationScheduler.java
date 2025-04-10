package com.couponmoa.backend.scheduler.notification;

import com.couponmoa.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * *")
    public void expireCouponNotifications() {
        notificationService.sendExpireCouponNotifications();
    }
}
