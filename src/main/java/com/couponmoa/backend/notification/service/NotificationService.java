package com.couponmoa.backend.notification.service;

import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.notification.entity.Notification;
import com.couponmoa.backend.notification.enums.NotificationType;
import com.couponmoa.backend.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 만료 알림 생성
    public void createCouponExpireNotification(UserCoupon userCoupon) {
        LocalDateTime notifyAvailableTime = userCoupon.getCoupon().getExpiryDate().minusDays(1);

        // 발급받은 쿠폰의 만료일이 하루 이상 남은 경우 스케줄러를 통해 전송하기 위해 알림 저장
        if(notifyAvailableTime.isAfter(LocalDateTime.now())) {
            Notification notification = new Notification(
                    "쿠폰 만료까지 하루 남았습니다!",
                    false,
                    userCoupon,
                    NotificationType.EXPIRE_SOON
            );
            notificationRepository.save(notification);
        }
    }
}
