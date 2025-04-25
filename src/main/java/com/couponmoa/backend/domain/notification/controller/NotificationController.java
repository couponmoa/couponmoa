package com.couponmoa.backend.domain.notification.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.notification.service.ExpiredNotificationService;
import com.couponmoa.backend.domain.notification.service.IssuedNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final ExpiredNotificationService expiredNotificationService;
    private final IssuedNotificationService issuedNotificationService;

    // 쿠폰 만료 전 알림 실행(스케줄러 서버에서 호출하는 api)
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> NotifyCouponExpire() {
        expiredNotificationService.sendExpireCouponNotifications();
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 알림 전송 완료시 상태 변경(알림서버에서 호출하는 api)
    @PostMapping("/{id}/notified")
    public ResponseEntity<ApiResponse<Void>> markAsNotified(@PathVariable Long id) {
        issuedNotificationService.markAsNotified(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
