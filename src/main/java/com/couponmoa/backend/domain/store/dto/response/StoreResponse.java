package com.couponmoa.backend.domain.store.dto.response;

import lombok.Getter;

@Getter
public class StoreResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final String address;

    public StoreResponse(Long id, String name, String description, String address) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
    }
}
