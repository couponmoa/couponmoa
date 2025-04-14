package com.couponmoa.backend.domain.notification.controller;

import com.couponmoa.backend.domain.notification.service.ExpiredNotificationService;
import com.couponmoa.backend.scheduler.notification.NotificationScheduler;
import lombok.RequiredArgsConstructor;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/notification")
public class NotificationTestController {

    private final JobScheduler jobScheduler;
    private final ExpiredNotificationService expiredNotificationService;

    @GetMapping("/expire")
    public String testExpireNotification() {
        jobScheduler.enqueue(expiredNotificationService::sendExpireCouponNotifications);
        return "JobRunr 백그라운드 잡 등록 완료";
    }
}
