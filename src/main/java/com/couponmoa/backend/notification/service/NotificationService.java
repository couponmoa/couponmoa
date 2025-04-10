package com.couponmoa.backend.notification.service;

import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.notification.entity.Notification;
import com.couponmoa.backend.notification.enums.NotificationType;
import com.couponmoa.backend.notification.repository.NotificationJdbcRepository;
import com.couponmoa.backend.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationJdbcRepository notificationJdbcRepository;
    private final NotificationRepository notificationRepository;
    private final EmailNotificationService emailNotificationService;

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

    // 만료 전 알림 전송
    @Transactional
    public void sendExpireCouponNotifications() {
        List<Notification> notifications = findNotificationsExpireTomorrow();
        log.info("조회된 만료 알림 수: {}", notifications.size());

        // 쿠폰 이름을 기준으로 알림 리스트 묶음
        Map<String, List<Notification>> grouped = notifications.stream()
                .collect(Collectors.groupingBy(n -> n.getUserCoupon().getCoupon().getName()));

        for (Map.Entry<String, List<Notification>> entry : grouped.entrySet()) {
            String couponName = entry.getKey();
            List<Notification> notiList = entry.getValue();

            log.info("처리 중인 쿠폰: {}, 사용자 수: {}", couponName, notiList.size());

            // 메일 전송에 필요한 정보
            List<User> users = notiList.stream().map(n -> n.getUserCoupon().getUser()).toList();
            String subject = "쿠폰 만료일 하루 전 알림";
            String text = String.format("%s 쿠폰이 하루 뒤 만료됩니다!", couponName);

            // 메일 전송
            emailNotificationService.sendEmail(users, subject, text);

            // isNotified true로 변경. 전송 확인
            updateNotificationsAsNotified(notiList);
        }
    }

    // 다음날에 만료되는 쿠폰 조회
    private List<Notification> findNotificationsExpireTomorrow() {
        LocalDate tomorrowDate = LocalDate.now().plusDays(1);

        LocalDateTime start = tomorrowDate.atStartOfDay();
        LocalDateTime end = tomorrowDate.plusDays(1).atStartOfDay();

        return notificationRepository.findNotificationsExpireTomorrow(start, end);
    }

    // 알림 상태 업데이트
    private void updateNotificationsAsNotified(List<Notification> notifications) {
        notificationJdbcRepository.updateIsNotified(notifications);
    }

}
