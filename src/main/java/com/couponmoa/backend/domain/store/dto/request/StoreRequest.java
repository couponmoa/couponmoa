package com.couponmoa.backend.domain.store.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class StoreRequest {

    private String name;
    private String description;
    private String address;

    public StoreRequest(String name, String description, String address) {
        this.name = name;
        this.description = description;
        this.address = address;
    }
}
