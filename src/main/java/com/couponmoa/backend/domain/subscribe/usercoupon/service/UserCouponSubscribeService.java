package com.couponmoa.backend.domain.subscribe.usercoupon.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.emailSender.dto.SendToMQDto;
import com.couponmoa.backend.domain.emailSender.service.SqsService;
import com.couponmoa.backend.domain.subscribe.usercoupon.dto.response.FindCouponSubscribeListResponse;
import com.couponmoa.backend.domain.subscribe.usercoupon.entity.UserCouponSubscribe;
import com.couponmoa.backend.domain.subscribe.usercoupon.repository.UserCouponSubscribeRepository;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.couponmoa.backend.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserCouponSubscribeService {

    private final UserRepository userRepo;
    private final CouponRepository couponRepo;
    private final UserCouponSubscribeRepository userCouponSubRepo;
    private final SqsService sqsService;

    @Transactional
    public void subscribeCoupon(Long userId, Long couponId) {
        Coupon coupon = getCoupon(couponId);
        User user = getUser(userId);

        if (userCouponSubRepo.existsByUserAndCoupon(user, coupon)) {
            throw new ApplicationException(DUPLICATED_USER_COUPON);
        }

        UserCouponSubscribe userCouponSubscribe = new UserCouponSubscribe(user, coupon);
        userCouponSubRepo.save(userCouponSubscribe);
    }

    @Transactional
    public void unSubscribeCoupon(Long userId, Long couponId) {
        Coupon coupon = getCoupon(couponId);
        User user = getUser(userId);

        UserCouponSubscribe userCouponSubscribe = userCouponSubRepo.findByUserAndCoupon(user, coupon).orElseThrow(() -> new ApplicationException(USER_COUPON_NOT_FOUND));

        userCouponSubRepo.delete(userCouponSubscribe);
    }

    @Transactional(readOnly = true)
    public List<FindCouponSubscribeListResponse> findSubscribeList(Long userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        User user = getUser(userId);

        return userCouponSubRepo.findByUser(user, pageable)
                .stream()
                .map(FindCouponSubscribeListResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> sendAlert(Long couponId) {
        Coupon coupon = couponRepo.findByIdOrElseThrow(couponId, USER_COUPON_NOT_FOUND);
        List<User> userList = userCouponSubRepo.findByCoupon_Id(couponId)
                .stream()
                .map(UserCouponSubscribe::getUser)
                .toList();

        List<String> emailList = userList.stream()
                .map(User::getEmail)
                .toList();

        if (emailList.isEmpty()) {
            throw new ApplicationException(NO_SUBSCRIBER);
        }

        SendToMQDto message = new SendToMQDto(
                emailList,
                "쿠폰 갱신 안내",
                coupon.getName(),
                "쿠폰이 새로 발행되었습니다!");

        sqsService.sendMessage(message);

        return emailList;
    }

    private User getUser(Long userId) {
        return userRepo.findByIdOrElseThrow(userId, USER_NOT_FOUND);
    }

    private Coupon getCoupon(Long couponId) {
        return couponRepo.findByIdOrElseThrow(couponId, COUPON_NOT_FOUND);
    }
}
