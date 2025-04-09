package com.couponmoa.backend.notification.entity;

import com.couponmoa.backend.common.entity.BaseEntity;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.notification.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contents;

    @NotNull
    private boolean isNotified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserCoupon userCoupon;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public Notification(String contents, boolean isNotified, UserCoupon userCoupon, NotificationType type) {
        this.contents = contents;
        this.isNotified = isNotified;
        this.userCoupon = userCoupon;
        this.type = type;
    }
}
