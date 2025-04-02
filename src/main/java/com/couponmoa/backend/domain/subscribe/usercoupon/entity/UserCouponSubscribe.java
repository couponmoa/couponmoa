package com.couponmoa.backend.domain.subscribe.usercoupon.entity;

import com.couponmoa.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class UserCouponSubscribe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_subscribe_id")
    private Long id;
}
