package com.couponmoa.backend.domain.subscribe.usercoupon.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
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
@Transactional
public class UserCouponSubscribeService {

    private final UserRepository userRepo;
    private final CouponRepository couponRepo;
    private final UserCouponSubscribeRepository userCouponSubRepo;
    private final JavaMailSender mailSender;

    public void subscribeCoupon(Long userId, Long couponId) {
        Coupon coupon = getCoupon(couponId);
        User user = getUser(userId);

        if (userCouponSubRepo.existsByUserAndCoupon(user, coupon)) {
            throw new ApplicationException(DUPLICATED_USER_COUPON);
        }

        UserCouponSubscribe userCouponSubscribe = new UserCouponSubscribe(user, coupon);
        userCouponSubRepo.save(userCouponSubscribe).getId();
    }

    public void unSubscribeCoupon(Long userId, Long couponId) {
        Coupon coupon = getCoupon(couponId);
        User user = getUser(userId);

        UserCouponSubscribe userCouponSubscribe = userCouponSubRepo.findByUserAndCoupon(user, coupon).orElseThrow(() -> new ApplicationException(NOT_FOUNT_USER_COUPON));

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
        Coupon coupon = couponRepo.findByIdOrElseThrow(couponId, NOT_FOUNT_USER_COUPON);
        List<User> userList = userCouponSubRepo.findByCoupon_Id(couponId)
                .stream()
                .map(UserCouponSubscribe::getUser)
                .toList();

        List<String> emailList = userList.stream()
                .map(User::getEmail)
                .toList();

        String[] emailArray = userList.stream()
                .map(User::getEmail)
                .toList().toArray(new String[0]);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailArray);
        message.setSubject("쿠폰 갱신 안내");
        message.setText(coupon.getName() + "쿠폰이 새로 발행되었습니다!");
        mailSender.send(message);

        return emailList;
    }

    private User getUser(Long userId) {
        return userRepo.findByIdOrElseThrow(userId, NOT_FOUNT_USER);
    }

    private Coupon getCoupon(Long couponId) {
        return couponRepo.findByIdOrElseThrow(couponId, NOT_FOUNT_COUPON);
    }
}
