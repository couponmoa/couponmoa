package com.couponmoa.backend.domain.subscribe.userstore.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.store.repository.StoreRepository;
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

import java.util.List;

import static com.couponmoa.backend.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserStoreSubscribeService {

    private final UserRepository userRepo;
    private final StoreRepository storeRepo;
    private final UserStoreSubscribeRepository userStoreSubRepo;
    private final JavaMailSender mailSender;

    public void subscribeStore(Long userId, Long storeId) {

        Store store = getStore(storeId);
        User user = getUser(userId);

        if (userStoreSubRepo.existsByUserAndStore(user, store)) {
            throw new ApplicationException(DUPLICATED_USER_COUPON);
        }

        UserStoreSubscribe userCouponSubscribe = new UserStoreSubscribe(user, store);
        userStoreSubRepo.save(userCouponSubscribe).getId();
    }

    public void unSubscribeCoupon(Long userId, Long storeId) {
        Store store = getStore(storeId);
        User user = getUser(userId);

        UserStoreSubscribe userCouponSubscribe = userStoreSubRepo.findByUserAndStore(user, store).orElseThrow(() -> new ApplicationException(NOT_FOUND_STORE));

        userStoreSubRepo.delete(userCouponSubscribe);
    }

    public List<FindStoreSubscribeListResponse> findSubscribeList(Long userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        User user = getUser(userId);

        return userStoreSubRepo.findByUser(user, pageable)
                .stream()
                .map(FindStoreSubscribeListResponse::new)
                .toList();
    }

    public void sendAlert(Long storeId) {
        //가게를 구독한 유저 리스트를 꺼내온다
        List<User> userList = userStoreSubRepo.findByStore_Id(storeId)
                .stream()
                .map(UserStoreSubscribe::getUser).toList();

        List<String> userEmailList = userList.stream()
                .map(User::getEmail)
                .toList();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("psejin1478@gmail.com");
        message.setSubject("이메일 수신 테스트");
        message.setSubject("이메일 본문 테스트");
        mailSender.send(message);
    }

    private User getUser(Long userId) {
        return userRepo.findByIdOrElseThrow(userId, NOT_FOUNT_USER);
    }

    private Store getStore(Long storeId) {
        return storeRepo.findByIdOrElseThrow(storeId, NOT_FOUND_STORE);
    }
}
