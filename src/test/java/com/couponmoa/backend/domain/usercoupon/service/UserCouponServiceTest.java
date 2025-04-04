package com.couponmoa.backend.domain.usercoupon.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.repository.UserRepository;
import com.couponmoa.backend.domain.usercoupon.dto.request.UserCouponRequest;
import com.couponmoa.backend.domain.usercoupon.dto.response.UseUserCouponResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponCodeResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponResponse;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus;
import com.couponmoa.backend.domain.usercoupon.repository.UserCouponRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserCouponServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private UserCouponRepository userCouponRepository;
    @InjectMocks
    private UserCouponService userCouponService;

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CreateUserCouponTests {

        private final long userId = 1L;
        private final long couponId = 1L;

        @Test
        @Order(1)
        void 쿠폰_발급_쿠폰_없음_실패() {
            ErrorCode errorCode = ErrorCode.COUPON_NOT_FOUND;
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class)))
                    .willThrow(new ApplicationException(errorCode));

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.createUserCoupon(userId, couponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(2)
        void 쿠폰_발급_기간_전_요청_실패() {
            ErrorCode errorCode = ErrorCode.COUPON_NOT_ACTIVE;
            Coupon coupon = mock();
            given(coupon.getStartDate()).willReturn(LocalDateTime.now().plusDays(1));
            given(coupon.getEndDate()).willReturn(LocalDateTime.now().plusDays(2));
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(coupon);

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.createUserCoupon(userId, couponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(3)
        void 쿠폰_발급_기간_후_요청_실패() {
            ErrorCode errorCode = ErrorCode.COUPON_NOT_ACTIVE;
            Coupon coupon = mock();
            given(coupon.getStartDate()).willReturn(LocalDateTime.now().minusDays(2));
            given(coupon.getEndDate()).willReturn(LocalDateTime.now().minusDays(1));
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(coupon);

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.createUserCoupon(userId, couponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(4)
        void 쿠폰_발급_수량_없음_실패() {
            ErrorCode errorCode = ErrorCode.COUPON_SOLE_OUT;
            Coupon coupon = mock();
            given(coupon.getStartDate()).willReturn(LocalDateTime.now().minusDays(1));
            given(coupon.getEndDate()).willReturn(LocalDateTime.now().plusDays(1));
            given(coupon.getAvailableQuantity()).willReturn(0);
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(coupon);

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.createUserCoupon(userId, couponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(5)
        void 쿠폰_발급_중복_요청_실패() {
            ErrorCode errorCode = ErrorCode.USER_COUPON_ALREADY_ISSUED;
            Coupon coupon = mock();
            given(coupon.getStartDate()).willReturn(LocalDateTime.now().minusDays(1));
            given(coupon.getEndDate()).willReturn(LocalDateTime.now().plusDays(1));
            given(coupon.getAvailableQuantity()).willReturn(100);
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(coupon);
            given(userCouponRepository.existsByUserIdAndCouponId(anyLong(), anyLong())).willReturn(true);

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.createUserCoupon(userId, couponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(6)
        void 쿠폰_발급_성공() {
            User user = mock();
            Coupon coupon = mock();
            given(coupon.getStartDate()).willReturn(LocalDateTime.now().minusDays(1));
            given(coupon.getEndDate()).willReturn(LocalDateTime.now().plusDays(1));
            given(coupon.getAvailableQuantity()).willReturn(100);
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(coupon);
            given(userCouponRepository.existsByUserIdAndCouponId(anyLong(), anyLong())).willReturn(false);
            given(userRepository.getReferenceById(anyLong())).willReturn(user);

            userCouponService.createUserCoupon(userId, couponId);

            verify(coupon, times(1)).availableQuantityDown();
            verify(couponRepository, times(1)).flush();
            verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindUserCouponsTests {

        private final long userId = 1L;
        private final int page = 1;
        private final int size = 10;

        @Test
        @Order(1)
        void 발급받은_쿠폰_목록_조회_성공() {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
            List<UserCouponResponse> responseList = List.of(mock(UserCouponResponse.class), mock(UserCouponResponse.class));
            Page<UserCouponResponse> responsePage = new PageImpl<>(responseList, pageable, responseList.size());
            given(userCouponRepository.findByUserIdAndStatus(anyLong(), any(), any(Pageable.class)))
                    .willReturn(responsePage);

            Page<UserCouponResponse> result = userCouponService.findUserCoupons(userId, null, page, size);

            assertNotNull(result);
            assertEquals(responsePage.getTotalElements(), result.getTotalElements());
            assertEquals(responsePage.getTotalPages(), result.getTotalPages());
            assertEquals(responsePage.getNumber(), result.getNumber());
            assertEquals(responsePage.getSize(), result.getSize());
        }

    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FindUserCouponCodeTests {

        private final long userId = 1L;
        private final long userCouponId = 1L;

        @Test
        @Order(1)
        void 쿠폰_코드_조회_쿠폰_없음_실패() {
            ErrorCode errorCode = ErrorCode.USER_COUPON_NOT_FOUND;
            given(userCouponRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class)))
                    .willThrow(new ApplicationException(errorCode));

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.findUserCouponCode(userId, userCouponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(2)
        void 쿠폰_코드_조회_쿠폰_주인_아님_실패() {
            ErrorCode errorCode = ErrorCode.USER_COUPON_ACCESS_DENIED;
            User user = mock();
            given(user.getId()).willReturn(2L);
            UserCoupon userCoupon = mock();
            given(userCoupon.getUser()).willReturn(user);
            given(userCouponRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(userCoupon);

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.findUserCouponCode(userId, userCouponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(3)
        void 쿠폰_코드_조회_사용된_쿠폰_실패() {
            ErrorCode errorCode = ErrorCode.USER_COUPON_CODE_UNAVAILABLE;
            User user = mock();
            given(user.getId()).willReturn(userId);
            UserCoupon userCoupon = mock();
            given(userCoupon.getUser()).willReturn(user);
            given(userCoupon.getStatus()).willReturn(UserCouponStatus.USED);
            given(userCouponRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(userCoupon);

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.findUserCouponCode(userId, userCouponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(4)
        void 쿠폰_코드_조회_만료된_쿠폰_실패() {
            ErrorCode errorCode = ErrorCode.USER_COUPON_CODE_UNAVAILABLE;
            User user = mock();
            given(user.getId()).willReturn(userId);
            UserCoupon userCoupon = mock();
            given(userCoupon.getUser()).willReturn(user);
            given(userCoupon.getStatus()).willReturn(UserCouponStatus.EXPIRED);
            given(userCouponRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(userCoupon);

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.findUserCouponCode(userId, userCouponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(5)
        void 쿠폰_코드_조회_성공() {
            String code = "code";
            User user = mock();
            given(user.getId()).willReturn(userId);
            UserCoupon userCoupon = mock();
            given(userCoupon.getUser()).willReturn(user);
            given(userCoupon.getStatus()).willReturn(UserCouponStatus.UNUSED);
            given(userCoupon.getCode()).willReturn(code);
            given(userCouponRepository.findByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(userCoupon);

            UserCouponCodeResponse result = userCouponService.findUserCouponCode(userId, userCouponId);

            assertNotNull(result);
            assertEquals(code, result.getCode());
        }
    }


    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class UseUserCouponTests {

        private final long userId = 1L;
        private final UserCouponRequest userCouponRequest = new UserCouponRequest("code");

        @Test
        @Order(1)
        void 쿠폰_사용_처리_쿠폰_없음_실패() {
            ErrorCode errorCode = ErrorCode.USER_COUPON_NOT_FOUND;
            given(userCouponRepository.findByCode(anyString())).willReturn(Optional.empty());

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.useUserCoupon(userId, userCouponRequest));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(2)
        void 쿠폰_사용_처리_쿠폰_매장_주인_아님_실패() {
            ErrorCode errorCode = ErrorCode.USER_COUPON_ACCESS_DENIED;
            User user = mock();
            given(user.getId()).willReturn(2L);
            Store store = mock();
            given(store.getUser()).willReturn(user);
            Coupon coupon = mock();
            given(coupon.getStore()).willReturn(store);
            UserCoupon userCoupon = mock();
            given(userCoupon.getCoupon()).willReturn(coupon);
            given(userCouponRepository.findByCode(anyString())).willReturn(Optional.of(userCoupon));

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.useUserCoupon(userId, userCouponRequest));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(3)
        void 쿠폰_사용_처리_사용된_쿠폰_실패() {
            ErrorCode errorCode = ErrorCode.USER_COUPON_CODE_UNAVAILABLE;
            User user = mock();
            given(user.getId()).willReturn(userId);
            Store store = mock();
            given(store.getUser()).willReturn(user);
            Coupon coupon = mock();
            given(coupon.getStore()).willReturn(store);
            UserCoupon userCoupon = mock();
            given(userCoupon.getStatus()).willReturn(UserCouponStatus.USED);
            given(userCoupon.getCoupon()).willReturn(coupon);
            given(userCouponRepository.findByCode(anyString())).willReturn(Optional.of(userCoupon));

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.useUserCoupon(userId, userCouponRequest));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(4)
        void 쿠폰_사용_처리_만료된_쿠폰_실패() {
            ErrorCode errorCode = ErrorCode.USER_COUPON_CODE_UNAVAILABLE;
            User user = mock();
            given(user.getId()).willReturn(userId);
            Store store = mock();
            given(store.getUser()).willReturn(user);
            Coupon coupon = mock();
            given(coupon.getStore()).willReturn(store);
            UserCoupon userCoupon = mock();
            given(userCoupon.getStatus()).willReturn(UserCouponStatus.EXPIRED);
            given(userCoupon.getCoupon()).willReturn(coupon);
            given(userCouponRepository.findByCode(anyString())).willReturn(Optional.of(userCoupon));

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.useUserCoupon(userId, userCouponRequest));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(5)
        void 쿠폰_사용_처리_성공() {
            String couponName = "couponName";
            User user = mock();
            given(user.getId()).willReturn(userId);
            Store store = mock();
            given(store.getUser()).willReturn(user);
            Coupon coupon = mock();
            given(coupon.getName()).willReturn(couponName);
            given(coupon.getStore()).willReturn(store);
            long userCouponId = 1L;
            UserCoupon userCoupon = mock();
            given(userCoupon.getId()).willReturn(userCouponId);
            given(userCoupon.getStatus()).willReturn(UserCouponStatus.UNUSED);
            given(userCoupon.getCoupon()).willReturn(coupon);
            given(userCouponRepository.findByCode(anyString())).willReturn(Optional.of(userCoupon));

            UseUserCouponResponse result = userCouponService.useUserCoupon(userId, userCouponRequest);

            assertNotNull(result);
            assertEquals(couponName, result.getName());
            assertEquals(userCouponId, result.getId());
        }
    }
}