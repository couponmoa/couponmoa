package com.couponmoa.backend.domain.user.service.v2;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.user.dto.response.UserResponse;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.couponmoa.backend.domain.user.enums.UserRole.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceV2Test {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceV2 userServiceV2;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "encodedPassword", "nickname", ROLE_USER);
    }

    @Nested
    class findUser {
        @Test
        void 사용자_조회_실패() {
            Long userId = 1L;

            given(userRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class)))
                    .willThrow(new ApplicationException(ErrorCode.USER_NOT_FOUND));

            ApplicationException exception = assertThrows(ApplicationException.class, () -> userServiceV2.findUser(userId));
            assertEquals(ErrorCode.USER_NOT_FOUND.getHttpStatus(), exception.getStatus());
        }

        @Test
        void 사용자_조회_성공() {
            Long userId = 1L;

            given(userRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(user);

            UserResponse userResponse = userServiceV2.findUser(userId);

            assertEquals(user.getEmail(), userResponse.getEmail());
            assertEquals(user.getNickname(), userResponse.getNickname());
            assertEquals(user.getImageKey(), "image/default.jpg");
        }
    }
}
