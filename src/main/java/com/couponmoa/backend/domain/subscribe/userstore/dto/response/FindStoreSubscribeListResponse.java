package com.couponmoa.backend.domain.subscribe.userstore.dto.response;

import com.couponmoa.backend.domain.store.entity.Store;
import com.couponmoa.backend.domain.subscribe.userstore.entity.UserStoreSubscribe;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindStoreSubscribeListResponse {
    private Long id;
    private String name;
    private String description;
    private String address;

    public FindStoreSubscribeListResponse(UserStoreSubscribe userCouponSubscribe) {
        Store store = userCouponSubscribe.getStore();
        this.address = store.getAddress();
        this.description = store.getDescription();
        this.id = store.getId();
        this.name = store.getName();
    }
}
