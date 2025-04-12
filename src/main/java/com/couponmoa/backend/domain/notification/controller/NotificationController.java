package com.couponmoa.backend.domain.notification.controller;

import com.couponmoa.backend.common.dto.ApiResponse;
import com.couponmoa.backend.domain.notification.service.NotificationService;
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

    private final NotificationService notificationService;

    // 알림 전송 완료시 상태 변경(알림서버에서 호출하는 api)
    @PostMapping("/notifications/{id}/notified")
    public ResponseEntity<ApiResponse<Void>> markAsNotified(@PathVariable Long id) {
        notificationService.markAsNotified(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
