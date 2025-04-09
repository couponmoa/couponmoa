package com.couponmoa.backend.scheduler.notification;

import com.couponmoa.backend.notification.entity.Notification;
import com.couponmoa.backend.notification.service.NotificationSendService;
import com.couponmoa.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final NotificationSendService notificationSendService;

    @Scheduled(cron = "0 0 0 * * *")
    public void expireCouponNotifications() {
        List<Notification> notifications = notificationService.findNotificationsExpireTomorrow();
        notificationSendService.sendExpireCouponNotifications(notifications);
    }
}
