package com.couponmoa.backend.domain.couponstats.validation.validator;

import com.couponmoa.backend.domain.couponstats.dto.request.CouponUsageSearchRequest;
import com.couponmoa.backend.domain.couponstats.validation.annotation.ValidDateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, CouponUsageSearchRequest> {
    @Override
    public boolean isValid(CouponUsageSearchRequest request, ConstraintValidatorContext context) {
        LocalDate start = request.getStart();
        LocalDate end = request.getEnd();

        if (start.isAfter(end)) {
            context.buildConstraintViolationWithTemplate("시작 날짜는 종료 날짜보다 클 수 없습니다.")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            return false;
        }

        if (end.isAfter(LocalDate.now().minusDays(1))) {
            context.buildConstraintViolationWithTemplate("종료 날짜는 어제까지의 날짜여야 합니다.")
                    .addPropertyNode("end")
                    .addConstraintViolation();
            return false;
        }

        if (ChronoUnit.DAYS.between(start, end) > 30) {
            context.buildConstraintViolationWithTemplate("날짜 범위는 30일을 초과할 수 없습니다.")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
