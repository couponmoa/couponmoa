package com.couponmoa.backend.domain.notification.controller;

import com.couponmoa.backend.common.service.RedisService;
import com.couponmoa.backend.config.JwtUtil;
import com.couponmoa.backend.config.SecurityConfig;
import com.couponmoa.backend.domain.notification.service.ExpiredNotificationService;
import com.couponmoa.backend.domain.notification.service.IssuedNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@Import({SecurityConfig.class, JwtUtil.class})
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RedisService redisService;

    @MockitoBean
    private IssuedNotificationService issuedNotificationService;

    @MockitoBean
    private ExpiredNotificationService expiredNotificationService;

    @Test
    void 쿠폰_만료_알림_실행() throws Exception {
        willDoNothing().given(expiredNotificationService).sendExpireCouponNotifications();

        mockMvc.perform(post("/api/v1/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    void 알림_상태_변경() throws Exception {
        Long userId = 1L;
        willDoNothing().given(issuedNotificationService).markAsNotified(userId);

        mockMvc.perform(post("/api/v1/notifications/{id}/notified",userId))
                .andExpect(status().isOk());
    }
}
