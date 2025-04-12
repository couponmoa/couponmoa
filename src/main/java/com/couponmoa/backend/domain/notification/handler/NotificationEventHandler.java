package com.couponmoa.backend.domain.notification.handler;

import com.couponmoa.backend.domain.emailSender.dto.CouponAlertDto;
import com.couponmoa.backend.domain.emailSender.service.SqsService;
import com.couponmoa.backend.domain.notification.event.CouponIssuedEvent;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final SqsService sqsService;

    @Async("threadPoolTaskExecutor") // 트랜잭션 커밋 후 별도 쓰레드에서 비동기 처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CouponIssuedEvent event) {
        sqsService.sendMessage(createCouponAlertDto(event.getUserId(), event.getUserCoupon(), event.getNotificationId()));
    }

    // sse 전송에 필요한 큐 dto 생성
    private CouponAlertDto createCouponAlertDto(Long userId, UserCoupon userCoupon, Long notificationId) {
        return new CouponAlertDto(userId, userCoupon.getCoupon().getName() + "쿠폰이 발급되었습니다!", notificationId);
    }
}
