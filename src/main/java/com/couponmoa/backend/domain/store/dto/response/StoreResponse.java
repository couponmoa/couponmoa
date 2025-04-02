package com.couponmoa.backend.domain.store.dto.response;

import com.couponmoa.backend.domain.store.enums.StoreCategory;
import lombok.Getter;

@Getter
public class StoreResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final String address;
    private final StoreCategory storeCategory;

    public StoreResponse(Long id, String name, String description, String address, StoreCategory storeCategory) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.storeCategory = storeCategory;
    }
}
