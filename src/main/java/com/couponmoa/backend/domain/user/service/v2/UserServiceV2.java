package com.couponmoa.backend.domain.user.service.v2;

import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.user.dto.response.UserResponse;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceV2 {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse findUser(Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId, ErrorCode.USER_NOT_FOUND);
        return UserResponse.fromEntityV2(user);
    }
}
