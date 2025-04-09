package com.couponmoa.backend.notification.service;

import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.notification.entity.Notification;
import com.couponmoa.backend.notification.enums.NotificationType;
import com.couponmoa.backend.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 만료 알림 생성
    @Transactional
    public void createCouponExpireNotification(UserCoupon userCoupon) {
        LocalDateTime notifyAvailableTime = userCoupon.getCoupon().getExpiryDate().minusDays(1);

        // 발급받은 쿠폰의 만료일이 하루 이상 남은 경우 스케줄러를 통해 전송하기 위해 알림 저장
        if (notifyAvailableTime.isAfter(LocalDateTime.now())) {
            Notification notification = new Notification(
                    false,
                    userCoupon,
                    NotificationType.EXPIRE_SOON
            );
            notificationRepository.save(notification);
        }
    }

    // 다음날에 만료되는 쿠폰 조회
    @Transactional(readOnly = true)
    public List<Notification> findNotificationsExpireTomorrow() {
        LocalDate tomorrowDate = LocalDate.now().plusDays(1);

        LocalDateTime start = tomorrowDate.atStartOfDay();
        LocalDateTime end = tomorrowDate.plusDays(1).atStartOfDay();
        return notificationRepository.findNotificationsExpireTomorrow(start,end);
    }

}
