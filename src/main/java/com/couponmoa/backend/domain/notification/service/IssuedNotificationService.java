package com.couponmoa.backend.domain.notification.service;

import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.notification.entity.Notification;
import com.couponmoa.backend.domain.notification.enums.NotificationType;
import com.couponmoa.backend.domain.notification.event.CouponIssuedEvent;
import com.couponmoa.backend.domain.notification.repository.NotificationRepository;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class IssuedNotificationService {

    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 쿠폰 발급 알림 생성 및 전송
    @Transactional
    public void createIssuedNotification(Long userId, UserCoupon userCoupon) {
        Notification notification = new Notification(
                false, userCoupon, NotificationType.ISSUED_COUPON);
        Notification savedNotification = notificationRepository.save(notification);
        eventPublisher.publishEvent(new CouponIssuedEvent(userId, userCoupon, savedNotification.getId()));
    }

    // 알림 상태 변경(아이디별)
    @Transactional
    public void markAsNotified(Long id) {
        Notification notification = notificationRepository.findByIdOrElseThrow(id, ErrorCode.NOTIFICATION_NOT_FOUND);
        notification.markAsNotified();
    }
}
