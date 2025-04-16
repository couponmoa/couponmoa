package com.couponmoa.backend.domain.store.dto.request;

public record StoreCursor(
        String keyword,
        Long storeId
) { }
