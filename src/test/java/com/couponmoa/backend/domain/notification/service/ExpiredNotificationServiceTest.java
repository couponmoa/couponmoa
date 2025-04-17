package com.couponmoa.backend.domain.notification.service;

import com.couponmoa.backend.common.emailSender.dto.SendToMQDto;
import com.couponmoa.backend.common.emailSender.service.SqsService;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.notification.entity.Notification;
import com.couponmoa.backend.domain.notification.repository.NotificationJdbcRepository;
import com.couponmoa.backend.domain.notification.repository.NotificationRepository;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import org.jobrunr.jobs.lambdas.JobLambda;
import org.jobrunr.scheduling.JobScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpiredNotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationJdbcRepository notificationJdbcRepository;

    @Mock
    private SqsService sqsService;

    @Mock
    private JobScheduler jobScheduler;

    @InjectMocks
    private ExpiredNotificationService expiredNotificationService;

    @Test
    void 만료_전_알림_생성에_성공() {
        Coupon coupon = mock(Coupon.class);
        given(coupon.getExpiryDate()).willReturn(LocalDateTime.now().plusDays(2));
        UserCoupon userCoupon = mock(UserCoupon.class);
        given(userCoupon.getCoupon()).willReturn(coupon);

        expiredNotificationService.createCouponExpireNotification(userCoupon);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void 만료_전_알림_생성_불가() {
        Coupon coupon = mock(Coupon.class);
        given(coupon.getExpiryDate()).willReturn(LocalDateTime.now().plusHours(4));
        UserCoupon userCoupon = mock(UserCoupon.class);
        given(userCoupon.getCoupon()).willReturn(coupon);

        expiredNotificationService.createCouponExpireNotification(userCoupon);

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void 만료_알림_큐에_등록() {
        Coupon coupon = mock(Coupon.class);
        given(coupon.getName()).willReturn("테스트쿠폰");

        UserCoupon userCoupon = mock(UserCoupon.class);
        given(userCoupon.getCoupon()).willReturn(coupon);

        Notification notification = mock(Notification.class);
        given(notification.getUserCoupon()).willReturn(userCoupon);
        ReflectionTestUtils.setField(notification, "id", 1L);

        List<Notification> notifications = List.of(notification);

        given(notificationRepository.findNotificationsExpireTomorrow(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(notifications);

        expiredNotificationService.sendExpireCouponNotifications();

        verify(jobScheduler, times(1)).enqueue(any(JobLambda.class));
    }

    @Test
    void 만료_알림_SQS_전송_성공() {
        List<Long> notificationIds = List.of(1L, 2L);

        User user1 = mock(User.class);
        given(user1.getEmail()).willReturn("test1@test.com");
        User user2 = mock(User.class);
        given(user2.getEmail()).willReturn("test2@test.com");

        UserCoupon userCoupon1 = mock(UserCoupon.class);
        given(userCoupon1.getUser()).willReturn(user1);
        UserCoupon userCoupon2 = mock(UserCoupon.class);
        given(userCoupon2.getUser()).willReturn(user2);

        Notification noti1 = mock(Notification.class);
        given(noti1.getUserCoupon()).willReturn(userCoupon1);

        Notification noti2 = mock(Notification.class);
        given(noti2.getUserCoupon()).willReturn(userCoupon2);

        List<Notification> notiList = List.of(noti1, noti2);

        given(notificationRepository.findAllById(notificationIds)).willReturn(notiList);

        doNothing().when(notificationJdbcRepository).updateIsNotified(anyList());

        doNothing().when(sqsService).sendMessage(any(SendToMQDto.class));

        expiredNotificationService.sendGroupedNotification(notificationIds, "name");

        verify(sqsService, times(1)).sendMessage(any(SendToMQDto.class));
    }

    @Test
    void 만료_알림_SQS_전송_실패() {
        List<Long> notificationIds = List.of(1L, 2L);

        User user1 = mock(User.class);
        given(user1.getEmail()).willReturn("test1@test.com");
        User user2 = mock(User.class);
        given(user2.getEmail()).willReturn("test2@test.com");

        UserCoupon userCoupon1 = mock(UserCoupon.class);
        given(userCoupon1.getUser()).willReturn(user1);
        UserCoupon userCoupon2 = mock(UserCoupon.class);
        given(userCoupon2.getUser()).willReturn(user2);

        Notification noti1 = mock(Notification.class);
        given(noti1.getUserCoupon()).willReturn(userCoupon1);

        Notification noti2 = mock(Notification.class);
        given(noti2.getUserCoupon()).willReturn(userCoupon2);

        List<Notification> notiList = List.of(noti1, noti2);

        given(notificationRepository.findAllById(notificationIds)).willReturn(notiList);

        doNothing().when(notificationJdbcRepository).updateIsNotified(anyList());

        doThrow(new RuntimeException("fail")).when(sqsService).sendMessage(any(SendToMQDto.class));

        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> expiredNotificationService.sendGroupedNotification(notificationIds, "name"));
        assertEquals(ErrorCode.SQS_SEND_FAILED.getHttpStatus(), exception.getStatus());
    }

}
