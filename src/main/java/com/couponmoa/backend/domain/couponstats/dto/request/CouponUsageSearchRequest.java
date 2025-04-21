package com.couponmoa.backend.domain.couponstats.dto.request;

import com.couponmoa.backend.domain.couponstats.validation.annotation.ValidDateRange;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@ValidDateRange
@AllArgsConstructor
public class CouponUsageSearchRequest {
    private final LocalDate start;
    private final LocalDate end;

    public LocalDate getStart() {
        if (start != null) return start;
        if (end != null) return end.minusDays(6);
        return LocalDate.now().minusDays(7);
    }

    public LocalDate getEnd() {
        if (end != null) return end;
        if (start != null) {
            LocalDate calculated = start.plusDays(6);
            LocalDate yesterday = LocalDate.now().minusDays(1);
            return calculated.isBefore(yesterday) ? calculated : yesterday;
        }
        return LocalDate.now().minusDays(1);
    }
}
