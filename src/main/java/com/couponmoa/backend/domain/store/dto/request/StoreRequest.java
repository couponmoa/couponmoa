package com.couponmoa.backend.domain.store.dto.request;

import com.couponmoa.backend.domain.store.enums.StoreCategory;
import lombok.Getter;

@Getter
public class StoreRequest {

    private String name;
    private String description;
    private String address;
    private StoreCategory storeCategory;

}
