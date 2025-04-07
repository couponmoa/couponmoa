package com.couponmoa.backend.domain.user.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.subscribe.usercoupon.entity.UserCouponSubscribe;
import com.couponmoa.backend.domain.subscribe.usercoupon.repository.UserCouponSubscribeRepository;
import com.couponmoa.backend.domain.subscribe.userstore.entity.UserStoreSubscribe;
import com.couponmoa.backend.domain.subscribe.userstore.repository.UserStoreSubscribeRepository;
import com.couponmoa.backend.domain.user.dto.request.UserDeleteRequest;
import com.couponmoa.backend.domain.user.dto.request.UserUpdatePasswordRequest;
import com.couponmoa.backend.domain.user.dto.request.UserUpdateRequest;
import com.couponmoa.backend.domain.user.dto.response.UserResponse;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCouponSubscribeRepository userCouponSubRepo;
    private final UserStoreSubscribeRepository userStoreSubRepo;

    @Transactional(readOnly = true)
    public UserResponse findUser(Long userId) {
        User user = getUserById(userId);
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = getUserById(userId);

        if (userRepository.existsByEmail(userUpdateRequest.getEmail())) {
            throw new ApplicationException(ErrorCode.EMAIL_ALREADY_EXIST);
        }

        user.update(userUpdateRequest.getEmail(), userUpdateRequest.getNickname());
        userRepository.save(user);
    }

    @Transactional
    public void updateUserPassword(Long userId, UserUpdatePasswordRequest userUpdatePasswordRequest) {
        User user = getUserById(userId);

        if (passwordEncoder.matches(userUpdatePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.SAME_PASSWORD);
        }

        checkPasswordMatches(userUpdatePasswordRequest.getOldPassword(), user);
        user.setPassword(passwordEncoder.encode(userUpdatePasswordRequest.getNewPassword()));
    }

    @Transactional
    public void deleteUser(Long userId, UserDeleteRequest userDeleteRequest) {
        User user = getUserById(userId);
        checkPasswordMatches(userDeleteRequest.getPassword(), user);
        user.setDeletedAt(LocalDateTime.now());

        userCouponSubRepo.findByUser(user)
                .stream()
                .map(UserCouponSubscribe::getId)
                .toList()
                .forEach(userCouponSubRepo::deleteById);

        userStoreSubRepo.findByUser(user)
                .stream()
                .map(UserStoreSubscribe::getId)
                .toList()
                .forEach(userStoreSubRepo::deleteById);
    }

    private User getUserById(Long userId) {
        return userRepository.findByIdOrElseThrow(userId, ErrorCode.USER_NOT_FOUND);
    }

    private void checkPasswordMatches(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApplicationException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
