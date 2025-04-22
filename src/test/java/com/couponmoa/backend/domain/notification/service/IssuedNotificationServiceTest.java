package com.couponmoa.backend.domain.notification.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.notification.entity.Notification;
import com.couponmoa.backend.domain.notification.enums.NotificationType;
import com.couponmoa.backend.domain.notification.event.CouponIssuedEvent;
import com.couponmoa.backend.domain.notification.repository.NotificationRepository;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssuedNotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private IssuedNotificationService issuedNotificationService;

    @Test
    void 쿠폰_발급_알림_생성_및_전송() {
        Long userId = 1L;
        UserCoupon userCoupon = mock(UserCoupon.class);
        Notification notification = new Notification(false, userCoupon, NotificationType.ISSUED_COUPON);

        given(notificationRepository.save(any(Notification.class))).willReturn(notification);
        doNothing().when(eventPublisher).publishEvent(any(CouponIssuedEvent.class));

        issuedNotificationService.createIssuedNotification(userId, userCoupon);

        verify(eventPublisher, times(1)).publishEvent(any(CouponIssuedEvent.class));
    }

    @Test
    void 쿠폰_발급_상태_변경() {
        Long id = 1L;
        Notification notification = new Notification(false, mock(UserCoupon.class), NotificationType.ISSUED_COUPON);

        given(notificationRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(notification);

        issuedNotificationService.markAsNotified(id);

        assertTrue(notification.isNotified());
        verify(notificationRepository, times(1)).findByIdOrElseThrow(anyLong(), any(ErrorCode.class));
    }

    @Test
    void 알림_조회_실패() {
        Long id = 1L;
        given(notificationRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class)))
                .willThrow(new ApplicationException(ErrorCode.NOTIFICATION_NOT_FOUND));

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> issuedNotificationService.markAsNotified(id));
        assertEquals(ErrorCode.NOTIFICATION_NOT_FOUND.getHttpStatus(), exception.getStatus());
    }
}
