package com.couponmoa.backend.domain.store.dto.response;

import com.couponmoa.backend.domain.store.entity.Store;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class StoreResponse implements Serializable {

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

    public static StoreResponse toDto(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getDescription(),
                store.getAddress());
    }
}
