package com.couponmoa.backend.scheduler.notification;

import com.couponmoa.backend.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Recurring(id = "coupon-expire-notification-job", cron = "0 0 * * *")
    @Job(name = "Send email notification 1 day before coupon expires", retries = 3)
    public void expireCouponNotifications() {
        notificationService.sendExpireCouponNotifications();
    }
}
