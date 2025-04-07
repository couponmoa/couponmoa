package com.couponmoa.backend.domain.subscribe.userstore.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
import com.couponmoa.backend.domain.subscribe.usercoupon.repository.UserCouponSubscribeRepository;
import com.couponmoa.backend.domain.subscribe.userstore.dto.response.FindStoreSubscribeListResponse;
import com.couponmoa.backend.domain.subscribe.userstore.entity.UserStoreSubscribe;
import com.couponmoa.backend.domain.subscribe.userstore.repository.UserStoreSubscribeRepository;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.couponmoa.backend.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserStoreSubscribeService {

    private final UserRepository userRepo;
    private final StoreRepository storeRepo;
    private final UserStoreSubscribeRepository userStoreSubRepo;
    private final UserCouponSubscribeRepository userCouponSubRepo;
    private final JavaMailSender mailSender;

    @Transactional
    public void subscribeStore(Long userId, Long storeId) {

        Store store = getStore(storeId);
        User user = getUser(userId);

        if (userStoreSubRepo.existsByUserAndStore(user, store)) {
            throw new ApplicationException(DUPLICATED_USER_COUPON);
        }

        UserStoreSubscribe userCouponSubscribe = new UserStoreSubscribe(user, store);
        userStoreSubRepo.save(userCouponSubscribe);
    }

    @Transactional
    public void unSubscribeCoupon(Long userId, Long storeId) {
        Store store = getStore(storeId);
        User user = getUser(userId);

        UserStoreSubscribe userCouponSubscribe = userStoreSubRepo.findByUserAndStore(user, store).orElseThrow(() -> new ApplicationException(STORE_NOT_FOUND));

        userStoreSubRepo.delete(userCouponSubscribe);
    }

    @Transactional(readOnly = true)
    public List<FindStoreSubscribeListResponse> findSubscribeList(Long userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        User user = getUser(userId);

        return userStoreSubRepo.findByUser(user, pageable)
                .stream()
                .map(FindStoreSubscribeListResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> sendAlert(Long storeId) {
        Store store = storeRepo.findByIdOrElseThrow(storeId, STORE_NOT_FOUND);

        //가게를 구독한 유저 리스트를 꺼내온다
        List<User> userList = userStoreSubRepo.findByStore_Id(storeId)
                .stream()
                .map(UserStoreSubscribe::getUser).toList();

        if (userList.isEmpty()) {
            return null;
        }

        List<String> emailList = userList.stream()
                .map(User::getEmail)
                .toList();

        String[] emailArray = userList.stream()
                .map(User::getEmail)
                .toList().toArray(new String[0]);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailArray);
        message.setSubject("가게 새 쿠폰 발행 안내");
        message.setText(store.getName() + "에서 새 쿠폰이 발행되었습니다!");
        mailSender.send(message);

        return emailList;
    }

    private User getUser(Long userId) {
        return userRepo.findByIdOrElseThrow(userId, USER_NOT_FOUND);
    }

    private Store getStore(Long storeId) {
        return storeRepo.findByIdOrElseThrow(storeId, STORE_NOT_FOUND);
    }
}
