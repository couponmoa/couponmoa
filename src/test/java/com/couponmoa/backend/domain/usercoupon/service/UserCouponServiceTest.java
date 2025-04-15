package com.couponmoa.backend.domain.usercoupon.service;

import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import com.couponmoa.backend.domain.coupon.entity.Coupon;
import com.couponmoa.backend.domain.coupon.enums.CouponStatus;
import com.couponmoa.backend.domain.coupon.repository.CouponRepository;
import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.usercoupon.dto.request.UserCouponRequest;
import com.couponmoa.backend.domain.usercoupon.dto.response.UseUserCouponResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponCodeResponse;
import com.couponmoa.backend.domain.usercoupon.dto.response.UserCouponResponse;
import com.couponmoa.backend.domain.usercoupon.entity.UserCoupon;
import com.couponmoa.backend.domain.usercoupon.enums.UserCouponStatus;
import com.couponmoa.backend.domain.usercoupon.repository.UserCouponRepository;
import com.couponmoa.backend.domain.usercoupon.repository.projection.UserCouponProjection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

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
    private CouponRepository couponRepository;
    @Mock
    private UserCouponRepository userCouponRepository;
    @Mock
    private UserCouponRedisService userCouponRedisService;
    @Mock
    private UserCouponAsyncService userCouponAsyncService;
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
                    () -> userCouponService.createUserCouponSync(userId, couponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(2)
        void 쿠폰_발급_IN_PROGRESS_상태_아님_실패() {
            ErrorCode errorCode = ErrorCode.COUPON_NOT_ACTIVE;
            Coupon coupon = mock();
            given(coupon.getStatus()).willReturn(CouponStatus.UPCOMING);
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(coupon);

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.createUserCouponSync(userId, couponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(3)
        void 쿠폰_발급_수량_없음_실패() {
            ErrorCode errorCode = ErrorCode.COUPON_SOLD_OUT;
            Coupon coupon = mock();
            given(coupon.getStatus()).willReturn(CouponStatus.IN_PROGRESS);
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(coupon);
            given(userCouponRedisService.couponIssue(anyLong(), anyLong())).willReturn(3);

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.createUserCouponSync(userId, couponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(4)
        void 쿠폰_발급_중복_요청_실패() {
            ErrorCode errorCode = ErrorCode.DUPLICATED_USER_COUPON;
            Coupon coupon = mock();
            given(coupon.getStatus()).willReturn(CouponStatus.IN_PROGRESS);
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(coupon);
            given(userCouponRedisService.couponIssue(anyLong(), anyLong())).willReturn(2);

            ApplicationException thrown = assertThrows(ApplicationException.class,
                    () -> userCouponService.createUserCouponSync(userId, couponId));

            assertNotNull(thrown);
            assertEquals(errorCode.getMessage(), thrown.getMessage());
            assertEquals(errorCode.getHttpStatus(), thrown.getStatus());
        }

        @Test
        @Order(5)
        void 쿠폰_발급_redis에_쿠폰_재고_등록_안됨_실패() {
            Coupon coupon = mock();
            given(coupon.getStatus()).willReturn(CouponStatus.IN_PROGRESS);
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(coupon);
            given(userCouponRedisService.couponIssue(anyLong(), anyLong())).willReturn(1);

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> userCouponService.createUserCouponSync(userId, couponId));

            assertNotNull(thrown);
        }

        @Test
        @Order(6)
        void 쿠폰_발급_lua_script_예상_못한_값_반환_실패() {
            Coupon coupon = mock();
            given(coupon.getStatus()).willReturn(CouponStatus.IN_PROGRESS);
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(coupon);
            given(userCouponRedisService.couponIssue(anyLong(), anyLong())).willReturn(-1);

            IllegalStateException thrown = assertThrows(IllegalStateException.class,
                    () -> userCouponService.createUserCouponSync(userId, couponId));

            assertNotNull(thrown);
        }

        @Test
        @Order(7)
        void 쿠폰_발급_성공() {
            Coupon coupon = mock();
            given(coupon.getStatus()).willReturn(CouponStatus.IN_PROGRESS);
            given(couponRepository.findActiveByIdOrElseThrow(anyLong(), any(ErrorCode.class))).willReturn(coupon);
            given(userCouponRedisService.couponIssue(anyLong(), anyLong())).willReturn(0);

            userCouponService.createUserCouponSync(userId, couponId);

            verify(userCouponAsyncService, times(1)).saveUserCoupon(anyLong(), anyLong());
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
            List<UserCouponProjection> responseList = List.of(mock(UserCouponProjection.class), mock(UserCouponProjection.class));
            Page<UserCouponProjection> responsePage = new PageImpl<>(responseList, pageable, responseList.size());
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
            given(userCouponRepository.findByCodeWithCouponAndStore(anyString())).willReturn(Optional.empty());

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
            given(userCouponRepository.findByCodeWithCouponAndStore(anyString())).willReturn(Optional.of(userCoupon));

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
            given(userCouponRepository.findByCodeWithCouponAndStore(anyString())).willReturn(Optional.of(userCoupon));

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
            given(userCouponRepository.findByCodeWithCouponAndStore(anyString())).willReturn(Optional.of(userCoupon));

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
            given(userCouponRepository.findByCodeWithCouponAndStore(anyString())).willReturn(Optional.of(userCoupon));

            UseUserCouponResponse result = userCouponService.useUserCoupon(userId, userCouponRequest);

            assertNotNull(result);
            assertEquals(couponName, result.getName());
            assertEquals(userCouponId, result.getId());
        }
    }
}