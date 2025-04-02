package com.couponmoa.backend.domain.usercoupon.entity;

import com.couponmoa.backend.common.entity.BaseEntity;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "user_coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "coupon_id", nullable = false)
//    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserCouponStatus status = UserCouponStatus.UNUSED;

    @Column(nullable = false, unique = true)
    private String code = UUID.randomUUID().toString();

//    public UserCoupon(User user, Coupon coupon) {
//        this.user = user;
//        this.coupon = coupon;
//    }
}
