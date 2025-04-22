package com.couponmoa.backend.domain.notification.handler;

import com.couponmoa.backend.common.emailSender.dto.CouponAlertDto;
import com.couponmoa.backend.common.emailSender.service.SqsService;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.notification.event.CouponIssuedEvent;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationEventHandlerTest {

    @Mock
    private SqsService sqsService;

    @InjectMocks
    private NotificationEventHandler notificationEventHandler;

    @Test
    void 쿠폰_발급_알림_SQS_전송() {
        Coupon coupon = mock(Coupon.class);
        given(coupon.getName()).willReturn("Coupon1");
        UserCoupon userCoupon = mock(UserCoupon.class);
        given(userCoupon.getCoupon()).willReturn(coupon);

        CouponIssuedEvent event = new CouponIssuedEvent(1L,userCoupon,1L);

        doNothing().when(sqsService).sendMessage(any(CouponAlertDto.class));

        notificationEventHandler.handle(event);

        verify(sqsService,times(1)).sendMessage(any(CouponAlertDto.class));
    }
}
