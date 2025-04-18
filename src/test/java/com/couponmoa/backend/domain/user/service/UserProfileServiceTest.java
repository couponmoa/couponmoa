package com.couponmoa.backend.domain.user.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.config.S3Config;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static com.couponmoa.backend.domain.user.enums.UserRole.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private UserProfileService userProfileService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "encodedPassword", "nickname", ROLE_USER);
    }

    @Nested
    class updateUserImage {
        @Test
        void 사용자_조회_실패() {
            Long userId = 1L;
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "profile.jpg",
                    "image/jpeg",
                    "fake image content".getBytes()
            );

            given(userRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class)))
                    .willThrow(new ApplicationException(ErrorCode.USER_NOT_FOUND));

            ApplicationException exception = assertThrows(ApplicationException.class,
                    () -> userProfileService.updateUserImage(userId, file));
            assertEquals(ErrorCode.USER_NOT_FOUND.getHttpStatus(), exception.getStatus());
        }

        @Test
        void 프로필_사진_업로드_실패_aws_서비스_오류() {
            Long userId = 1L;
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "profile.jpg",
                    "image/jpeg",
                    "fake image content".getBytes()
            );

            given(userRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(user);
            given(amazonS3.putObject(any(PutObjectRequest.class))).willThrow(AmazonServiceException.class);

            ApplicationException exception = assertThrows(ApplicationException.class,
                    () -> userProfileService.updateUserImage(userId, file));
            assertEquals(ErrorCode.S3_SERVICE_ERROR.getHttpStatus(), exception.getStatus());
        }

        @Test
        void 프로필_사진_업로드_실패_aws_클라이언트_오류() {
            Long userId = 1L;
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "profile.jpg",
                    "image/jpeg",
                    "fake image content".getBytes()
            );

            given(userRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(user);
            given(amazonS3.putObject(any(PutObjectRequest.class))).willThrow(SdkClientException.class);

            ApplicationException exception = assertThrows(ApplicationException.class,
                    () -> userProfileService.updateUserImage(userId, file));
            assertEquals(ErrorCode.S3_CLIENT_ERROR.getHttpStatus(), exception.getStatus());
        }

        @Test
        void 프로필_사진_업로드_성공() throws IOException {
            Long userId = 1L;
            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "profile.jpg",
                    "image/jpeg",
                    "fake image content".getBytes()
            );

            given(userRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(user);

            userProfileService.updateUserImage(userId, file);

            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    class deleteUserImage {
        @Test
        void 사용자_조회_실패() {
            Long userId = 1L;

            given(userRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class)))
                    .willThrow(new ApplicationException(ErrorCode.USER_NOT_FOUND));

            ApplicationException exception = assertThrows(ApplicationException.class,
                    () -> userProfileService.deleteUserImage(userId));
            assertEquals(ErrorCode.USER_NOT_FOUND.getHttpStatus(), exception.getStatus());
        }

        @Test
        void 프로필_사진_삭제_성공() {
            Long userId = 1L;
            given(userRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(user);

            userProfileService.deleteUserImage(userId);

            verify(userRepository, times(1)).save(any(User.class));
        }
    }
}
