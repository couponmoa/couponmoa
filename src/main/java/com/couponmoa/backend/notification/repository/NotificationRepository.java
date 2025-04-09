package com.couponmoa.backend.notification.repository;

import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.notification.entity.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends BaseRepository<Notification, Long> {

    @Query("""
            SELECT n FROM Notification n
            JOIN FETCH n.userCoupon uc
            JOIN FETCH uc.user
            JOIN FETCH uc.coupon
            WHERE n.isNotified = false
            AND uc.status = 'UNUSED'
            AND n.userCoupon.coupon.expiryDate BETWEEN :start AND :end
            """)
    List<Notification> findNotificationsExpireTomorrow(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
